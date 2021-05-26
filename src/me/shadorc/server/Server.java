package me.shadorc.server;

import me.shadorc.client.Main;
import me.shadorc.utility.ServerUtility;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class Server implements Runnable {

    private ServerSocket ss_chat, ss_data;
    private String ip;

    public enum MessageType {
        NORMAL, COMMAND, INFO;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        ss_chat = null; //Chat Socket Server
        ss_data = null; //Data Socket Server

        try {
            ss_chat = new ServerSocket(15000);
            ss_data = new ServerSocket(15001);

            ip = ServerUtility.getIp();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ip), null);

            ServerMain.getFrame().updateInfos(ip, ss_chat.getLocalPort(), ss_data.getLocalPort());

            ServerMain.getFrame().dispMessage("Welcome");
            ServerMain.getFrame().dispMessage("-------------------------------------------");

            while (true) {
                //Pending connection loop (blocking on accept())
                new ServerClient(ss_chat.accept(), ss_data.accept());
            }

        } catch (SocketException ignore) {
            //Server's ending, ignore it

        } catch (IOException e) {
            ServerMain.getFrame().dispError(e, "Erreur lors de l'ouverture du serveur : " + e.getMessage());

        } finally {
            try {
                if (ss_chat != null) ss_chat.close();
                if (ss_data != null) ss_data.close();
            } catch (IOException e) {
                ServerMain.getFrame().dispError(e, "Erreur lors de la fermeture du serveur : " + e.getMessage());
            }
        }
    }

    public void stop() {
        try {
            Server.sendAll("/serverClosed", MessageType.COMMAND);
            if (ss_chat != null) ss_chat.close();
            if (ss_data != null) ss_data.close();
        } catch (IOException e) {
            Main.showErrorDialog(e, "Erreur lors de la fermeture du serveur : " + e.getMessage());
        }
    }

    public static synchronized void sendAll(String message, MessageType type) {

        if (type != MessageType.COMMAND) {
            message = ServerUtility.getTime()
                    + (type == MessageType.INFO ? "<b><i><font color=red>[INFO]</b></i> " : "")
                    + message;
            ServerMain.getFrame().dispMessage(message);
        }

        for (ServerClient client : ServerMain.getClients()) {
            client.sendMessage(message);
        }
    }


}