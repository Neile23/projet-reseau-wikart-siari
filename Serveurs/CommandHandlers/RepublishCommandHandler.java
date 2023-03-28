package CommandHandlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import Database.MessageManager;
import Database.Models.Message;

public class RepublishCommandHandler implements CommandHandler{
    private Connection conn;
    private String body;
    private BufferedWriter output;

    public RepublishCommandHandler(String body, BufferedWriter output, Connection conn) {
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
        String msgIdStr = Util.extractValueFromBody(body, Util.MSG_ID_PREFIX);
        if (msgIdStr == null) {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            return;
        }

        int msgId = Integer.parseInt(msgIdStr.trim());

        try {
            MessageManager messagesTableManager = new MessageManager(conn);
            Message originalMessageObj = messagesTableManager.getMessage(msgId);
            if (originalMessageObj == null) {
                output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
                return;
            }

            String message = originalMessageObj.getMessage();
            int newMessageId = Util.addMessageToDatabase(conn, message, author, null, true);
            if (newMessageId != -1) {
                output.write(Util.OK_RESPONSE + "\r\n\r\n");
            } else {
                output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            }
        } catch (SQLException e) {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
        }
    }
}
