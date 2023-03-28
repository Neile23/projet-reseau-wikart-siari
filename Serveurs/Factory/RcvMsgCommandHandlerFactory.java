package Factory;

import java.io.BufferedWriter;
import java.sql.Connection;
import CommandHandlers.CommandHandler;
import CommandHandlers.RcvMsgCommandHandler;

public class RcvMsgCommandHandlerFactory implements CommandHandlerFactory {

    @Override
    public CommandHandler createCommandHandler(String body, BufferedWriter output, Connection conn) {
        return new RcvMsgCommandHandler(body, output, conn);
    }
}
