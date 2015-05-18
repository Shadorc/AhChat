package me.shadorc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.filechooser.FileSystemView;

import me.shadorc.client.frame.ClientFrame;
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

	public static void receiveFile(String name) {
		//Client's Desktop with file's name
		File file = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "\\" + name);	
		try {
			new Transfer(s_data.getInputStream(), new FileOutputStream(file), file).start();
		} catch (IOException e) {
			ClientFrame.showError(e, "Erreur lors du téléchargement : " + e.getMessage());
		}
	}

	public static void sendFile(String path) throws FileNotFoundException {
		//Client's Desktop with file's name
		File file = new File(path);	
		try {
			new Transfer(new FileInputStream(file), s_data.getOutputStream(), file).start();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			ClientFrame.showError(e, "Erreur lors du téléversement : " + e.getMessage());
		}
	}

	public static void exit() {
		try {
			s_chat.close();
			s_data.close();
			emission.close();
			reception.close();
		} catch (IOException | NullPointerException e) {
			ClientFrame.showError(e, "Erreur lors de la fermeture du client : " + e.getMessage());
		}

		Tray.close();
		System.exit(0);
	}
}
