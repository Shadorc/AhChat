package me.shadorc.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import me.shadorc.client.Client;
import me.shadorc.client.frame.Frame;

public class ServerFrame extends JFrame implements KeyListener, FocusListener {

	private static final long serialVersionUID = 1L;

	private String DEFAULT_TEXT = "Envoyer un message";

	private static boolean isOpen = false;

	private static HTMLEditorKit kit;
	private static HTMLDocument doc;
	private static JTextArea users;
	private JFormattedTextField textField;

	private Server serv;

	public ServerFrame() {
		super("Serveur");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				serv.stop();
				isOpen = false;
				if(!Frame.isOpen()) {
					Client.exit(true);
				}
			}
		});

		isOpen = true;
		kit = new HTMLEditorKit();
		doc = new HTMLDocument();
		textField = new JFormattedTextField(DEFAULT_TEXT);

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JTextPane chat = new JTextPane();
		chat.setEditorKit(kit);
		chat.setDocument(doc);
		chat.setEditable(false);
		((DefaultCaret) chat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scroll = new JScrollPane(chat, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.add(scroll, BorderLayout.CENTER);

		users = new JTextArea();
		users.setEditable(false);
		users.setBorder(BorderFactory.createLoweredBevelBorder());
		users.setPreferredSize(new Dimension((int) (Frame.getDimension().getWidth()/4), 0));
		pane.add(users, BorderLayout.EAST);

		textField.setPreferredSize(new Dimension(0, 25));
		textField.addKeyListener(this);
		textField.addFocusListener(this);
		pane.add(textField, BorderLayout.PAGE_END);

		this.setContentPane(pane);
		this.pack();
		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		serv = new Server();
		serv.start();
	}

	public static void addUser(String name) {
		users.append(name + "\n");
	}

	public static void removeUser(String name) {
		users.setText(users.getText().replace(name, ""));
	}

	public static void replaceUser(String oldName, String newName) {
		users.setText(users.getText().replace(oldName, newName));
	}

	public static void showError(Exception e, String error) {
		JOptionPane.showMessageDialog(null, error, "Serveur - Erreur", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	public static void dispMessage(String message) {
		disp("<font size=4>" + message + "</font>");
	}

	public static void dispError(Exception e, String error) {
		disp("<b><i><font color=red size=4> /!\\ " + error + " /!\\\n</b></i></font>");
		e.printStackTrace();
	}

	private static void disp(String message) {
		try {
			kit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			showError(e, "Erreur lors de l'affichage du message : " + e.getMessage());
		}
	}

	public static void split() {
		ServerFrame.dispMessage("--------");
	}

	public static boolean isOpen() {
		return isOpen;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		if(textField.getText().equals(DEFAULT_TEXT)) {
			textField.setText(null);
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		if(textField.getText().isEmpty()) {
			textField.setText(DEFAULT_TEXT);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		String text = textField.getText();
		if(e.getKeyCode() == KeyEvent.VK_ENTER && text.length() > 0) {
			if(text.startsWith("/")) {
				ServerCommand.admin(text);
			} else {
				Server.sendAll("<b><font color='black'>[SERVER] : </b>" + text, Server.Type.MESSAGE);
			}
			textField.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}