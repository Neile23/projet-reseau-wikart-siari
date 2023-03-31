package CommandHandlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import database.MessageManager;
import database.UserManager;
import database.Models.Message;

public class RcvMsgCommandHandler implements CommandHandler {
    private final String body;
    private final BufferedWriter output;
    private final Connection conn;

    public RcvMsgCommandHandler(String body, BufferedWriter output, Connection conn) {
        this.body = body;
        this.output = output;
        this.conn = conn;
    }

    @Override
    public void handle() throws IOException {
        String msgIdStr = Util.extractValueFromBody(body, "msg_id:");
        if (msgIdStr == null) {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            return;
        }

        int msgId = Integer.parseInt(msgIdStr.trim());

        try {
            MessageManager messagesTableManager = new MessageManager(conn);
            UserManager usersTableManager = new UserManager(conn);
            Message messageObj = messagesTableManager.getMessage(msgId);
            String message = messageObj.getMessage();
            int userId = messageObj.getUserId();
            String author = usersTableManager.getUserName(userId);
            Integer replyToId = messageObj.getReplyToId();
            boolean republished = messageObj.isRepublished();

            if (message != null && author != null) {
                StringBuilder msgHeader = new StringBuilder(
                        Util.MSG_RESPONSE + " " + Util.AUTHOR_PREFIX + author + " msg_id:" + msgId);
                if (replyToId != null) {
                    msgHeader.append(" reply_to_id:").append(replyToId);
                } else {
                    msgHeader.append(" reply_to_id:").append(-1);
                }
                msgHeader.append(" republished:").append(republished);

                output.write(msgHeader.toString() + "\r\n" + message + "\r\n");
            } else {
                output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            }
        } catch (SQLException e) {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
        }
    }
}
