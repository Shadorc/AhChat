package com.shadorc.ahchat.client.command;

import com.shadorc.ahchat.client.frame.ConnectedPanel;

import java.util.List;

public class RenameCmd extends ClientCmd {

    public RenameCmd() {
        super(List.of("rename"));
    }

    @Override
    public void execute(final ClientContext context) {
        final String[] args = context.getArg().split(" ", 2);
        ConnectedPanel.getUsersList().replaceUser(args[0], args[1]);
    }

}
