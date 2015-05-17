package me.shadorc.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class ServerFrame extends JFrame implements KeyListener, FocusListener {

	private static final long serialVersionUID = 1L;

	private String DEFAULT_TEXT = "Envoyer un message";

	private static HTMLEditorKit kit;
	private static HTMLDocument doc;
	private JFormattedTextField textField;

	public ServerFrame() {
		super("Serveur");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

		textField.setPreferredSize(new Dimension(this.getWidth(), 25));
		textField.addKeyListener(this);
		textField.addFocusListener(this);
		pane.add(textField, BorderLayout.PAGE_END);

		this.setContentPane(pane);
		this.pack();
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		new Server().start();
	}

	public static void split() {
		ServerFrame.dispMessage("--------");
	}

	public static void dispMessage(String message) {
		disp("<font size=4>" + message + "</font>");
	}

	public static void dispError(String error) {
		disp("<b><i><font color=red size=4> /!\\ " + error + " /!\\\n</b></i></font>");
	}

	private static void disp(String message) {
		try {
			kit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
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
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			Command.admin(textField.getText());
			textField.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}