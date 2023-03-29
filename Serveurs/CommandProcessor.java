import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import CommandHandlers.CommandHandler;
import Database.MessageManager;
import Database.UserManager;
import Database.Models.Message;
import Factory.CommandHandlerFactory;

public class CommandProcessor {

    private final BufferedReader input;
    private final BufferedWriter output;
    private final Map<String, CommandHandlerFactory> commandHandlerFactories;
    private List<ClientHandler> clientHandlers;
    private ClientHandler clientHandler;
    private Connection conn;

    public CommandProcessor(BufferedReader input, BufferedWriter output,
            Map<String, CommandHandlerFactory> commandHandlerFactories, List<ClientHandler> clientHandlers, ClientHandler clientHandler) {
        this.input = input;
        this.output = output;
        this.commandHandlerFactories = commandHandlerFactories;
        this.clientHandlers = clientHandlers;
        this.clientHandler = clientHandler;
    }

    public void processCommands() throws IOException, SQLException {
        while (true) {
            String request = "";
            String line = input.readLine();
            if (line == null) {
                break;
            }

            while (!line.isEmpty()) {
                request += line + "\r\n";
                line = input.readLine();
            }

            String[] parts = request.split(" ", 2);
            String command = parts[0];
            String body = parts.length > 1 ? parts[1] : "";

            if ("SET_REPOST_CLIENT".equals(command.trim())) {
                clientHandler.setIsRepostClient(true);
                continue;
            }

            CommandHandlerFactory commandHandlerFactory = commandHandlerFactories.get(command);
            if (commandHandlerFactory != null) {
                conn = ConnectionPool.getConnection();
                CommandHandler commandHandler = commandHandlerFactory.createCommandHandler(body, output, conn);
                commandHandler.handle();
                
                if (command.equals("PUBLISH")) {
                    MessageManager messageManager = new MessageManager(conn);
                    UserManager userManager = new UserManager(conn);
                    int id = messageManager.getLastMessageId();
                    Message message = messageManager.getMessage(id);
                    String author = userManager.getUserName(message.getUserId());
                    repostMessage(message, author);
                }

                conn.close();
            } else {
                output.write("ERROR\r\n\r\n");
            }

            try {
                output.flush();
            } catch (SocketException e) {
                System.err.println("Client disconnected unexpectedly: " + e.getMessage());
                break;
            }
        }
    }

    private void repostMessage(Message message, String author) throws IOException {
        StringBuilder msgHeader = new StringBuilder(
                "MSG" + " " + "author:" +author + " msg_id:" + message.getId());
        if (message.getReplyToId() != null) {
            msgHeader.append(" reply_to_id:").append(message.getReplyToId());
        } else {
            msgHeader.append(" reply_to_id:").append(-1);
        }
        msgHeader.append(" republished:").append(message.isRepublished());

        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(msgHeader.toString() + "\r\n" + message.getMessage() + "\r\n");
        }
    }
}
