import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Factory.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Map<String, CommandHandlerFactory> commandHandlerFactories;
    private List<ClientHandler> clientHandlers;
    private BufferedWriter output;
    BufferedReader input;
    private boolean isRepostClient = false;

    public ClientHandler(Socket socket, List<ClientHandler> clientHandlers) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
        try {
            commandHandlerFactories = new HashMap<>();
            commandHandlerFactories.put("PUBLISH", new PublishCommandHandlerFactory());
            commandHandlerFactories.put("REPLY", new ReplyCommandHandlerFactory());
            commandHandlerFactories.put("REPUBLISH", new RepublishCommandHandlerFactory());
            commandHandlerFactories.put("RCV_IDS", new RcvIdsCommandHandlerFactory());
            commandHandlerFactories.put("RCV_MSG", new RcvMsgCommandHandlerFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            CommandProcessor commandProcessor = new CommandProcessor(input, output, commandHandlerFactories, clientHandlers, this);
            commandProcessor.processCommands();

        } catch (IOException e) {
            if (e.getMessage().contains("Stream closed")) {
                // Remove the current ClientHandler from the clientHandlers list
                synchronized (clientHandlers) {
                    clientHandlers.remove(this);
                }
                System.err.println("ClientHandler closed: " + e.getMessage());
            } else {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Close the socket
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setIsRepostClient(boolean isRepostClient) {
        this.isRepostClient = isRepostClient;
    }

    public boolean isRepostClient() {
        return isRepostClient;
    }

    public void sendMessage(String message) throws IOException {
        if (!isRepostClient) {
            return;
        }
        try {
             // Synchronize on the output object to prevent concurrent writes
            output.write(message + "\r\n");
            output.flush();
        } catch (IOException e) {
            System.err.println("Error sending message to client: " + e.getMessage());
            if (e.getMessage().contains("Stream closed")) {
                // Remove the current ClientHandler from the clientHandlers list
                synchronized (clientHandlers) {
                    clientHandlers.remove(this);
                }
                input.close();
                System.err.println("ClientHandler closed: " + e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }

}
