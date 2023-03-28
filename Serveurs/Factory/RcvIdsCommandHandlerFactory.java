package Factory;

import java.io.BufferedWriter;
import java.sql.Connection;
import CommandHandlers.CommandHandler;
import CommandHandlers.RcvIdsCommandHandler;

public class RcvIdsCommandHandlerFactory implements CommandHandlerFactory {

    @Override
    public CommandHandler createCommandHandler(String body, BufferedWriter output, Connection conn) {
        return new RcvIdsCommandHandler(body, output, conn);
    }
}
