package Clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Repost {
    private static final Logger logger = Logger.getLogger(Repost.class.getName());

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username ('username'): ");
        String username ="@" + scanner.nextLine();
        System.out.println("Enter the names of the authors to repost (separated by commas): ");
        String authorsInput = scanner.nextLine();
        String[] authorsArray = authorsInput.split(", ");
        Set<String> authorsToRepost = new HashSet<>();
        for (String author : authorsArray) {
            authorsToRepost.add(author.trim());
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            String setRepost = "SET_REPOST_CLIENT\r\n\r\n";
            output.write(setRepost);
            output.flush();

            // Listen for incoming messages and repost them
            Pattern pattern = Pattern.compile("MSG author:@(\\S+) msg_id:(\\d+)");
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }

                // Check if the line contains a message from a user we want to repost
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String user = matcher.group(1);
                    if (authorsToRepost.contains(user)) {
                        System.out.println("Reposting message from @" + user + ": " + input.readLine());
                        // Extract the message ID and send a REPUBLISH command
                        String msgId = matcher.group(2);
                        String command = "REPUBLISH author:" + username + " msg_id:" + msgId + "\r\n\r\n";
                        output.write(command);
                        output.flush();
                    }
                }
            }

            scanner.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "IO Error: " + e.getMessage());
        }
    }
}
