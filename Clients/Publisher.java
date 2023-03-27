package Clients;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Publisher {
    private static final Logger logger = Logger.getLogger(Publisher.class.getName());

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Entrez votre pseudo : ");
        String author = scanner.nextLine();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            while (true) {
                System.out.println("Enter command (PUBLISH, RCV_IDS, RCV_MSG) or type 'exit' to quit: ");
                String command = scanner.nextLine();

                if ("exit".equalsIgnoreCase(command)) {
                    break;
                }

                switch (command.toUpperCase()) {
                    case "PUBLISH":
                        System.out.println("Message : ");
                        String message = scanner.nextLine();
                        String request = "PUBLISH author:" + author + "\r\n" + message + "\r\n\r\n";
                        output.write(request);
                        break;

                    case "RCV_IDS":
                        System.out.println("Enter the author ('user', optional): ");
                        String targetAuthor = scanner.nextLine();
                        System.out.println("Enter the tag ('#tag', optional): ");
                        String tag = scanner.nextLine();
                        System.out.println("Enter since_id ('id', optional): ");
                        String sinceId = scanner.nextLine();
                        System.out.println("Enter limit ('n', optional, default is 5): ");
                        String limit = scanner.nextLine();

                        request = "RCV_IDS";
                        if (!targetAuthor.isEmpty()) {
                            request += " author:" + targetAuthor;
                        }
                        if (!tag.isEmpty()) {
                            request += " tag:" + tag;
                        }
                        if (!sinceId.isEmpty()) {
                            request += " since_id:" + sinceId;
                        }
                        if (!limit.isEmpty()) {
                            request += " limit:" + limit;
                        }
                        request += "\r\n\r\n";
                        output.write(request);
                        break;

                    case "RCV_MSG":
                        System.out.println("Enter the message ID: ");
                        int messageId = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline
                        request = "RCV_MSG msg_id:" + messageId + "\r\n\r\n";
                        output.write(request);
                        break;

                    default:
                        System.out.println("Invalid command.");
                        continue;
                }

                output.flush();

                // Read server response
                StringBuilder responseBuilder = new StringBuilder();
                String responseLine = input.readLine();
                while (responseLine != null && !responseLine.isEmpty()) {
                    responseBuilder.append(responseLine).append("\n");
                    responseLine = input.readLine();
                }
                String response = responseBuilder.toString().trim();
                System.out.println("Server response:\n" + response);

            }

            scanner.close();

        } catch (IOException e) {
            logger.log(Level.WARNING, "IO Error: " + e.getMessage());
        }
    }

}
