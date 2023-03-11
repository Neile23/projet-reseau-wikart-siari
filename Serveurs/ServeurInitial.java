package Serveurs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.MessagesTableManager;
import database.UsersTableManager;

public class ServeurInitial implements Runnable {
    private static final Logger logger = Logger.getLogger(UsersTableManager.class.getName());

    private static final String PUBLISH_COMMAND = "PUBLISH";
    private static final String AUTHOR_PREFIX = "author:";
    private static final String OK_RESPONSE = "OK";
    private static final String ERROR_RESPONSE = "ERROR";

    private static final int DEFAULT_PORT = 12345;

    private UsersTableManager usersTableManager;
    private MessagesTableManager messagesTableManager;

    private Socket socket;

    public ServeurInitial(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String request = "";
            String line = input.readLine();

            while (line != null && !line.isEmpty()) {
                request += line + "\r\n";
                line = input.readLine();
            }

            if (request.isEmpty()) {
                return;
            }

            String[] parts = request.split(" ", 2);
            String command = parts[0];
            String body = parts.length > 1 ? parts[1] : "";

            if (command.equals(PUBLISH_COMMAND)) {
                handlePublishCommand(body, output);
            } else {
                output.write(ERROR_RESPONSE + "\r\n");
            }

        } catch (IOException e) {
            logger.log(Level.WARNING,"IO Error: " + e.getMessage());
        }

    }

    private void handlePublishCommand(String body, BufferedWriter output) throws IOException {
        int authorIndex = body.indexOf(AUTHOR_PREFIX);
        if (authorIndex == -1) {
            output.write(ERROR_RESPONSE + "\n");
            return;
        }

        String author = body.substring(authorIndex + AUTHOR_PREFIX.length());
        String message = body.substring(0, authorIndex).trim();

        if (message.isEmpty()) {
            output.write(ERROR_RESPONSE + "\n");
            return;
        }

        addMessageToDatabase(message, author);
        output.write(OK_RESPONSE + "\n");
    }

    private void addMessageToDatabase(String message, String author) {
        try {
            usersTableManager = new UsersTableManager();
            int userId = usersTableManager.getUserId(author);
            usersTableManager.close();
            messagesTableManager = new MessagesTableManager();
            messagesTableManager.addMessage(message, userId);
            messagesTableManager.close();
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.WARNING,"Error while adding message to database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(DEFAULT_PORT)) {
            while (true) {
                Socket clientSocket = ss.accept();
                (new Thread(new ServeurInitial(clientSocket))).start();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING,"IO error: " + e.getMessage());
        }
    }

}