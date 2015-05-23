package me.shadorc.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import me.shadorc.server.Server.Type;

public class ServerClient implements Runnable {

	private Socket s_chat;
	private Socket s_data;

	private InputStream inData;
	private OutputStream outData;
	private DataOutputStream outInfoData;

	private BufferedReader inChat;
	private PrintWriter outChat;

	private String name;
	private String ip;

	public ServerClient(Socket s_chat, Socket s_data) {

		this.s_chat = s_chat;
		this.s_data = s_data;

		this.name = "Unknown";
		this.ip = s_chat.getRemoteSocketAddress().toString();

		try {
			outChat = new PrintWriter(s_chat.getOutputStream());
			inChat = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));

			outData = s_data.getOutputStream();
			inData = s_data.getInputStream();
			outInfoData = new DataOutputStream(outData); 

			name = inChat.readLine();

			//			If pseudo already exists, add number while pseudo exists (ex: Shadorc, Shadorc(1), Shadorc(2), ...)
			//						for(int i = 1; Server.getClients().containsKey((name)); i++) {
			//							name = name + "(" + i + ")";
			//						}

			Server.addClient(this);

			new Thread(this).start();

		} catch (IOException e) {
			ServerFrame.dispError(e, "Erreur lors de la création du client : " + e.getMessage());
			this.quit();
		}
	}

	@Override
	public void run() {
		String message;

		try {
			this.sendMessage("<b><font color=18B00A>* * Bienvenue ! Pour de l'aide entrer /help.");

			Server.sendAll(name + " vient de se connecter.", Type.INFO);

			//Send the list of all connected people
			for(ServerClient client : Server.getClients()) {
				if(client == ServerClient.this) continue;
				this.sendMessage("/connexion " + client.getName());
			}

			this.waitingForFile();

			//Waiting for messages from the client (blocking on inChat.readLine())
			while((message = inChat.readLine()) != null) {
				if(message.startsWith("/")) {
					this.sendMessage(ServerCommand.user(this, message));
				} else {
					Server.sendAll("<b><font color=blue>&lt;" + name + "&gt;</b> " + message, Type.MESSAGE);
				}
			}

		} catch (SocketException ignored) {
			//Client leave, the exception doesn't need to be managed

		} catch (IOException e) {
			ServerFrame.dispError(e, "Erreur lors de l'envoi de messages : " + e.getMessage());

		} finally {
			this.quit();
		}
	}

	public void sendMessage(String message) {
		outChat.println(message);
		outChat.flush();
	}

	public void sendData(byte[] b, int off, int len) throws IOException {
		outData.write(b, off, len);
		outData.flush();
	}

	public void sendLong(long data) throws IOException {
		outInfoData.writeLong(data);
		outInfoData.flush();
	}

	public void sendString(String data) throws IOException {
		outInfoData.writeUTF(data);
		outInfoData.flush();
	}

	//This Thread is waiting for file from the Client
	private void waitingForFile() {
		//The client sends a file, Server sends it to others clients
		new Thread(new Runnable() {
			@Override
			public void run() {

				DataInputStream dataIn = null;

				try {
					dataIn = new DataInputStream(inData);
					long size = dataIn.readLong();
					String fileName = dataIn.readUTF();

					ServerFrame.dispMessage(ServerClient.this.name + " envoie un fichier de " + size/1024 + "ko nommé " + fileName + ".");

					byte buff[] = new byte[1024];
					long total = 0;
					int data; 

					while(total < size && (data = inData.read(buff)) > 0) {
						for(ServerClient client : Server.getClients()) {
							if(client == ServerClient.this) continue;

							client.sendLong(size);
							client.sendString(fileName);
							client.sendData(buff, 0, data);
						}
						total += data;
					}

					ServerFrame.dispMessage("Fichier reçu et transmis à tous les clients.");

				} catch(EOFException | SocketException ignore) {
					//Server's ending, ignore it

				} catch (IOException e) {
					ServerClient.this.sendMessage("Erreur lors de l'envoi du fichier, " + e.getMessage());
					ServerFrame.dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage());
				}

				ServerClient.this.waitingForFile();
			}
		}).start();
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		Server.sendAll("/rename " + this.name + " " + name, Type.COMMAND);
		Server.sendAll(this.name + " s'est renommé en " + name + ".", Type.INFO);
		this.name = name;
	}

	private void quit() {
		try {
			Server.sendAll(name + " s'est déconnecté.", Type.INFO);
			Server.delClient(this);
			s_chat.close();
			s_data.close();
			inData.close();
			outData.close();
			outInfoData.close();
			inChat.close();
			outChat.close();
		} catch (IOException | NullPointerException e) {
			ServerFrame.dispError(e, "Erreur lors de la fermeture du client : " + e.getMessage());
		}
	}
}