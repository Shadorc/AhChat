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
import me.shadorc.client.frame.Storage;
import me.shadorc.client.frame.Storage.Data;

public class Client {

	private static Socket s_chat, s_data;

	private static Emission emission;
	private static Reception reception;

	private static InputStream inData;
	private static OutputStream outData;

	private static BufferedReader inChat;
	private static PrintWriter outChat;

	public static boolean connect(String pseudo, File icon, String ip) {

		Storage.saveData(Data.PSEUDO, pseudo);
		Storage.saveData(Data.IP, ip);
		Storage.saveData(Data.ICON, icon.getPath());

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

			sendMessage(pseudo);

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

	public static void exit(boolean closeWindow) {
		try {
			if(s_chat != null) s_chat.close();
			if(s_data != null) s_data.close();
			if(inChat != null) inChat.close();
			if(inData != null) inData.close();
			if(outChat != null) outChat.close();
			if(outData != null) outData.close();
		} catch (IOException e) {
			Frame.popupError(e, "Erreur lors de la fermeture du client : " + e.getMessage());
		}

		if(closeWindow) {
			System.exit(0);
		}
	}
}
