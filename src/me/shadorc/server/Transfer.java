//package me.shadorc.server;
//
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.OutputStream;
//
//class Transfer /*implements Runnable*/ {
//
// private InputStream in = null;
// private OutputStream out = null;
//
// Transfer(InputStream in, OutputStream out) {
// this.in = in;
// this.out = out;
// }
//
// public void run() {
//
// byte buf[] = new byte[1024];
// int n;
//
// Frame.dispMessage("[INFO] Transfert en cours.");
//
// try {
// while((n = in.read(buf)) != -1)
// out.write(buf, 0, n);
//
// } catch (IOException e) {
// Frame.dispError("Erreur lors de l'écriture des données : " + e.toString());
//
// } finally {
// Frame.dispMessage("[INFO] Transfert finis.");
// try {
// in.close();
// out.flush();
// out.close();
// } catch (IOException e) {
// Frame.dispError("Erreur lors de la fermeture du lecteur de données : " +
// e.toString());
// }
// }
// }
//}
