package Factory;

import java.io.BufferedWriter;
import java.sql.Connection;
import CommandHandlers.CommandHandler;
import CommandHandlers.RepublishCommandHandler;

public class RepublishCommandHandlerFactory implements CommandHandlerFactory {

    @Override
    public CommandHandler createCommandHandler(String body, BufferedWriter output, Connection conn) {
        return new RepublishCommandHandler(body, output, conn);
    }
}
