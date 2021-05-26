package me.shadorc.client;

import me.shadorc.client.frame.ConnectedPanel;

import javax.swing.*;
import java.io.*;
import java.net.SocketException;

public class Reception implements Runnable {

    private BufferedReader inChat;
    private InputStream inData;

    public Reception(BufferedReader inChat, InputStream inData) {
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
            while ((message = inChat.readLine()) != null) {
                if (message.startsWith("/")) {
                    Command.serverCommand(message);
                } else {
                    ConnectedPanel.dispMessage(message);
                }
            }

        } catch (IOException e) {
            ConnectedPanel.dispError(e, "Le serveur a été fermé.");

        } finally {
            Client.exit(false);
        }
    }

    //This thread is waiting for receiving data
    private void waitingForFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream fileWriter = null;

                try {
                    //Send file's informations
                    DataInputStream dataIn = new DataInputStream(inData);
                    String[] infos = dataIn.readUTF().split("&");

                    String fileName = infos[0];
                    long size = Long.parseLong(infos[1]);

                    ConnectedPanel.addProgressBar("Téléchargement", fileName);

                    int index = fileName.lastIndexOf(".");
                    String name = (index > 0) ? fileName.substring(0, index) : fileName;
                    String format = (index > 0) ? fileName.substring(index) : null;

                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (chooser.showDialog(null, "Enregistrer \"" + fileName + "\"") == JFileChooser.APPROVE_OPTION) {
                        File saveFolder = chooser.getSelectedFile();

                        //While the file exists, change name
                        File file = new File(saveFolder + "/" + name + format);
                        for (int i = 1; file.exists(); i++) {
                            file = new File(saveFolder + "/" + name + " (" + i + ")" + format);
                        }

                        fileWriter = new FileOutputStream(file);

                        byte buff[] = new byte[1024];
                        long total = 0;
                        int data;

                        while (total < size && (data = inData.read(buff)) > 0) {
                            fileWriter.write(buff, 0, data);
                            fileWriter.flush();
                            total += data;
                            ConnectedPanel.updateBar("Téléchargement", fileName, (int) (total * 100 / size));
                        }
                    }

                } catch (SocketException ignore) {
                    //Server's ending, ignore it.

                } catch (IOException e) {
                    ConnectedPanel.dispError(e, "Erreur lors de la réception du fichier, " + e.getMessage());

                } finally {
                    try {
                        if (fileWriter != null) fileWriter.close();
                    } catch (IOException e) {
                        ConnectedPanel.dispError(e, "Erreur lors de la fermeture de la réception du fichier, " + e.getMessage());
                    }
                }

                Reception.this.waitingForFile();
            }
        }).start();
    }
}