package com.shadorc.ahchat.server;

import com.shadorc.ahchat.Util;
import com.shadorc.ahchat.command.BaseCmd;
import com.shadorc.ahchat.command.Context;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
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
            this.chatOut = new PrintWriter(chatSocket.getOutputStream(), false, StandardCharsets.UTF_8);
            this.chatIn = new BufferedReader(new InputStreamReader(chatSocket.getInputStream(), StandardCharsets.UTF_8));

            this.dataOut = dataSocket.getOutputStream();
            this.dataIn = dataSocket.getInputStream();

            this.infoDataOut = new DataOutputStream(this.dataOut);

            // Waits for name to arrive
            this.name = this.chatIn.readLine();

            // TODO: Improve this
            // Rename the client if someone has already this name
            for (final ServerClient client : ServerManager.getInstance().getClients()) {
                if (client.getName().equalsIgnoreCase(this.name)) {
                    this.name = this.name + "_(" + ThreadLocalRandom.current().nextInt(10) + ")";
                }
            }

            ServerManager.getInstance().addClient(this);

            new Thread(this).start();

        } catch (final IOException err) {
            ServerManager.getInstance().getFrame()
                    .dispError(err, "Erreur lors de la création du client : " + err.getMessage());
            this.quit();
        }
    }

    @Override
    public void run() {
        try {
            this.sendMessage("<b><font color=18B00A>* * Bienvenue ! Pour afficher l'aide, entrez /help.");
            Server.sendAll(this.name + " vient de se connecter.", Server.MessageType.INFO);

            //Sends the list of all connected people
            for (final ServerClient client : ServerManager.getInstance().getClients()) {
                if (client == this) {
                    continue;
                }
                this.sendMessage("/connexion " + client.getName());
            }

            this.listenForFiles();
            this.listenForMessages();
        } catch (final SocketException ignored) {
            // Client left, the exception doesn't need to be managed
        } catch (final IOException err) {
            ServerManager.getInstance().getFrame()
                    .dispError(err, "Erreur lors de l'envoi de messages : " + err.getMessage());
        } finally {
            this.quit();
        }
    }

    public void sendMessage(final String message) {
        this.chatOut.println(message);
        this.chatOut.flush();
    }

    public void sendData(final byte[] b, final int off, final int len) throws IOException {
        this.dataOut.write(b, off, len);
        this.dataOut.flush();
    }

    private void sendFileInfo(final String fileName, final long fileSize) throws IOException {
        this.infoDataOut.writeUTF(String.format("%s&%d", fileName, fileSize));
        this.infoDataOut.flush();
    }

    private void listenForMessages() throws IOException {
        String message = null;
        //Waiting for messages from the client (blocking on inChat.readLine())
        while ((message = this.chatIn.readLine()) != null) {
            if (message.startsWith("/")) {
                final Context context = new Context(this, message);
                final BaseCmd cmd = CommandManager.getInstance().getCommand(context.getCommandName());
                if (cmd != null) {
                    cmd.execute(context);
                }
            } else {
                Server.sendAll("<b><font color=blue>&lt;" + this.name + "&gt;</b> " + message, Server.MessageType.NORMAL);
            }
        }
    }

    // This Thread is waiting for file from the Client
    private void listenForFiles() {
        // The client sends a file, Server sends it to others clients
        new Thread(() -> {
            try (final DataInputStream dataIn = new DataInputStream(ServerClient.this.dataIn)) {
                final String[] infos = dataIn.readUTF().split("&");
                final String fileName = infos[0];
                final long size = Long.parseLong(infos[1]);

                ServerManager.getInstance().getFrame()
                        .dispMessage(ServerClient.this.name + " envoie un fichier de " + Util.toReadableUnit(size) + " nommé " +
                                "\"" + fileName + "\".");

                final byte[] buff = new byte[1024];
                long total = 0;
                int data;

                for (final ServerClient client : ServerManager.getInstance().getClients()) {
                    if (client == ServerClient.this) {
                        continue;
                    }
                    client.sendFileInfo(fileName, size);
                }

                while (total < size && (data = ServerClient.this.dataIn.read(buff)) > 0) {
                    for (final ServerClient client : ServerManager.getInstance().getClients()) {
                        if (client == ServerClient.this) {
                            continue;
                        }
                        client.sendData(buff, 0, data);
                    }
                    total += data;
                }

                ServerManager.getInstance().getFrame()
                        .dispMessage("\"" + fileName + "\" a été transmis à tous les clients.");

            } catch (final EOFException | SocketException ignore) {
                //Server's ending, ignore it

            } catch (final IOException e) {
                ServerClient.this.sendMessage("Erreur lors de l'envoi du fichier, " + e.getMessage());
                ServerManager.getInstance().getFrame()
                        .dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage());
            }

            ServerClient.this.listenForFiles();
        }).start();
    }

    public void setName(final String name) {
        Server.sendAll("/rename " + this.name + " " + name, Server.MessageType.COMMAND);
        Server.sendAll(this.name + " s'est renommé en " + name + ".", Server.MessageType.INFO);
        ServerManager.getInstance().getFrame().replaceUser(this.name, name);
        this.name = name;
    }

    public String getIp() {
        return this.ip;
    }

    public String getName() {
        return this.name;
    }

    private void quit() {
        Server.sendAll(this.name + " s'est déconnecté.", Server.MessageType.INFO);
        ServerManager.getInstance().removeClient(this);
        Util.close(this.chatSocket);
        Util.close(this.dataSocket);
        Util.close(this.infoDataOut);
        Util.close(this.dataIn);
        Util.close(this.dataOut);
        Util.close(this.chatIn);
        Util.close(this.chatOut);
    }
}