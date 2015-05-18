package me.shadorc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import me.shadorc.client.frame.Frame;
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

			BufferedReader inData = new BufferedReader(new InputStreamReader(s_data.getInputStream()));
			BufferedReader inChat = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));
			PrintWriter outChat = new PrintWriter(s_chat.getOutputStream());

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

	public static void sendFile(String path) throws FileNotFoundException {
		//Client's Desktop with file's name
		File file = new File(path);	
		try {
			new Transfer(new FileInputStream(file), s_data.getOutputStream(), file).start();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			Frame.showError(e, "Erreur lors du téléversement : " + e.getMessage());
		}
	}

	public static void exit() {
		try {
			s_chat.close();
			s_data.close();
			emission.close();
			reception.close();
		} catch (IOException | NullPointerException e) {
			Frame.showError(e, "Erreur lors de la fermeture du client : " + e.getMessage());
		}

		Tray.close();
		System.exit(0);
	}
}
