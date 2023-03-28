import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import CommandHandlers.CommandHandler;
import Factory.CommandHandlerFactory;

public class CommandProcessor {

    private final BufferedReader input;
    private final BufferedWriter output;
    private final Map<String, CommandHandlerFactory> commandHandlerFactories;

    public CommandProcessor(BufferedReader input, BufferedWriter output,
            Map<String, CommandHandlerFactory> commandHandlerFactories) {
        this.input = input;
        this.output = output;
        this.commandHandlerFactories = commandHandlerFactories;
    }

    public void processCommands() throws IOException {
        while (true) {
            String request = "";
            String line = input.readLine();
            if (line == null) {
                break;
            }

            while (!line.isEmpty()) {
                request += line + "\r\n";
                line = input.readLine();
            }

            String[] parts = request.split(" ", 2);
            String command = parts[0];
            String body = parts.length > 1 ? parts[1] : "";

            CommandHandlerFactory commandHandlerFactory = commandHandlerFactories.get(command);
            if (commandHandlerFactory != null) {
                try (Connection conn = ConnectionPool.getConnection()) {
                    CommandHandler commandHandler = commandHandlerFactory.createCommandHandler(body, output, conn);
                    commandHandler.handle();
                } catch (SQLException e) {
                    output.write("ERROR\r\n\r\n");
                }
            } else {
                output.write("ERROR\r\n\r\n");
            }

            try {
                output.flush();
            } catch (SocketException e) {
                System.err.println("Client disconnected unexpectedly: " + e.getMessage());
                break;
            }
        }
    }
}
