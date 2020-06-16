package com.shadorc.ahchat.server;

import com.shadorc.ahchat.client.Main;
import com.shadorc.ahchat.client.frame.UserList;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;

public class ServerFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_TEXT = "Envoyer un message";

    private final JFormattedTextField inputField;
    private final HTMLEditorKit kit;
    private final HTMLDocument doc;

    private final JList<String> serverInfos;
    private final UserList usersList;

    public ServerFrame() {
        super("AhChat - Serveur");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.kit = new HTMLEditorKit();
        this.doc = new HTMLDocument();

        final JPanel mainPanel = new JPanel(new BorderLayout());

        final JTextPane textPane = new JTextPane();
        textPane.setEditorKit(this.kit);
        textPane.setDocument(this.doc);
        textPane.setEditable(false);
        ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        final JScrollPane scroll = new JScrollPane(textPane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll, BorderLayout.CENTER);

        final JPanel rightPanel = new JPanel(new GridLayout(2, 0));
        rightPanel.setPreferredSize(new Dimension(Main.getFrame().getWidth() / 4, 0));

        this.usersList = new UserList();
        this.usersList.setBorder(BorderFactory.createLoweredBevelBorder());
        rightPanel.add(this.usersList);

        this.serverInfos = new JList<>();
        this.serverInfos.setBorder(BorderFactory.createLoweredBevelBorder());
        this.serverInfos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    final JPopupMenu menu = new JPopupMenu();
                    final JMenuItem item = new JMenuItem("Copy");
                    item.addActionListener(e1 -> Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(new StringSelection(
                                    ServerFrame.this.serverInfos.getSelectedValue().split(" : ", 2)[1]), null));
                    menu.add(item);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        rightPanel.add(this.serverInfos);

        mainPanel.add(rightPanel, BorderLayout.LINE_END);

        this.inputField = new JFormattedTextField(this.DEFAULT_TEXT);
        this.inputField.setPreferredSize(new Dimension(0, 25));

        this.inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(final KeyEvent event) {
                final String text = ServerFrame.this.inputField.getText();
                if (event.getKeyCode() == KeyEvent.VK_ENTER && !text.trim().isEmpty()) {
                    Server.sendAll("<b><font color='black'>[SERVER] : </b>" + text, Server.MessageType.NORMAL);
                    ServerFrame.this.inputField.setText(null);
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {}

            @Override
            public void keyTyped(final KeyEvent e) {}
        });

        this.inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent event) {
                if (ServerFrame.this.inputField.getText().equals(ServerFrame.this.DEFAULT_TEXT)) {
                    ServerFrame.this.inputField.setText(null);
                }
            }

            @Override
            public void focusLost(final FocusEvent event) {
                if (ServerFrame.this.inputField.getText().isEmpty()) {
                    ServerFrame.this.inputField.setText(ServerFrame.this.DEFAULT_TEXT);
                }
            }
        });
        mainPanel.add(this.inputField, BorderLayout.PAGE_END);

        this.setContentPane(mainPanel);
        this.pack();
        this.setMinimumSize(new Dimension(800, 600));
        this.setPreferredSize(new Dimension(800, 600));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void addUser(final String name) {
        this.usersList.addUser(name, new ImageIcon(this.getClass().getResource("/icon.png")));
    }

    public void replaceUser(final String oldName, final String newName) {
        this.usersList.replaceUser(oldName, newName);
    }

    public void removeUser(final String name) {
        this.usersList.removeUser(name);
    }

    public void dispMessage(final String message) {
        this.disp("<font size=4>" + message + "</font>");
    }

    public void dispError(final Exception e, final String error) {
        this.disp("<b><i><font color=red size=4> /!\\ " + error + " /!\\\n</b></i></font>");
        e.printStackTrace();
    }

    private void disp(final String message) {
        try {
            this.kit.insertHTML(this.doc, this.doc.getLength(), message, 0, 0, null);
        } catch (final BadLocationException | IOException e) {
            Main.showErrorDialog(e, "Erreur lors de l'affichage du message : " + e.getMessage());
        }
    }

    public void updateInfos(final String ip, final int chatPort, final int dataPort) {
        this.serverInfos.setListData(new String[]{"IP : " + ip, "Chat port : " + chatPort, "Data port : " + dataPort});
    }
}