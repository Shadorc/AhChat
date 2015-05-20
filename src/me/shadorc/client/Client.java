package me.shadorc.client;

import java.io.*;
import java.net.Socket;

import me.shadorc.client.frame.ConnectedPanel;
import me.shadorc.client.frame.Frame;
import me.shadorc.client.frame.Tray;

public class Client {

	private static Socket s_chat, s_data;

	private static Emission emission;
	private static Reception reception;

	private static InputStream inData;
	private static OutputStream outData;

	private static BufferedReader inChat;
	private static PrintWriter outChat;

	public static boolean connect(String pseudo, String ip) {

		try {
			//Ping server to test if it is reachable
			Process ping = Runtime.getRuntime().exec("ping -n 1 " + ip);
			ping.waitFor();

			if(ping.exitValue() == 0) {
				s_chat = new Socket(ip, 15000);
				s_data = new Socket(ip, 15001);
			} else {
				return false;
			}

			inChat = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));
			outChat = new PrintWriter(s_chat.getOutputStream());

			inData = s_data.getInputStream();
			outData = s_data.getOutputStream();

			//Chat's thread
			reception = new Reception(inChat, inData);
			reception.start();

			emission = new Emission(outChat);
			emission.sendMessage(pseudo);

			return true;

		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	public static void sendMessage(String message) {
		emission.sendMessage(message);
	}

	public static void sendFile(File file) throws FileNotFoundException {
		new Thread(new Runnable() {
			@Override
			public void run() {

				if(ConnectedPanel.getUsers().size() == 1) {
					ConnectedPanel.dispMessage("[INFO] Il n'y a personne à qui envoyer ce fichier.");
					return;
				}

				byte buff[] = new byte[1024];
				int data;

				DataOutputStream dataOut = null;
				FileInputStream fileReader = null;

				try {
					fileReader = new FileInputStream(file);

					ConnectedPanel.dispMessage("[INFO] Client : Envoi de " + file.getName() + " en cours...");

					dataOut = new DataOutputStream(outData);
					dataOut.writeLong(file.length());
					dataOut.writeUTF(file.getName());
					dataOut.flush();

					while((data = fileReader.read(buff)) != -1) {
						outData.write(buff, 0, data);
						outData.flush();
					}

					ConnectedPanel.dispMessage("[INFO] Client : " + file.getName() + " envoyé !");

				} catch (FileNotFoundException e) {
					ConnectedPanel.dispError(e, "Merci d'entrer un chemin de fichier valide.");

				} catch (IOException e) {
					ConnectedPanel.dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage() + ".");

				} finally {
					try {
						if(fileReader != null) {
							fileReader.close();
						}
					} catch (IOException e) {
						ConnectedPanel.dispError(e, "Erreur lors de la fermeture de l'envoi du fichier, " + e.getMessage() + ".");
					}
				}
			}
		}).start();
	}

	public static void exit() {
		try {
			s_chat.close();
			s_data.close();
			inChat.close();
			inData.close();
			outChat.close();
			outData.close();
		} catch (IOException | NullPointerException e) {
			Frame.showError(e, "Erreur lors de la fermeture du client : " + e.getMessage());
		}

		Tray.close();
		System.exit(0);
	}
}
