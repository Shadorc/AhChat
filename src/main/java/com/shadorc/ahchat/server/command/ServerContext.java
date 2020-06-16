package com.shadorc.ahchat.server.command;

import com.shadorc.ahchat.command.BaseContext;
import com.shadorc.ahchat.server.ServerClient;

public class ServerContext extends BaseContext {

    private final ServerClient client;

    public ServerContext(final ServerClient client, final String message) {
        super(message);
        this.client = client;
    }

    public ServerClient getClient() {
        return this.client;
    }

}
