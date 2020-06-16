package com.shadorc.ahchat.server;

import com.shadorc.ahchat.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class Server implements Runnable {

    private ServerSocket chatSocket;
    private ServerSocket dataSocket;
    private String ip;

    public enum MessageType {
        NORMAL, COMMAND, INFO;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try (final ServerSocket chatSocket = new ServerSocket(15000);
                final ServerSocket dataSocket = new ServerSocket(15001)) {

            this.chatSocket = chatSocket;
            this.dataSocket = dataSocket;
            this.ip = Util.getIp();

            ServerManager.getInstance().getFrame()
                    .updateInfos(this.ip, this.chatSocket.getLocalPort(), this.dataSocket.getLocalPort());

            ServerManager.getInstance().getFrame().dispMessage("Welcome");
            ServerManager.getInstance().getFrame().dispMessage("-------------------------------------------");

            while (true) {
                // Pending connection loop (blocking on accept())
                new ServerClient(chatSocket.accept(), dataSocket.accept());
            }

        } catch (final SocketException ignore) {
            //Server's ending, ignore it

        } catch (final IOException err) {
            ServerManager.getInstance().getFrame()
                    .dispError(err, "Erreur lors de l'ouverture du serveur : " + err.getMessage());
        }
    }

    public void stop() {
        Server.sendAll("/serverClosed", MessageType.COMMAND);
        Util.close(this.chatSocket);
        Util.close(this.dataSocket);
    }

    // TODO: Message should be final
    public static synchronized void sendAll(String message, final MessageType type) {
        if (type != MessageType.COMMAND) {
            message = Util.getFormattedTime()
                    + (type == MessageType.INFO ? "<b><i><font color=red>[INFO]</b></i> " : "")
                    + message;
            ServerManager.getInstance().getFrame().dispMessage(message);
        }

        for (ServerClient client : ServerManager.getInstance().getClients()) {
            client.sendMessage(message);
        }
    }


}