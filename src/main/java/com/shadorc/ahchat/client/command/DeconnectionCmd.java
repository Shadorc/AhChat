package com.shadorc.ahchat.client.command;

import com.shadorc.ahchat.client.frame.ConnectedPanel;

import java.util.List;

public class DeconnectionCmd extends ClientCmd {

    public DeconnectionCmd() {
        super(List.of("deconnection"));
    }

    @Override
    public void execute(final ClientContext context) {
        ConnectedPanel.getUsersList()
                .removeUser(context.getArg());
    }

}
