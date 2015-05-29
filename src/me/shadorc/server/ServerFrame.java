package me.shadorc.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import me.shadorc.client.Client;
import me.shadorc.client.Command;
import me.shadorc.client.frame.Frame;
import me.shadorc.client.frame.UserList;

public class ServerFrame extends JFrame implements KeyListener, FocusListener {

	private static final long serialVersionUID = 1L;

	private String DEFAULT_TEXT = "Envoyer un message";

	private static boolean isOpen = false;

	private JFormattedTextField inputField;
	private static HTMLEditorKit kit;
	private static HTMLDocument doc;

	private static JList <String> serverInfos;
	private static UserList usersList;

	private Server serv;

	public ServerFrame() {
		super("AhChat - Serveur");
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

		inputField = new JFormattedTextField(DEFAULT_TEXT);
		kit = new HTMLEditorKit();
		doc = new HTMLDocument();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JTextPane chatPane = new JTextPane();
		chatPane.setEditorKit(kit);
		chatPane.setDocument(doc);
		chatPane.setEditable(false);
		((DefaultCaret) chatPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scroll = new JScrollPane(chatPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(scroll, BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new GridLayout(2, 0));
		rightPanel.setPreferredSize(new Dimension((int) (Frame.getDimension().getWidth()/4), 0));

		usersList = new UserList();
		usersList.setBorder(BorderFactory.createLoweredBevelBorder());
		rightPanel.add(usersList);

		serverInfos = new JList <String> ();
		serverInfos.setBorder(BorderFactory.createLoweredBevelBorder());
		rightPanel.add(serverInfos);

		mainPanel.add(rightPanel, BorderLayout.EAST);

		inputField.setPreferredSize(new Dimension(0, 25));
		inputField.addKeyListener(this);
		inputField.addFocusListener(this);
		mainPanel.add(inputField, BorderLayout.PAGE_END);

		this.setContentPane(mainPanel);
		this.pack();
		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		serv = new Server();
		serv.start();
	}

	public static void addUser(String name) {
		usersList.addUser(name, new ImageIcon(Command.class.getResource("/res/icon.png")));
	}

	public static void removeUser(String name) {
		usersList.removeUser(name);
	}

	public static void replaceUser(String oldName, String newName) {
		usersList.replaceUser(oldName, newName);
	}

	public static void showError(Exception e, String error) {
		JOptionPane.showMessageDialog(null, error, "AhChat - Serveur - Erreur", JOptionPane.ERROR_MESSAGE);
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

	public static boolean isOpen() {
		return isOpen;
	}

	@Override
	public void focusGained(FocusEvent event) {
		if(inputField.getText().equals(DEFAULT_TEXT)) {
			inputField.setText(null);
		}
	}

	@Override
	public void focusLost(FocusEvent event) {
		if(inputField.getText().isEmpty()) {
			inputField.setText(DEFAULT_TEXT);
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		String text = inputField.getText();
		if(event.getKeyCode() == KeyEvent.VK_ENTER && text.length() > 0) {
			Server.sendAll("<b><font color='black'>[SERVER] : </b>" + text, Server.Type.MESSAGE);
			inputField.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	public static void update(String ip, String chatPort, String dataPort) {
		serverInfos.setListData(new String[] {"IP : " + ip, "Chat port : " + chatPort, "Data port : " + dataPort});
	}
}