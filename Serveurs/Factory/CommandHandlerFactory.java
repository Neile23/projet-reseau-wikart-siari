package Factory;

import java.io.BufferedWriter;
import java.sql.Connection;
import CommandHandlers.CommandHandler;

public interface CommandHandlerFactory {
    CommandHandler createCommandHandler(String body, BufferedWriter output, Connection conn);
}
