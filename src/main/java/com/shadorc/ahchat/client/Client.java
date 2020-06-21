package com.shadorc.ahchat.client;

import com.shadorc.ahchat.Util;
import com.shadorc.ahchat.client.frame.Storage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    private static Client instance;

    static {
        Client.instance = new Client();
    }

    private String ip;
    private String pseudo;
    private File icon;

    private Socket chatSocket;
    private Socket dataSocket;
    private Emitter emitter;
    private Receiver receiver;

    public boolean connect(final String ip, final String pseudo, final File icon) {
        this.ip = ip;
        this.pseudo = pseudo;
        this.icon = icon;

        Storage.getInstance().save(Storage.Data.PSEUDO, pseudo);
        Storage.getInstance().save(Storage.Data.IP, ip);
        Storage.getInstance().save(Storage.Data.ICON, icon.getPath());

        try {
            // Ping server to test if it's reachable
            final Process ping = Runtime.getRuntime().exec("ping -n 1 " + ip);
            ping.waitFor();

            if (ping.exitValue() != 0) {
                return false;
            }

            this.chatSocket = new Socket(this.ip, 15000);
            this.dataSocket = new Socket(this.ip, 15001);

            final BufferedReader inChat = new BufferedReader(
                    new InputStreamReader(this.chatSocket.getInputStream(), StandardCharsets.UTF_8));
            final PrintWriter outChat = new PrintWriter(this.chatSocket.getOutputStream(), false, StandardCharsets.UTF_8);

            final InputStream inData = this.dataSocket.getInputStream();
            final OutputStream outData = this.dataSocket.getOutputStream();

            this.emitter = new Emitter(outChat, outData);
            this.receiver = new Receiver(inChat, inData);
            this.receiver.start();

            this.emitter.sendMessage(pseudo);

        } catch (final IOException | InterruptedException err) {
            return false;
        }

        return true;
    }

    public Emitter getEmitter() {
        return this.emitter;
    }

    public void disconnect() {
        Util.close(this.chatSocket);
        Util.close(this.dataSocket);
    }

    public static Client getInstance() {
        return Client.instance;
    }

}
