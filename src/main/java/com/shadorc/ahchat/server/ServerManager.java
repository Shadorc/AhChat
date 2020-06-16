package com.shadorc.ahchat.server;

import com.shadorc.ahchat.client.Client;
import com.shadorc.ahchat.client.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager {

    private static ServerManager instance;

    static {
        ServerManager.instance = new ServerManager();
    }

    private final List<ServerClient> clients;
    private final AtomicBoolean isStarted;

    private ServerFrame serverFrame;
    private Server server;

    private ServerManager() {
        this.clients = new ArrayList<>();
        this.isStarted = new AtomicBoolean(false);
    }

    public void start() {
        if (!this.isStarted.getAndSet(true)) {
            this.serverFrame = new ServerFrame();
            this.server = new Server();
            this.server.start();
        }
    }

    public synchronized void addClient(final ServerClient client) {
        this.clients.add(client);
        this.serverFrame.addUser(client.getName());
        Server.sendAll(String.format("/connexion %s", client.getName()), Server.MessageType.COMMAND);
    }

    public synchronized void removeClient(final ServerClient client) {
        this.clients.remove(client);
        this.serverFrame.removeUser(client.getName());
        Server.sendAll(String.format("/deconnexion %s", client.getName()), Server.MessageType.COMMAND);
    }

    public List<ServerClient> getClients() {
        return Collections.unmodifiableList(this.clients);
    }

    public ServerFrame getFrame() {
        return this.serverFrame;
    }

    public boolean isStarted() {
        return this.isStarted.get();
    }

    public void stop() {
        this.server.stop();
        this.isStarted.set(false);
        if (!Main.getFrame().isVisible()) {
            Client.exit(true);
        }
    }

    public static ServerManager getInstance() {
        return ServerManager.instance;
    }

}
