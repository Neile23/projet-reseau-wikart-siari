package Clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Follower {
    private static final Logger logger = Logger.getLogger(Follower.class.getName());

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Entrez le nom du ou des auteurs choisis (séparés par des virgules) : ");
        String authorsInput = scanner.nextLine();
        String[] authors = authorsInput.split(",");

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            // Récupérer les identifiants des messages des auteurs
            List<Integer> messageIds = new ArrayList<>();
            for (String author : authors) {
                String request = "RCV_IDS author:" + author.trim() +" limit:100"+ "\r\n\r\n";
                output.write(request);
                output.flush();

                // Lire la réponse du serveur
                String responseLine = input.readLine();
                while (responseLine != null && !responseLine.isEmpty()) {
                    try {
                        int messageId = Integer.parseInt(responseLine);
                        messageIds.add(messageId);
                    } catch (NumberFormatException e) {
                        // Ignorer les lignes non valides
                    }
                    responseLine = input.readLine();
                }
            }

            // Afficher le contenu des messages correspondant à ces identifiants
            for (int messageId : messageIds) {
                String request = "RCV_MSG msg_id:" + messageId + "\r\n\r\n";
                output.write(request);
                output.flush();

                // Lire la réponse du serveur
                String responseLine = input.readLine();
                String replyTo = null;
                boolean republished = false;

                if (responseLine.startsWith("MSG")) {
                    String[] metadata = responseLine.split(" ");
                    for (String meta : metadata) {
                        if (meta.startsWith("reply_to_id:")) {
                            replyTo = meta.substring("reply_to_id:".length());
                        }
                        if (meta.startsWith("republished:")) {
                            republished = Boolean.parseBoolean(meta.substring("republished:".length()));
                        }
                    }
                }

                // Lire le contenu du message
                StringBuilder responseBuilder = new StringBuilder();
                responseLine = input.readLine();
                while (responseLine != null && !responseLine.isEmpty()) {
                    responseBuilder.append(responseLine).append("\n");
                    responseLine = input.readLine();
                }
                String response = responseBuilder.toString().trim();

                // Afficher les informations du message et son contenu
                System.out.println("Message ID " + messageId + ":");
                System.out.println(response);
                if (!replyTo.equals("-1")) {
                    System.out.println("  - Reply to: " + replyTo);
                }
                if(republished) {
                    System.out.println(" - Republished");
                }
                scanner.close();
            }

        } catch (IOException e) {
        logger.log(Level.WARNING, "IO Error: " + e.getMessage());
        }
    }
}
