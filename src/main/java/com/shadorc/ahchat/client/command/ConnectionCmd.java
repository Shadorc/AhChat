package com.shadorc.ahchat.client.command;

import com.shadorc.ahchat.client.frame.ConnectedPanel;

import javax.swing.ImageIcon;
import java.util.List;

public class ConnectionCmd extends ClientCmd {

    public ConnectionCmd() {
        super(List.of("connection"));
    }

    @Override
    public void execute(final ClientContext context) {
        ConnectedPanel.getUsersList().addUser(context.getArg(), new ImageIcon(this.getClass().getResource("/icon.png")));
    }
}
