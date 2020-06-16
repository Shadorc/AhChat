package com.shadorc.ahchat.client;

import com.shadorc.ahchat.client.frame.ConnectedPanel;

import javax.swing.JFileChooser;
import java.io.*;
import java.net.SocketException;

public class Reception implements Runnable {

    private final BufferedReader inChat;
    private final InputStream inData;

    public Reception(final BufferedReader inChat, final InputStream inData) {
        this.inChat = inChat;
        this.inData = inData;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            this.waitingForFile();

            String message;
            while ((message = this.inChat.readLine()) != null) {
                if (message.startsWith("/")) {
                    Command.serverCommand(message);
                } else {
                    ConnectedPanel.dispMessage(message);
                }
            }

        } catch (final IOException e) {
            ConnectedPanel.dispError(e, "Le serveur a été fermé.");

        } finally {
            Client.exit(false);
        }
    }

    //This thread is waiting for receiving data
    private void waitingForFile() {
        new Thread(() -> {
            //Send file's informations
            try (final DataInputStream dataIn = new DataInputStream(Reception.this.inData)) {
                final String[] infos = dataIn.readUTF().split("&");

                final String fileName = infos[0];
                final long size = Long.parseLong(infos[1]);

                ConnectedPanel.addProgressBar("Téléchargement", fileName);

                final int index = fileName.lastIndexOf(".");
                final String name = (index > 0) ? fileName.substring(0, index) : fileName;
                final String format = (index > 0) ? fileName.substring(index) : null;

                final JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (chooser.showDialog(null, "Enregistrer \"" + fileName + "\"") == JFileChooser.APPROVE_OPTION) {
                    final File saveFolder = chooser.getSelectedFile();

                    //While the file exists, change name
                    File file = new File(saveFolder + "/" + name + format);
                    for (int i = 1; file.exists(); i++) {
                        file = new File(saveFolder + "/" + name + " (" + i + ")" + format);
                    }

                    try (final FileOutputStream fileWriter = new FileOutputStream(file)) {
                        final byte[] buff = new byte[1024];
                        long total = 0;
                        int data;

                        while (total < size && (data = Reception.this.inData.read(buff)) > 0) {
                            fileWriter.write(buff, 0, data);
                            fileWriter.flush();
                            total += data;
                            ConnectedPanel.updateBar("Téléchargement", fileName, (int) (total * 100 / size));
                        }
                    }
                }

            } catch (final SocketException ignore) {
                //Server's ending, ignore it.

            } catch (final IOException e) {
                ConnectedPanel.dispError(e, "Erreur lors de la réception du fichier, " + e.getMessage());
            }

            Reception.this.waitingForFile();
        }).start();
    }
}