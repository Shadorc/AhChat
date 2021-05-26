package me.shadorc.server;

import me.shadorc.client.Client;
import me.shadorc.client.Main;
import me.shadorc.server.Server.MessageType;

import java.util.ArrayList;

public class ServerMain {

    private static final ArrayList<ServerClient> clients = new ArrayList<>();

    private static boolean isOpen = false;
    private static ServerFrame serverFrame;
    private static Server server;

    public static void init() {
        isOpen = true;
        serverFrame = new ServerFrame();
        server = new Server();
        server.start();
    }

    public static synchronized void addClient(ServerClient client) {
        clients.add(client);
        serverFrame.addUser(client.getName());
        Server.sendAll("/connexion " + client.getName(), MessageType.COMMAND);
    }

    public static synchronized void delClient(ServerClient client) {
        clients.remove(client);
        serverFrame.removeUser(client.getName());
        Server.sendAll("/deconnexion " + client.getName(), MessageType.COMMAND);
    }

    public static ArrayList<ServerClient> getClients() {
        return clients;
    }

    public static ServerFrame getFrame() {
        return serverFrame;
    }

    public static boolean isOpen() {
        return isOpen;
    }

    public static void exit() {
        server.stop();
        isOpen = false;
        if (!Main.getFrame().isVisible()) {
            Client.exit(true);
        }
    }
}
