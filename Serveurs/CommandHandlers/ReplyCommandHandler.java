package CommandHandlers;


import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;

public class ReplyCommandHandler implements CommandHandler{
    private Connection conn;
    private String body;
    private BufferedWriter output;

    public ReplyCommandHandler(String body, BufferedWriter output, Connection conn) {
        this.body = body;
        this.output = output;
        this.conn = conn;
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

        String replyToIdStr = Util.extractValueFromBody(body, Util.REPLY_TO_ID_PREFIX);
        if (replyToIdStr == null) {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
            return;
        }

        int replyToId = Integer.parseInt(replyToIdStr.trim());

        System.out.println("@" + author + " (reply to " + replyToId + "): " + message);
        int messageId = Util.addMessageToDatabase(conn, message, author, replyToId, false);
        if (messageId != -1) {
            output.write(Util.OK_RESPONSE + "\r\n\r\n");
        } else {
            output.write(Util.ERROR_RESPONSE + "\r\n\r\n");
        }
    }
}
