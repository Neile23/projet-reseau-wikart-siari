import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import Factory.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Map<String, CommandHandlerFactory> commandHandlerFactories;

    public ClientHandler(Socket socket) {
        this.socket = socket;
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
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            CommandProcessor commandProcessor = new CommandProcessor(input, output, commandHandlerFactories);
            commandProcessor.processCommands();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
