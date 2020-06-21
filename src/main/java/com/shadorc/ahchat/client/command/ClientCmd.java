package com.shadorc.ahchat.client.command;

import com.shadorc.ahchat.command.BaseCmd;

import java.util.List;

public abstract class ClientCmd extends BaseCmd<ClientContext> {

    public ClientCmd(final List<String> names) {
        super(names);
    }

}
