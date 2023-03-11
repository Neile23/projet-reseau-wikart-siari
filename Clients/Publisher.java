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
                System.out.println("Message : ");
                String message = scanner.nextLine();
                if (message == null || message.equals("\u0004")) {
                    break;
                }
                String request = "PUBLISH author:@" + author + "\r\n" + message + "\r\n\r\n";
                output.write(request);
                output.flush();

                // Lecture de la réponse du serveur
                String response = input.readLine();
                while (response == null) {
                    Thread.sleep(100);
                    response = input.readLine();
                }
                System.out.println("Server response: " + response);
            }

            scanner.close();

        } catch (IOException e) {
            logger.log(Level.WARNING, "IO Error: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
