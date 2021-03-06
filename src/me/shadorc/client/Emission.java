package me.shadorc.client;

import me.shadorc.client.frame.ConnectedPanel;

import java.io.*;

public class Emission {

    private final PrintWriter outChat;
    private final OutputStream outData;

    public Emission(PrintWriter outChat, OutputStream outData) {
        this.outChat = outChat;
        this.outData = outData;
    }

    public void sendMessage(String m) {
        outChat.println(m);
        outChat.flush();
    }

    public void sendFile(File file) {
        new Thread(() -> {

            if (ConnectedPanel.getUsersList().getUsersArray().length == 1) {
                ConnectedPanel.dispMessage("[INFO] Il n'y a personne à qui envoyer ce fichier.");
                return;
            }

            FileInputStream fileReader = null;

            try {
                ConnectedPanel.addProgressBar("Envoi", file.getName());

                fileReader = new FileInputStream(file);

                DataOutputStream dataOut = new DataOutputStream(outData);
                dataOut.writeUTF(file.getName() + "&" + file.length());
                dataOut.flush();

                byte[] buff = new byte[1024];
                int data;
                int total = 0;

                while ((data = fileReader.read(buff)) > 0) {
                    outData.write(buff, 0, data);
                    outData.flush();
                    total += data;
                    ConnectedPanel.updateBar("Envoi", file.getName(), (int) (total * 100 / file.length()));
                }

            } catch (IOException e) {
                ConnectedPanel.dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage());

            } finally {
                try {
                    if (fileReader != null) fileReader.close();
                } catch (IOException e) {
                    ConnectedPanel.dispError(e, "Erreur lors de la fermeture de l'envoi du fichier, " + e.getMessage());
                }
            }
        }).start();
    }
}