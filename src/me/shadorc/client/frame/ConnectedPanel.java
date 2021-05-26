package me.shadorc.client.frame;

import me.shadorc.client.Client;
import me.shadorc.client.Main;
import me.shadorc.client.frame.Button.Size;

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

public class ConnectedPanel extends JPanel implements ActionListener {

    private final String DEFAULT_TEXT = "Envoyer un message";

    private static JScrollPane jsp;
    private static HashMap<String, JProgressBar> progressBars;

    private final JButton messageButton;
    private final JFormattedTextField inputField;

    private final Image background;

    private static final HTMLEditorKit kit = new HTMLEditorKit();
    private static final HTMLDocument doc = new HTMLDocument();

    private static final UserList users = new UserList();

    public ConnectedPanel() {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.background = new ImageIcon(this.getClass().getResource("/res/background.png")).getImage();

        JTextPane chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setEditorKit(kit);
        chatPane.setDocument(doc);
        chatPane.setBorder(BorderFactory.createEmptyBorder());
        chatPane.setContentType("text/html");
        ((DefaultCaret) chatPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(chatPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setOpaque(false);
        this.add(scroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridLayout(2, 0));

        users.setBorder(BorderFactory.createLoweredBevelBorder());
        users.setPreferredSize(new Dimension(Main.getFrame().getWidth() / 4, 0));
        rightPanel.add(users);

        progressBars = new HashMap<>();

        JPanel progressPanel = new JPanel(new GridLayout(10, 1));
        progressPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        progressPanel.setBackground(Color.WHITE);

        jsp = new JScrollPane(progressPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(jsp);

        this.add(rightPanel, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 50, 5, 50));

        inputField = new JFormattedTextField(DEFAULT_TEXT);
        inputField.setPreferredSize(new Dimension(Main.getFrame().getWidth(), 25));
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ConnectedPanel.this.sendMessage();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
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
        bottom.add(inputField, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.setOpaque(false);

        messageButton = new Button("send", "Envoyer un message", Size.SMALL, this);
        messageButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttonsPanel.add(messageButton);

        JButton fileButton = new Button("send", "Envoyer un fichier", Size.SMALL, this);
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

    private static void disp(String text) {
        try {
            kit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
            Tray.alert();
        } catch (BadLocationException | IOException e) {
            Main.showErrorDialog(e, "Une erreur est survenue lors de l'affichage du message : " + e.getMessage());
        }
    }

    public static void addProgressBar(String state, String name) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bar.setToolTipText("Ouvrir");
        bar.setName(state + " : " + name);
        bar.setStringPainted(true);
        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    try {
                        Desktop.getDesktop().open(new File(FileSystemView.getFileSystemView().getHomeDirectory() + "/" + name));
                    } catch (IOException e) {
                        Main.showErrorDialog(e, "Erreur lors de l'ouverture du fichier, " + e.getMessage());
                    }
                }
            }
        });

        progressBars.put(name, bar);
        ((JPanel) jsp.getViewport().getView()).add(bar);
        jsp.revalidate();
        jsp.repaint();
    }

    public static void updateBar(String state, String name, int value) {
        progressBars.get(name).setValue(value);
        progressBars.get(name).setString(state + " : " + name + " (" + value + "%)");
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            Client.sendMessage(message);
        }
        inputField.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JButton bu = (JButton) event.getSource();
        if (bu == messageButton) {
            this.sendMessage();

        } else {
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int choice = chooser.showOpenDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
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
