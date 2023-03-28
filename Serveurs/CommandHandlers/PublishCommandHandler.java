package CommandHandlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import Database.MessageManager;

public class PublishCommandHandler implements CommandHandler{
    private static final Logger logger = Logger.getLogger(PublishCommandHandler.class.getName());
    private Connection conn;
    private String body;
    private BufferedWriter output;

    public PublishCommandHandler(String body, BufferedWriter output, Connection conn) {
        this.conn = conn;
        this.body = body;
        this.output = output;
    }

    @Override
    public void handle() throws IOException {
        String author = Util.getAuthorFromBody(body);
        if (author == null) {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            return;
        }

        String message = Util.getMessageFromBody(body);
        if (message == null || message.isEmpty()) {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            return;
        }

        System.out.println("@" + author + " : " + message);
        int messageId = Util.addMessageToDatabase(conn, message, author, null, false);
        if (messageId != -1) {
            // Extract tags from the message
            Set<String> tags = Util.extractTagsFromMessage(message);

            try {
                MessageManager messagesTableManager = new MessageManager(conn);
                // Add tags to the database
                for (String tag : tags) {
                    messagesTableManager.addTag(tag, messageId);
                }
                output.write(Util.OK_RESPONSE + "\r\n\r\n");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error while adding tags to database: " + e.getMessage());
                output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            }
        } else {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
        }
    }
}
