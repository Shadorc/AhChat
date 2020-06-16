package com.shadorc.ahchat.client;

import com.shadorc.ahchat.client.frame.ConnectedPanel;

import java.io.*;

public class Emission {

    private final PrintWriter outChat;
    private final OutputStream outData;

    public Emission(final PrintWriter outChat, final OutputStream outData) {
        this.outChat = outChat;
        this.outData = outData;
    }

    public void sendMessage(final String m) {
        this.outChat.println(m);
        this.outChat.flush();
    }

    @Deprecated
    public void sendFile(final File file) {
        new Thread(() -> {

            if (ConnectedPanel.getUsersList().getUsersArray().length == 1) {
                ConnectedPanel.dispMessage("[INFO] Il n'y a personne à qui envoyer ce fichier.");
                return;
            }

            ConnectedPanel.addProgressBar("Envoi", file.getName());

            try (final FileInputStream fileReader = new FileInputStream(file);
                    final DataOutputStream dataOut = new DataOutputStream(Emission.this.outData)) {
                dataOut.writeUTF(file.getName() + "&" + file.length());
                dataOut.flush();

                final byte[] buff = new byte[1024];
                int data;
                int total = 0;

                while ((data = fileReader.read(buff)) > 0) {
                    Emission.this.outData.write(buff, 0, data);
                    Emission.this.outData.flush();
                    total += data;
                    ConnectedPanel.updateBar("Envoi", file.getName(), (int) (total * 100 / file.length()));
                }

            } catch (final IOException e) {
                ConnectedPanel.dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage());
            }
        }).start();
    }
}