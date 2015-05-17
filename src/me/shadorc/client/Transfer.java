package me.shadorc.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.Timer;

import me.shadorc.client.frame.ConnectedPanel;

public class Transfer implements Runnable {

	private InputStream in;
	private OutputStream out;

	private Timer timer;

	private int seconds = 0;
	private long lastLenght = 0;

	public Transfer(InputStream in, OutputStream out, File file) {
		this.in = in;
		this.out = out;

		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				seconds++;
				if(seconds%10 == 0) {
					ConnectedPanel.dispMessage("[INFO] " + file.length()/1024 + "ko reçus || " + lastLenght/1000 + " ko/s.");
				}
				lastLenght = file.length() - lastLenght; //Bytes downloaded in 1s
			}
		});
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		byte buff[] = new byte[1024];
		int data;

		ConnectedPanel.dispMessage("[INFO] Transfert en cours.");

		timer.start();

		try {
			while((data = in.read(buff)) != -1) {
				out.write(buff, 0, data);
			}

		} catch (IOException e) {
			ConnectedPanel.dispError("Erreur lors de la récéption du fichier, " + e.getMessage() + ", annulation.");

		} finally {
			ConnectedPanel.dispError("[INFO] Transfert fini.");
			try {
				timer.stop();
				out.close();
				out.flush();
				in.close();
			} catch (IOException e) {
				ConnectedPanel.dispError("Erreur lors de la fin du transfert des données : " + e.getMessage());
			}
		}
	}
}