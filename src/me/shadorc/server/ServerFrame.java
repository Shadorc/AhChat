package me.shadorc.server;

import me.shadorc.client.Command;
import me.shadorc.client.Main;
import me.shadorc.client.frame.UserList;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;

public class ServerFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private String DEFAULT_TEXT = "Envoyer un message";

    private JFormattedTextField inputField;
    private HTMLEditorKit kit;
    private HTMLDocument doc;

    private JList<String> serverInfos;
    private UserList usersList;

    public ServerFrame() {
        super("AhChat - Serveur");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        kit = new HTMLEditorKit();
        doc = new HTMLDocument();

        JPanel mainPanel = new JPanel(new BorderLayout());

        JTextPane textPane = new JTextPane();
        textPane.setEditorKit(kit);
        textPane.setDocument(doc);
        textPane.setEditable(false);
        ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(textPane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridLayout(2, 0));
        rightPanel.setPreferredSize(new Dimension((int) (Main.getFrame().getWidth() / 4), 0));

        usersList = new UserList();
        usersList.setBorder(BorderFactory.createLoweredBevelBorder());
        rightPanel.add(usersList);

        serverInfos = new JList<String>();
        serverInfos.setBorder(BorderFactory.createLoweredBevelBorder());
        serverInfos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item = new JMenuItem("Copy");
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(serverInfos.getSelectedValue().split(" : ", 2)[1]), null);
                        }
                    });
                    menu.add(item);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        rightPanel.add(serverInfos);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        inputField = new JFormattedTextField(DEFAULT_TEXT);
        inputField.setPreferredSize(new Dimension(0, 25));

        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent event) {
                String text = inputField.getText();
                if (event.getKeyCode() == KeyEvent.VK_ENTER && text.trim().length() > 0) {
                    Server.sendAll("<b><font color='black'>[SERVER] : </b>" + text, Server.MessageType.NORMAL);
                    inputField.setText(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent event) {
                if (inputField.getText().equals(DEFAULT_TEXT)) {
                    inputField.setText(null);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText(DEFAULT_TEXT);
                }
            }
        });
        mainPanel.add(inputField, BorderLayout.PAGE_END);

        this.setContentPane(mainPanel);
        this.pack();
        this.setMinimumSize(new Dimension(800, 600));
        this.setPreferredSize(new Dimension(800, 600));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void addUser(String name) {
        usersList.addUser(name, new ImageIcon(Command.class.getResource("/res/icon.png")));
    }

    public void replaceUser(String oldName, String newName) {
        usersList.replaceUser(oldName, newName);
    }

    public void removeUser(String name) {
        usersList.removeUser(name);
    }

    public void dispMessage(String message) {
        this.disp("<font size=4>" + message + "</font>");
    }

    public void dispError(Exception e, String error) {
        this.disp("<b><i><font color=red size=4> /!\\ " + error + " /!\\\n</b></i></font>");
        e.printStackTrace();
    }

    private void disp(String message) {
        try {
            kit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
        } catch (BadLocationException | IOException e) {
            Main.showErrorDialog(e, "Erreur lors de l'affichage du message : " + e.getMessage());
        }
    }

    public void updateInfos(String ip, int chatPort, int dataPort) {
        serverInfos.setListData(new String[]{"IP : " + ip, "Chat port : " + chatPort, "Data port : " + dataPort});
    }
}