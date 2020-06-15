package com.shadorc.ahchat.server;

import com.shadorc.ahchat.server.Server.MessageType;
import com.shadorc.ahchat.utility.ServerUtil;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

public class ServerClient implements Runnable {

    private final Socket chatSocket;
    private final Socket dataSocket;
    private final String ip;
    private String name;

    private DataOutputStream infoDataOut;
    private InputStream dataIn;
    private OutputStream dataOut;
    private BufferedReader chatIn;
    private PrintWriter chatOut;


    public ServerClient(final Socket chatSocket, final Socket dataSocket) {
        this.chatSocket = chatSocket;
        this.dataSocket = dataSocket;

        this.name = "Unknown";
        this.ip = chatSocket.getRemoteSocketAddress().toString();

        try {
            this.chatOut = new PrintWriter(chatSocket.getOutputStream());
            this.chatIn = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));

            this.dataOut = dataSocket.getOutputStream();
            this.dataIn = dataSocket.getInputStream();

            this.infoDataOut = new DataOutputStream(this.dataOut);

            // Waits for name to arrive
            this.name = chatIn.readLine();

            // TODO: Improve this
            // Rename the client if someone has already this name
            for (final ServerClient client : ServerMain.getClients()) {
                if (client.getName().equalsIgnoreCase(name)) {
                    this.name = this.name + "_(" + ThreadLocalRandom.current().nextInt(10) + ")";
                }
            }

            ServerMain.addClient(this);

            new Thread(this).start();

        } catch (final IOException err) {
            ServerMain.getFrame().dispError(err, "Erreur lors de la création du client : " + err.getMessage());
            this.quit();
        }
    }

    @Override
    public void run() {
        String message;

        try {
            this.sendMessage("<b><font color=18B00A>* * Bienvenue ! Pour afficher l'aide, entrez /help.");

            Server.sendAll(name + " vient de se connecter.", MessageType.INFO);

            //Send the list of all connected people
            for (ServerClient client : ServerMain.getClients()) {
                if (client == this) {
                    continue;
                }
                this.sendMessage("/connexion " + client.getName());
            }

            this.waitingForFile();

            //Waiting for messages from the client (blocking on inChat.readLine())
            while ((message = chatIn.readLine()) != null) {
                if (message.startsWith("/")) {
                    this.sendMessage(ServerCommand.user(this, message));
                } else {
                    Server.sendAll("<b><font color=blue>&lt;" + name + "&gt;</b> " + message, MessageType.NORMAL);
                }
            }

        } catch (SocketException ignored) {
            //Client leave, the exception doesn't need to be managed

        } catch (IOException e) {
            ServerMain.getFrame().dispError(e, "Erreur lors de l'envoi de messages : " + e.getMessage());

        } finally {
            this.quit();
        }
    }

    public void sendMessage(String message) {
        chatOut.println(message);
        chatOut.flush();
    }

    public void sendData(byte[] b, int off, int len) throws IOException {
        dataOut.write(b, off, len);
        dataOut.flush();
    }

    public void sendString(String data) throws IOException {
        infoDataOut.writeUTF(data);
        infoDataOut.flush();
    }

    //This Thread is waiting for file from the Client
    private void waitingForFile() {
        //The client sends a file, Server sends it to others clients
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataInputStream dataIn = new DataInputStream(ServerClient.this.dataIn);
                    String infos[] = dataIn.readUTF().split("&");
                    String fileName = infos[0];
                    long size = Long.parseLong(infos[1]);

                    ServerMain.getFrame().dispMessage(ServerClient.this.name + " envoie un fichier de " + ServerUtil.toReadableUnit(size) + " nommé \"" + fileName + "\".");

                    byte buff[] = new byte[1024];
                    long total = 0;
                    int data;

                    for (ServerClient client : ServerMain.getClients()) {
                        if (client == ServerClient.this) {
                            continue;
                        }
                        client.sendString(fileName + "&" + size);
                    }

                    while (total < size && (data = ServerClient.this.dataIn.read(buff)) > 0) {
                        for (ServerClient client : ServerMain.getClients()) {
                            if (client == ServerClient.this) {
                                continue;
                            }
                            client.sendData(buff, 0, data);
                        }
                        total += data;
                    }

                    ServerMain.getFrame().dispMessage("\"" + fileName + "\" a été transmis à tous les clients.");

                } catch (EOFException | SocketException ignore) {
                    //Server's ending, ignore it

                } catch (IOException e) {
                    ServerClient.this.sendMessage("Erreur lors de l'envoi du fichier, " + e.getMessage());
                    ServerMain.getFrame().dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage());
                }

                ServerClient.this.waitingForFile();
            }
        }).start();
    }

    public void setName(String name) {
        Server.sendAll("/rename " + this.name + " " + name, MessageType.COMMAND);
        Server.sendAll(this.name + " s'est renommé en " + name + ".", MessageType.INFO);
        ServerMain.getFrame().replaceUser(this.name, name);
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    private void quit() {
        try {
            Server.sendAll(name + " s'est déconnecté.", MessageType.INFO);
            ServerMain.delClient(this);
            if (chatSocket != null) {
                chatSocket.close();
            }
            if (dataSocket != null) {
                dataSocket.close();
            }
            if (dataIn != null) {
                dataIn.close();
            }
            if (dataOut != null) {
                dataOut.close();
            }
            if (infoDataOut != null) {
                infoDataOut.close();
            }
            if (chatOut != null) {
                chatIn.close();
            }
            if (chatOut != null) {
                chatOut.close();
            }
        } catch (IOException e) {
            ServerMain.getFrame().dispError(e, "Erreur lors de la fermeture du client : " + e.getMessage());
        }
    }
}