package com.shadorc.ahchat.server.command;

import com.shadorc.ahchat.command.CommandException;

import java.util.List;

public class RenameCmd extends ServerCmd {

    public RenameCmd() {
        super(List.of("rename"));
    }

    @Override
    public void execute(final ServerContext context) {
        final String arg = context.getArg();
        if (arg == null) {
            throw new CommandException("Pseudo invalide.");
        }

        context.getClient().setName(arg);
        context.getClient().sendMessage(String.format("Vous avez été renommé \"%s\".", arg));
    }

    @Override
    public void displayHelp(final ServerContext context) {
        context.getClient().sendMessage(String.format("Changer de pseudo: /%s <name>", this.getName()));
    }
}
