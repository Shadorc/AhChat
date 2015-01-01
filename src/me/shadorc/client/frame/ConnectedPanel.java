package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
import me.shadorc.client.Emission;

public class ConnectedPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static HTMLEditorKit kit = new HTMLEditorKit();
	private static HTMLDocument doc = new HTMLDocument();

	private static JTextArea users = new JTextArea();

	ConnectedPanel(JFrame frame) {

		super(new BorderLayout());

		new Command(users);

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
		users.setPreferredSize(new Dimension(frame.getWidth()/4, 0));
		this.add(users, BorderLayout.EAST);

		final JFormattedTextField saisisTexte = new JFormattedTextField();
		saisisTexte.setPreferredSize(new Dimension(frame.getWidth(), 25));
		saisisTexte.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = saisisTexte.getText();

					if(message.equals("/quit")) {
						Client.exit();
					} else if(message.length() > 0) {
						Emission.sendMessage(message);
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

	public static void dispMessage(String message) {
		try {
			kit.insertHTML(doc, doc.getLength(), "<font size=4>" + message + "</font>", 0, 0, null);
		} catch (BadLocationException | IOException e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void dispError(String error) {
		try {
			kit.insertHTML(doc, doc.getLength(), "<b><i><font color='red' size=4> /!\\ " + error + " /!\\\n</b></i></font>", 0, 0, null);
		} catch (BadLocationException | IOException e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
