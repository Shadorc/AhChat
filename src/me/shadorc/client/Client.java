package me.shadorc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import me.shadorc.client.frame.Tray;

public class Client {

	private static Socket s_chat = new Socket();
	//	private static Socket s_data = new Socket();

	private static Emission emission = null;
	private static Reception reception = null;

	public static boolean launch(String pseudo, String ip) {

		try {
			//Ping server to test if it is reachable
			Process p1 = Runtime.getRuntime().exec("ping -n 1 " + ip);
			p1.waitFor();

			if(p1.exitValue() == 0) {
				s_chat = new Socket(ip, 15000);
				//				s_data = new Socket(ip, 15001);
			} else {
				return false;
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));

			//Lance le Thread de chat.
			reception = new Reception(in);
			new Thread(reception).start();

			emission = new Emission(new PrintWriter(s_chat.getOutputStream()));
			Emission.sendMessage(pseudo);

			return true;

		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	//	public static void receiveFile() throws FileNotFoundException, IOException {
	//		File file = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "\\test.wmv");	//Le bureau du client avec le nom du fichier récéptionné.
	//
	//		//Lance le Thread de transfert.
	//		Thread tr = new Thread(new Transfert(s_data.getInputStream(), new FileOutputStream(file), file));
	//		tr.start();
	//	}

	public static void exit() {

		try {
			s_chat.close();
			//			s_data.close();
			emission.close();
			reception.close();
		} catch (IOException | NullPointerException e) {
			JOptionPane.showMessageDialog(null, "Erreur lors de la fermeture du client : " + e.toString(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}

		Tray.close();
		System.exit(0);
	}
}
