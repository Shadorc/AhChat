package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import me.shadorc.client.Client;

public class ConnectedPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static JPanel progressPanel;
	private static ArrayList <JProgressBar> progressBars;

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

		JPanel right = new JPanel(new GridLayout(2, 0));

		users.setEditable(false);
		users.setBorder(BorderFactory.createLoweredBevelBorder());
		users.setPreferredSize(new Dimension((int) (Frame.getDimension().getWidth()/4), 0));
		right.add(users);

		progressBars = new ArrayList <JProgressBar> ();
		progressPanel = new JPanel(new GridLayout());
		right.add(progressPanel);

		this.add(right, BorderLayout.EAST);

		JPanel bottom = new JPanel(new BorderLayout());

		JFormattedTextField saisisTexte = new JFormattedTextField();
		saisisTexte.setPreferredSize(new Dimension((int) Frame.getDimension().getWidth(), 25));
		saisisTexte.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = saisisTexte.getText();
					if(message.length() > 0){
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
		bottom.add(saisisTexte, BorderLayout.CENTER);

		JButton file = new JButton("Envoyer un fichier");
		file.addActionListener(this);
		file.setFocusable(false);
		file.setBackground(Color.WHITE);
		bottom.add(file, BorderLayout.EAST);

		this.add(bottom, BorderLayout.PAGE_END);
	}

	public static void addUser(String user) {
		users.append(user + "\n");
	}

	public static void removeUser(String user) {
		users.setText(users.getText().replace(user, ""));
	}

	public static void replaceUser(String oldName, String newName) {
		users.setText(users.getText().replace(oldName, newName));
	}

	public static List <String> getUsers() {
		return Arrays.asList(users.getText().split("\n"));
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
			Frame.showError(e, "Une erreur est survenue lors de l'affichage du message : " + e.getMessage());
		}
	}

	public static void addProgressBar(String name) {
		JProgressBar bar = new JProgressBar(0, 100);
		bar.setName(name);
		bar.setStringPainted(true);
		progressBars.add(bar);
		progressPanel.add(bar);
		GridLayout progressLayout = ((GridLayout) progressPanel.getLayout());
		progressLayout.setRows(progressLayout.getRows()+1);
		progressPanel.revalidate();
		progressPanel.repaint();
	}

	public static void removeProgressBar(String name) {
		for(JProgressBar bar : progressBars) {
			if(bar.getName().equals(name)) {
				progressBars.remove(bar);
				progressPanel.remove(bar);
				((GridLayout) progressPanel.getLayout()).setRows(((GridLayout) progressPanel.getLayout()).getRows()-1);
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
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int choice = chooser.showOpenDialog(null);
		if(choice == JFileChooser.APPROVE_OPTION) {
			Client.sendFile(chooser.getSelectedFile());
		}
	}
}
