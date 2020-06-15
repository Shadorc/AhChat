package com.shadorc.ahchat.client;

import com.shadorc.ahchat.client.frame.ConnectedPanel;

import javax.swing.ImageIcon;
import java.util.Arrays;
import java.util.List;


public class Command {

    public static void serverCommand(String command) {

        List<String> argsList = Arrays.asList(command.split(" "));

        switch (argsList.get(0)) {
            case "/connexion":
                ConnectedPanel.getUsersList().addUser(argsList.get(1), new ImageIcon(Command.class.getResource("/icon.png")));
                break;
            case "/deconnexion":
                ConnectedPanel.getUsersList().removeUser(argsList.get(1));
                break;
            case "/rename":
                ConnectedPanel.getUsersList().replaceUser(argsList.get(1), argsList.get(2));
                break;
            case "/serverClosed":
                Client.exit(false);
                break;
        }
    }
}
