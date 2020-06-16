package com.shadorc.ahchat.server;

import com.shadorc.ahchat.ThreadPoolManager;
import com.shadorc.ahchat.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class Server {

    private boolean isRunning;
    private ServerSocket chatSocket;
    private ServerSocket dataSocket;
    private String ip;

    public enum MessageType {
        NORMAL, COMMAND, INFO;
    }

    public Server() {
        this.isRunning = false;
    }

    public void start() {
        this.isRunning = true;

        ThreadPoolManager.getInstance().execute(() -> {
            try (final ServerSocket chatSocket = new ServerSocket(15000);
                    final ServerSocket dataSocket = new ServerSocket(15001)) {

                this.chatSocket = chatSocket;
                this.dataSocket = dataSocket;
                this.ip = Util.getIp();

                ServerManager.getInstance().getFrame()
                        .updateInfos(this.ip, this.chatSocket.getLocalPort(), this.dataSocket.getLocalPort());

                ServerManager.getInstance().getFrame().dispMessage("Welcome");
                ServerManager.getInstance().getFrame().dispMessage("-------------------------------------------");

                while (this.isRunning) {
                    // Pending connection loop (blocking on accept())
                    final ServerClient client = new ServerClient(chatSocket.accept(), dataSocket.accept());
                    client.start();
                }

            } catch (final SocketException ignore) {
                // Server's ending, ignore it

            } catch (final IOException err) {
                ServerManager.getInstance().getFrame()
                        .dispError(err, "An unknown error occurred: " + err.getMessage());
            }
        });
    }

    public void stop() {
        this.isRunning = false;
        Server.sendAll("/serverClosed", MessageType.COMMAND);
        Util.close(this.chatSocket);
        Util.close(this.dataSocket);
    }

    // TODO: Message should be final
    public static synchronized void sendAll(final String message, final MessageType type) {
        String formattedMessage = message;
        if (type != MessageType.COMMAND) {
            formattedMessage = Util.getFormattedTime()
                    + (type == MessageType.INFO ? "<b><i><font color=red>[INFO]</b></i> " : "")
                    + formattedMessage;
            ServerManager.getInstance().getFrame().dispMessage(formattedMessage);
        }

        for (final ServerClient client : ServerManager.getInstance().getClients()) {
            client.sendMessage(formattedMessage);
        }
    }


}
