package com.shadorc.ahchat.client;

import com.shadorc.ahchat.ThreadPoolManager;
import com.shadorc.ahchat.client.command.*;
import com.shadorc.ahchat.client.frame.ConnectedPanel;
import com.shadorc.ahchat.command.CommandManager;

import javax.swing.JFileChooser;
import java.io.*;
import java.net.SocketException;

public class Receiver {

    private final BufferedReader inChat;
    private final InputStream inData;

    public Receiver(final BufferedReader inChat, final InputStream inData) {
        this.inChat = inChat;
        this.inData = inData;
    }

    public void start() {
        // this.waitsForFile();
        this.waitsForMessage();
    }

    private void waitsForMessage() {
        final CommandManager<ClientCmd> cmdManager = new CommandManager(new ConnectionCmd(),
                new DeconnectionCmd(), new RenameCmd(), new ServerClosedCmd());

        ThreadPoolManager.getInstance().execute(() -> {
            try {
                String message;
                while ((message = this.inChat.readLine()) != null) {
                    if (message.startsWith("/")) {
                        final ClientContext context = new ClientContext(message);
                        final ClientCmd cmd = cmdManager.getCommand(context.getCommandName());
                        if (cmd != null) {
                            cmd.execute(context);
                        }
                    } else {
                        ConnectedPanel.dispMessage(message);
                    }
                }
            } catch (final SocketException ignored) {
                // Client has been closed
                Client.getInstance().disconnect();

            } catch (final IOException err) {
                System.err.println("An error occurred while receiving message: " + err.getMessage());
                err.printStackTrace();
            }
        });
    }

    // This thread is waiting for receiving data
    @Deprecated
    private void waitsForFile() {
        ThreadPoolManager.getInstance().execute(() -> {
            //Send file's informations
            try (final DataInputStream dataIn = new DataInputStream(Receiver.this.inData)) {
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

                        while (total < size && (data = Receiver.this.inData.read(buff)) > 0) {
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

            Receiver.this.waitsForFile();
        });
    }
}