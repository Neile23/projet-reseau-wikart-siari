package Serveurs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Database.MessageManager;
import Database.UserManager;
import Database.MessageManager;
import Database.Query.QueryBuilder;
import Database.Query.QueryPreparer;
import Database.Query.QueryExecutor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Serveur implements Runnable {
    private static final Logger logger = Logger.getLogger(Serveur.class.getName());

    private static final String PUBLISH_COMMAND = "PUBLISH";
    private static final String RCV_IDS_COMMAND = "RCV_IDS";
    private static final String RCV_MSG_COMMAND = "RCV_MSG";
    private static final String AUTHOR_PREFIX = "author:";
    private static final String TAG_PREFIX = "tag:";
    private static final String SINCE_ID_PREFIX = "since_id:";
    private static final String LIMIT_PREFIX = "limit:";
    private static final String MSG_IDS_RESPONSE = "MSG_IDS";
    private static final String MSG_RESPONSE = "MSG";
    private static final String OK_RESPONSE = "OK";
    private static final String ERROR_RESPONSE = "ERROR";

    private static final int DEFAULT_PORT = 12345;
    private static final int DEFAULT_LIMIT = 5;

    private Socket socket;
    private Connection conn;

    public Serveur(Socket socket) {
        this.socket = socket;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:database/database.db");
            Class.forName("org.sqlite.JDBC");
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.WARNING, "Database connection error: " + e.getMessage());
        }
    }

    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            while (true) {
                String request = "";
                String line = input.readLine();

                while (line != null && !line.isEmpty()) {
                    request += line + "\r\n";
                    line = input.readLine();
                }

                String[] parts = request.split(" ", 2);
                String command = parts[0];
                String body = parts.length > 1 ? parts[1] : "";

                if (command.equals(PUBLISH_COMMAND)) {
                    handlePublishCommand(body, output);
                } else if (command.equals(RCV_IDS_COMMAND)) {
                    handleRcvIdsCommand(body, output);
                } else if (command.equals(RCV_MSG_COMMAND)) {
                    handleRcvMsgCommand(body, output);
                } else {
                    output.write(ERROR_RESPONSE + "\r\n");
                }
                output.flush();
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "IO Error: " + e.getMessage());
        }

    }

    private void handlePublishCommand(String body, BufferedWriter output) throws IOException {
        String author = getAuthorFromBody(body);
        if (author == null) {
            output.write(ERROR_RESPONSE + "\n");
            return;
        }

        String message = getMessageFromBody(body);
        if (message == null || message.isEmpty()) {
            output.write(ERROR_RESPONSE + "\n");
            return;
        }

        System.out.println(author + " : " + message);
        if (addMessageToDatabase(message, author)) {
            output.write(OK_RESPONSE + "\n");
        } else {
            output.write(ERROR_RESPONSE + "\n");
        }
    }

    private String getAuthorFromBody(String body) {
        int authorIndex = body.indexOf(AUTHOR_PREFIX);
        if (authorIndex == -1) {
            return null;
        }
        return body.substring(AUTHOR_PREFIX.length(), body.indexOf("\r"));
    }

    private String getMessageFromBody(String body) {
        int messageIndex = body.indexOf("\r\n") + 2;
        if (messageIndex == -1) {
            return null;
        }
        return body.substring(messageIndex, body.length());
    }

    private boolean addMessageToDatabase(String message, String author) {
        try {
            UserManager usersTableManager = new UserManager(conn);
            int userId = usersTableManager.getUserId(author);

            MessageManager messagesTableManager = new MessageManager(conn);
            messagesTableManager.addMessage(message, userId);
            return true;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while adding message to database: " + e.getMessage());
            return false;
        }
    }


    private void handleRcvIdsCommand(String body, BufferedWriter output) throws IOException {
        try {
            MessageManager messagesTableManager = new MessageManager(conn);
            String author = extractValueFromBody(body, AUTHOR_PREFIX);
            String tag = extractValueFromBody(body, TAG_PREFIX);
            String sinceIdStr = extractValueFromBody(body, SINCE_ID_PREFIX);
            String limitStr = extractValueFromBody(body, LIMIT_PREFIX);
            int sinceId = sinceIdStr == null ? -1 : Integer.parseInt(sinceIdStr);
            int limit = limitStr == null ? DEFAULT_LIMIT : Integer.parseInt(limitStr);
            List<Integer> messageIds = messagesTableManager.getMessageIds(author, tag, sinceId, limit);
            StringBuilder response = new StringBuilder(MSG_IDS_RESPONSE);
            for (int messageId : messageIds) {
                response.append(" ").append(messageId);
            }
            output.write(response.toString() + "\r\n");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching message IDs: " + e.getMessage());
            output.write(ERROR_RESPONSE + "\r\n");
        }
    }

    private void handleRcvMsgCommand(String body, BufferedWriter output) throws IOException {
        String msgIdStr = extractValueFromBody(body, "msg_id:");
        if (msgIdStr == null) {
            output.write(ERROR_RESPONSE + "\r\n");
            return;
        }

        int msgId = Integer.parseInt(msgIdStr);
        try {
            MessageManager messagesTableManager = new MessageManager(conn);
            UserManager usersTableManager = new UserManager(conn);
            String message = messagesTableManager.getMessage(msgId);
            int userId = messagesTableManager.getUserIdForMessage(msgId);
            String author = usersTableManager.getUserName(userId);

            if (message != null && author != null) {
                output.write(MSG_RESPONSE + " " + AUTHOR_PREFIX + author + "\r\n" + message + "\r\n");
            } else {
                output.write(ERROR_RESPONSE + "\r\n");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching message: " + e.getMessage());
            output.write(ERROR_RESPONSE + "\r\n");
        }
    }

    private String extractValueFromBody(String body, String prefix) {
        int prefixIndex = body.indexOf(prefix);
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





    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(DEFAULT_PORT)) {
            while (true) {
                Socket clientSocket = ss.accept();
                (new Thread(new Serveur(clientSocket))).start();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "IO error: " + e.getMessage());
        }
    }

}