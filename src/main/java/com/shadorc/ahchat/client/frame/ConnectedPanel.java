package com.shadorc.ahchat.client.frame;

import com.shadorc.ahchat.client.Client;
import com.shadorc.ahchat.client.Main;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConnectedPanel extends JPanel implements ActionListener {

    private static final String DEFAULT_TEXT = "Envoyer un message";

    private static JScrollPane jsp;
    private static Map<String, JProgressBar> progressBars;

    private final JButton fileButton;
    private final JButton messageButton;
    private final JFormattedTextField inputField;

    private final Image background;

    private static final HTMLEditorKit kit = new HTMLEditorKit();
    private static final HTMLDocument doc = new HTMLDocument();

    private static final UserList users = new UserList();

    public ConnectedPanel() {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.background = new ImageIcon(this.getClass().getResource("/background.png")).getImage();

        final JTextPane chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setEditorKit(ConnectedPanel.kit);
        chatPane.setDocument(ConnectedPanel.doc);
        chatPane.setBorder(BorderFactory.createEmptyBorder());
        chatPane.setContentType("text/html");
        ((DefaultCaret) chatPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        final JScrollPane scroll = new JScrollPane(chatPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setOpaque(false);
        this.add(scroll, BorderLayout.CENTER);

        final JPanel rightPanel = new JPanel(new GridLayout(2, 0));

        ConnectedPanel.users.setBorder(BorderFactory.createLoweredBevelBorder());
        ConnectedPanel.users.setPreferredSize(new Dimension(Main.getFrame().getWidth() / 4, 0));
        rightPanel.add(ConnectedPanel.users);

        ConnectedPanel.progressBars = new HashMap<>();

        final JPanel progressPanel = new JPanel(new GridLayout(10, 1));
        progressPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        progressPanel.setBackground(Color.WHITE);

        ConnectedPanel.jsp = new JScrollPane(progressPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(ConnectedPanel.jsp);

        this.add(rightPanel, BorderLayout.LINE_END);

        final JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 50, 5, 50));

        this.inputField = new JFormattedTextField(this.DEFAULT_TEXT);
        this.inputField.setPreferredSize(new Dimension(Main.getFrame().getWidth(), 25));
        this.inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ConnectedPanel.this.sendMessage();
                }
            }

            @Override
            public void keyTyped(final KeyEvent e) {}

            @Override
            public void keyReleased(final KeyEvent e) {}
        });

        this.inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent event) {
                if (ConnectedPanel.this.inputField.getText().equals(ConnectedPanel.this.DEFAULT_TEXT)) {
                    ConnectedPanel.this.inputField.setText(null);
                }
            }

            @Override
            public void focusLost(final FocusEvent event) {
                if (ConnectedPanel.this.inputField.getText().isEmpty()) {
                    ConnectedPanel.this.inputField.setText(ConnectedPanel.this.DEFAULT_TEXT);
                }
            }
        });
        bottom.add(this.inputField, BorderLayout.CENTER);

        final JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.setOpaque(false);

        this.messageButton = new Button("send", "Envoyer un message", Button.Size.SMALL, this);
        this.messageButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttonsPanel.add(this.messageButton);

        this.fileButton = new Button("send", "Envoyer un fichier", Button.Size.SMALL, this);
        this.fileButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttonsPanel.add(this.fileButton);

        bottom.add(buttonsPanel, BorderLayout.LINE_END);

        this.add(bottom, BorderLayout.PAGE_END);
    }

    public static UserList getUsersList() {
        return ConnectedPanel.users;
    }

    public static void dispMessage(final String message) {
        ConnectedPanel.disp("<font size=4>" + message + "</font>");
    }

    public static void dispError(final Exception e, final String error) {
        ConnectedPanel.disp("<b><i><font color='red' size=4> /!\\ " + error + " /!\\\n</b></i></font>");
        e.printStackTrace();
    }

    private static void disp(final String text) {
        try {
            ConnectedPanel.kit.insertHTML(ConnectedPanel.doc, ConnectedPanel.doc.getLength(), text, 0, 0, null);
            Tray.alert();
        } catch (final BadLocationException | IOException e) {
            Main.showErrorDialog(e, "Une erreur est survenue lors de l'affichage du message : " + e.getMessage());
        }
    }

    public static void addProgressBar(final String state, final String name) {
        final JProgressBar bar = new JProgressBar(0, 100);
        bar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bar.setToolTipText("Ouvrir");
        bar.setName(state + " : " + name);
        bar.setStringPainted(true);
        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    try {
                        Desktop.getDesktop().open(new File(FileSystemView.getFileSystemView().getHomeDirectory() + "/" + name));
                    } catch (final IOException e) {
                        Main.showErrorDialog(e, "Erreur lors de l'ouverture du fichier, " + e.getMessage());
                    }
                }
            }
        });

        ConnectedPanel.progressBars.put(name, bar);
        ((JPanel) ConnectedPanel.jsp.getViewport().getView()).add(bar);
        ConnectedPanel.jsp.revalidate();
        ConnectedPanel.jsp.repaint();
    }

    public static void updateBar(final String state, final String name, final int value) {
        ConnectedPanel.progressBars.get(name).setValue(value);
        ConnectedPanel.progressBars.get(name).setString(state + " : " + name + " (" + value + "%)");
    }

    private void sendMessage() {
        final String message = this.inputField.getText().trim();
        if (!message.isEmpty()) {
            Client.getInstance().getEmitter().sendMessage(message);
        }
        this.inputField.setText("");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final JButton bu = (JButton) event.getSource();
        if (bu == this.messageButton) {
            this.sendMessage();

        }
        /*else {
            final JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            final int choice = chooser.showOpenDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                Client.sendFile(chooser.getSelectedFile());
            }
        }*/
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.background, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.background.getWidth(null),
                this.background.getHeight(null), this);
    }
}
