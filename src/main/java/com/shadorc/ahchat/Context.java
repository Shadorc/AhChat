package com.shadorc.ahchat;

import com.shadorc.ahchat.server.ServerClient;

public class Context {

    private final ServerClient client;
    private final String command;
    private final String arg;

    public Context(final ServerClient client, final String text) {
        this.client = client;

        final String[] splitCmd = text.split(" ", 2);
        this.command = splitCmd[0].toLowerCase();
        this.arg = (splitCmd.length > 1) ? splitCmd[1] : null;
    }

    public ServerClient getClient() {
        return this.client;
    }

    public String getCommandName() {
        return this.command;
    }

    public String getArg() {
        return this.arg;
    }

}
