package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import me.shadorc.client.Client;
import me.shadorc.client.frame.Button.Size;

public class ConnectedPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static JPanel progressPanel;
	private static ArrayList <JProgressBar> progressBars;

	private JButton fileButton, messageButton;
	private JFormattedTextField inputText;

	private Image background;

	private static HTMLEditorKit kit = new HTMLEditorKit();
	private static HTMLDocument doc = new HTMLDocument();

	private static UserList users = new UserList();

	public ConnectedPanel() {

		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.background = new ImageIcon(this.getClass().getResource("/res/background.png")).getImage();

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

		JPanel right = new JPanel(new GridLayout(2, 0));

		users.setBorder(BorderFactory.createLoweredBevelBorder());
		users.setPreferredSize(new Dimension((int) (Frame.getDimension().getWidth()/4), 0));
		right.add(users);

		progressBars = new ArrayList <JProgressBar> ();
		progressPanel = new JPanel(new GridLayout(10, 1));
		progressPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		progressPanel.setBackground(Color.WHITE);
		right.add(progressPanel);

		this.add(right, BorderLayout.EAST);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setOpaque(false);
		bottom.setBorder(BorderFactory.createEmptyBorder(10, 50, 5, 50));

		inputText = new JFormattedTextField();
		inputText.setPreferredSize(new Dimension((int) Frame.getDimension().getWidth(), 25));
		inputText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
		bottom.add(inputText, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
		buttonsPanel.setOpaque(false);

		messageButton = new Button("Envoyer", "Envoyer un message", Size.SMALL, this);
		messageButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		buttonsPanel.add(messageButton);

		fileButton = new Button("Envoyer", "Envoyer un fichier", Size.SMALL, this);
		fileButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		buttonsPanel.add(fileButton);

		bottom.add(buttonsPanel, BorderLayout.EAST);

		this.add(bottom, BorderLayout.PAGE_END);
	}

	public static UserList getUsersList() {
		return users;
	}

	public static void dispMessage(String message) {
		disp("<font size=4>" + message + "</font>");
	}

	public static void dispError(Exception e, String error) {
		disp("<b><i><font color='red' size=4> /!\\ " + error + " /!\\\n</b></i></font>");
		e.printStackTrace();
	}

	public static void dispError(String error) {
		disp("<b><i><font color='red' size=4> /!\\ " + error + " /!\\\n</b></i></font>");
	}

	private static void disp(String text) {
		try {
			kit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			Frame.popupError(e, "Une erreur est survenue lors de l'affichage du message : " + e.getMessage());
		}
	}

	public static void addProgressBar(String name) {
		JProgressBar bar = new JProgressBar(0, 100);
		bar.setName(name);
		bar.setStringPainted(true);
		progressBars.add(bar);
		progressPanel.add(bar);
		progressPanel.revalidate();
		progressPanel.repaint();
	}

	public static void removeProgressBar(String name) {
		for(JProgressBar bar : progressBars) {
			if(bar.getName().equals(name)) {
				progressBars.remove(bar);
				progressPanel.remove(bar);
				progressPanel.revalidate();
				progressPanel.repaint();
				return;
			}
		}
	}

	public static void updateBar(String name, int value) {
		for(JProgressBar bar : progressBars) {
			if(bar.getName().equals(name)) {
				bar.setValue(value);
				bar.setString(name + " : " + value + "%");
				return;
			}
		}
	}

	private void sendMessage() {
		String message = inputText.getText();
		if(message.length() > 0){
			Client.sendMessage(message);
		}
		inputText.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton bu = (JButton) event.getSource();
		if(bu == messageButton) {
			this.sendMessage();
		} else {
			JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int choice = chooser.showOpenDialog(null);
			if(choice == JFileChooser.APPROVE_OPTION) {
				Client.sendFile(chooser.getSelectedFile());
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), 0, 0, background.getWidth(null), background.getHeight(null), this);
	}
}
