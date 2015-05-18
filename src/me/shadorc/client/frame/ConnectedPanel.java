package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import me.shadorc.client.Client;

public class ConnectedPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static HTMLEditorKit kit = new HTMLEditorKit();
	private static HTMLDocument doc = new HTMLDocument();

	private static JTextArea users = new JTextArea();

	public ConnectedPanel() {

		super(new BorderLayout());

		JTextPane chat = new JTextPane();
		chat.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {}

			@Override
			public void changedUpdate(DocumentEvent arg0) {}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				Tray.alerteMessage();
			}
		});

		chat.setEditable(false);
		chat.setEditorKit(kit);
		chat.setDocument(doc);
		chat.setBorder(BorderFactory.createEmptyBorder());
		chat.setContentType("text/html");
		((DefaultCaret) chat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scroll = new JScrollPane(chat, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setOpaque(false);
		this.add(scroll, BorderLayout.CENTER);

		users.setEditable(false);
		users.setBorder(BorderFactory.createLoweredBevelBorder());
		users.setPreferredSize(new Dimension((int) (ClientFrame.getDimension().getWidth()/4), 0));
		this.add(users, BorderLayout.EAST);

		JFormattedTextField saisisTexte = new JFormattedTextField();
		saisisTexte.setPreferredSize(new Dimension((int) ClientFrame.getDimension().getWidth(), 25));
		saisisTexte.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = saisisTexte.getText();

					if(message.equals("/quit")) {
						Client.exit();
					} else if(message.startsWith("/send")) {
						try {
							if(message.split(" ").length != 2) throw new FileNotFoundException("Chemin du fichier non spécifié.");
							Client.sendFile(message.split(" ")[1]);
						} catch (FileNotFoundException e1) {
							ConnectedPanel.dispError("Merci d'entrer le chemin du fichier à envoyer.");
						}
					} else if(message.length() > 0) {
						Client.sendMessage(message);
					}

					saisisTexte.setText("");
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
		this.add(saisisTexte, BorderLayout.PAGE_END);
	}

	public static void addUser(String user) {
		if(!Arrays.asList(users.getText().split("\n")).contains(user)) {
			users.append(user + "\n");
		}
	}

	public static void removeUser(String user) {
		users.setText(users.getText().replace(user, ""));
	}

	public static void replaceUser(String oldName, String newName) {
		users.setText(users.getText().replace(oldName, newName));
	}

	public static void dispMessage(String message) {
		disp("<font size=4>" + message + "</font>");
	}

	public static void dispError(String error) {
		disp("<b><i><font color='red' size=4> /!\\ " + error + " /!\\\n</b></i></font>");
	}

	private static void disp(String text) {
		try {
			kit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			ClientFrame.showError(e, "Une erreur est survenue lors de l'affichage du message : " + e.getMessage());
		}
	}
}
