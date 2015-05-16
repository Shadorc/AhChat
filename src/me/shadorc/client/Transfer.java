package me.shadorc.client;
//
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.io.File;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.OutputStream;
//
// import javax.swing.Timer;
//
// import me.shadorc.frame.ConnectedPanel;
//
//public class Transfer /*implements Runnable*/ {
//
// private InputStream in = null;
// private OutputStream out = null;
// private File file = null;
// private int secondes = 0;
// private long lastLenght = 0;
//
// Timer timer = new Timer(1000, new ActionListener() {
// public void actionPerformed(ActionEvent e) {
// secondes++;
// if(secondes%10 == 0)
// ConnectedPanel.dispMessage("[INFO] " + file.length()/1024 + "ko reçus || " +
// lastLenght/1000 + " ko/s.");
// lastLenght = file.length() - lastLenght; //Octets téléchargés en 1s
// }
// });
//
// Transfer(InputStream in, OutputStream out, File file) {
// this.file = file;
// this.in = in;
// this.out = out;
// }
//
// public void run() {
//
// byte buf[] = new byte[1024];
// int n;
//
// ConnectedPanel.dispMessage("[INFO] Transfert en cours.");
//
// timer.start();
//
// try {
// while((n = in.read(buf)) != -1)
// out.write(buf, 0, n);
//
// } catch (IOException e) {
// ConnectedPanel.dispError("Erreur lors de la récéption du fichier, " +
// e.toString() + ", annulation.");
//
// } finally {
// ConnectedPanel.dispError("[INFO] Transfert finis.");
// try {
// timer.stop();
// out.close();
// out.flush();
// in.close();
// } catch (IOException e) {
// ConnectedPanel.dispError("Erreur lors de la fin du transfert des données : "
// + e.toString());
// }
// }
// }
//}