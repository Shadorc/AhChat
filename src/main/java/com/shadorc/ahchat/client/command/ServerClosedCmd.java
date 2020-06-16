package com.shadorc.ahchat.client.command;

import com.shadorc.ahchat.client.Client;

import java.util.List;

public class ServerClosedCmd extends ClientCmd {

    public ServerClosedCmd() {
        super(List.of("server_closed"));
    }

    @Override
    public void execute(final ClientContext context) {
        Client.exit(false);
    }

}
