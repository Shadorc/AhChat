package me.shadorc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import me.shadorc.client.frame.Tray;

public class Client {

	private static Socket s_chat;
	private static Socket s_data;

	private static Emission emission;
	private static Reception reception;

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

			BufferedReader in = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));
			PrintWriter out = new PrintWriter(s_chat.getOutputStream());

			//Chat's thread
			reception = new Reception(in);
			reception.start();

			emission = new Emission(out);
			emission.sendMessage(pseudo);

			return true;

		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
	
	public static void sendMessage(String message) {
		emission.sendMessage(message);
	}

	public static void receiveFile() throws FileNotFoundException, IOException {
		//Client's Desktop with file's name
		File file = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "\\test.jpg");	
		new Transfer(s_data.getInputStream(), new FileOutputStream(file), file).start();
	}

	public static void exit() {
		try {
			s_chat.close();
			s_data.close();
			emission.close();
			reception.close();
		} catch (IOException | NullPointerException e) {
			JOptionPane.showMessageDialog(null, "Erreur lors de la fermeture du client : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}

		Tray.close();
		System.exit(0);
	}
}
