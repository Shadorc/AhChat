package com.shadorc.ahchat.server.command;

import com.shadorc.ahchat.command.BaseCmd;

import java.util.List;

public abstract class ServerCmd extends BaseCmd<ServerContext> {

    public ServerCmd(final List<String> names) {
        super(names);
    }

    public abstract void displayHelp(final ServerContext baseContext);

}
