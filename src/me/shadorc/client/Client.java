package me.shadorc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

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
			//Ping server to test if it's reachable
			Process ping = Runtime.getRuntime().exec("ping -n 1 " + ip);
			ping.waitFor();

			//Connexion successful
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

			emission = new Emission(outChat, outData);
			emission.sendMessage(pseudo);

			return true;

		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	public static void sendMessage(String message) {
		emission.sendMessage(message);
	}

	public static void sendFile(File file) {
		emission.sendFile(file);
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
