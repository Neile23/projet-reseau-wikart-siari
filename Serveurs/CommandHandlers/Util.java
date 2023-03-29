package CommandHandlers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Database.MessageManager;
import Database.UserManager;

public class Util {
    private static final Logger logger = Logger.getLogger(Util.class.getName());
    
    public static final String AUTHOR_PREFIX = "author:";
    public static final String TAG_PREFIX = "tag:";
    public static final String SINCE_ID_PREFIX = "since_id:";
    public static final String LIMIT_PREFIX = "limit:";
    public static final String MSG_IDS_RESPONSE = "MSG_IDS";
    public static final String MSG_RESPONSE = "MSG";
    public static final String OK_RESPONSE = "OK";
    public static final String ERROR_RESPONSE = "ERROR";
    public static final String REPLY_TO_ID_PREFIX = "reply_to_id:";
    public static final String MSG_ID_PREFIX = "msg_id:";

    public static final int DEFAULT_PORT = 12345;
    public static final int DEFAULT_LIMIT = 5;

    public static String getAuthorFromBody(String body) {
        int authorIndex = body.indexOf(AUTHOR_PREFIX);
        if (authorIndex == -1) {
            return null;
        }
        int startIndex = authorIndex + AUTHOR_PREFIX.length();
        int endIndex = body.indexOf(" ", startIndex);
        int endIndexCarriageReturn = body.indexOf("\r", startIndex);

        if (endIndexCarriageReturn != -1 && (endIndexCarriageReturn < endIndex || endIndex == -1)) {
            endIndex = endIndexCarriageReturn;
        }

        if (endIndex == -1) {
            endIndex = body.length(); // In case there is no whitespace or \r after the author name
        }
        return body.substring(startIndex, endIndex);
    }

    public static String getMessageFromBody(String body) {
        int messageIndex = body.indexOf("\r\n") + 2;
        if (messageIndex == -1) {
            return null;
        }
        return body.substring(messageIndex, body.length());
    }

    public static Set<String> extractTagsFromMessage(String message) {
        Set<String> tags = new HashSet<>();
        Matcher matcher = Pattern.compile("#\\w+").matcher(message);
        while (matcher.find()) {
            tags.add(matcher.group());
        }
        return tags;
    }

    public static String extractValueFromBody(String body, String prefix) {
        int prefixIndex = body.toLowerCase().indexOf(prefix.toLowerCase());
        if (prefixIndex == -1) {
            return null;
        }
        int valueStartIndex = prefixIndex + prefix.length();
        int valueEndIndex = body.indexOf(" ", valueStartIndex);
        if (valueEndIndex == -1) {
            valueEndIndex = body.length();
        }
        return body.substring(valueStartIndex, valueEndIndex);
    }

    public static int addMessageToDatabase(Connection conn, String message, String author, Integer replyToId, boolean republished) {
        try {
            UserManager usersTableManager = new UserManager(conn);
            if (!usersTableManager.userExists(author)) {
                usersTableManager.addUser(author);
            }

            int userId = usersTableManager.getUserId(author);

            MessageManager messagesTableManager = new MessageManager(conn);
            messagesTableManager.addMessage(message, userId, replyToId, republished);

            return messagesTableManager.getLastMessageId();

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while adding message to database: " + e.getMessage());
            return -1;
        }
    }

}
