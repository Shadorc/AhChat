package com.shadorc.ahchat.client;

import com.shadorc.ahchat.Util;
import com.shadorc.ahchat.client.frame.Storage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    private static Socket chatSocket;
    private static Socket dataSocket;

    private static Emitter emitter;
    private static Receiver receiver;

    private static InputStream inData;
    private static OutputStream outData;

    private static BufferedReader inChat;
    private static PrintWriter outChat;

    public static boolean connect(final String pseudo, final File icon, final String ip) {

        Storage.getInstance().save(Storage.Data.PSEUDO, pseudo);
        Storage.getInstance().save(Storage.Data.IP, ip);
        Storage.getInstance().save(Storage.Data.ICON, icon.getPath());

        try {
            //Ping server to test if it's reachable
            final Process ping = Runtime.getRuntime().exec("ping -n 1 " + ip);
            ping.waitFor();

            //Connexion successful
            if (ping.exitValue() == 0) {
                Client.chatSocket = new Socket(ip, 15000);
                Client.dataSocket = new Socket(ip, 15001);
            } else {
                return false;
            }

            Client.inChat = new BufferedReader(new InputStreamReader(Client.chatSocket.getInputStream(), StandardCharsets.UTF_8));
            Client.outChat = new PrintWriter(Client.chatSocket.getOutputStream(), false, StandardCharsets.UTF_8);

            Client.inData = Client.dataSocket.getInputStream();
            Client.outData = Client.dataSocket.getOutputStream();

            //Chat's thread
            Client.receiver = new Receiver(Client.inChat, Client.inData);
            Client.receiver.start();

            Client.emitter = new Emitter(Client.outChat, Client.outData);

            Client.sendMessage(pseudo);

            return true;

        } catch (final IOException | InterruptedException e) {
            return false;
        }
    }

    public static void sendMessage(final String message) {
        Client.emitter.sendMessage(message);
    }

    @Deprecated
    public static void sendFile(final File file) {
        Client.emitter.sendFile(file);
    }

    public static void exit(final boolean exit) {
        Util.close(Client.chatSocket);
        Util.close(Client.dataSocket);
        Util.close(Client.inChat);
        Util.close(Client.outChat);
        Util.close(Client.inData);
        Util.close(Client.outData);

        if (exit) {
            System.exit(0);
        }
    }
}
