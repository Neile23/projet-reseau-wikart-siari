package CommandHandlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import database.MessageManager;

public class RcvIdsCommandHandler implements CommandHandler {
    private String body;
    private BufferedWriter output;
    private Connection conn;

    public RcvIdsCommandHandler(String body, BufferedWriter output, Connection conn) {
        this.body = body;
        this.output = output;
        this.conn = conn;
    }

    @Override
    public void handle() throws IOException {
        try {
            MessageManager messagesTableManager = new MessageManager(conn);
            String author = Util.extractValueFromBody(body, Util.AUTHOR_PREFIX);
            author = author == null ? null : author.trim();
            String tag = Util.extractValueFromBody(body, Util.TAG_PREFIX);
            String sinceIdStr = Util.extractValueFromBody(body, Util.SINCE_ID_PREFIX);
            String limitStr = Util.extractValueFromBody(body, Util.LIMIT_PREFIX);
            int sinceId = sinceIdStr == null ? -1 : Integer.parseInt(sinceIdStr.trim());
            int limit = limitStr == null ? Util.DEFAULT_LIMIT : Integer.parseInt(limitStr.trim());
            List<Integer> messageIds = messagesTableManager.getMessageIds(author, tag, sinceId, limit);

            StringBuilder response = new StringBuilder(Util.MSG_IDS_RESPONSE);
            for (int messageId : messageIds) {
                response.append("\r\n").append(messageId);
            }
            output.write(response.toString() + "\r\n\r\n");
        } catch (SQLException e) {
            System.err.println("Error while fetching message IDs: " + e.getMessage());
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
        }
    }
}
