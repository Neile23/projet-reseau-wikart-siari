package Factory;

import java.io.BufferedWriter;
import java.sql.Connection;
import CommandHandlers.CommandHandler;
import CommandHandlers.ReplyCommandHandler;

public class ReplyCommandHandlerFactory implements CommandHandlerFactory {

    @Override
    public CommandHandler createCommandHandler(String body, BufferedWriter output, Connection conn) {
        return new ReplyCommandHandler(body, output, conn);
    }
}
