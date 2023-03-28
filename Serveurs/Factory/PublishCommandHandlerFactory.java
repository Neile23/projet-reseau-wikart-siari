package Factory;

import java.io.BufferedWriter;
import java.sql.Connection;
import CommandHandlers.CommandHandler;
import CommandHandlers.PublishCommandHandler;

public class PublishCommandHandlerFactory implements CommandHandlerFactory {
    
    @Override
    public CommandHandler createCommandHandler(String body, BufferedWriter output, Connection conn) {
        return new PublishCommandHandler(body, output, conn);
    }
}
