package com.shadorc.ahchat.client.frame;

import com.shadorc.ahchat.client.Client;
import com.shadorc.ahchat.client.Main;
import com.shadorc.ahchat.server.ServerManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class ConnectionPanel extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

    private final JFormattedTextField nameField;
    private final JFormattedTextField ipField;
    private final JButton connect;
    private final JButton create;
    private final JButton iconButton;
    private final Image background;
    private File icon;

    public ConnectionPanel() {
        super(new GridBagLayout());
        this.setOpaque(false);

        this.background = new ImageIcon(this.getClass().getResource("/background.png")).getImage();

        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(500, 325));
        mainPanel.setOpaque(false);

        this.icon = new File((Storage.getData(Storage.Data.ICON) != null) ? Storage.getData(Storage.Data.ICON) :
                this.getClass().getResource("/icon.png").getFile());

        /*Icon Panel*/
        this.iconButton = new JButton(UserImage.create(this.icon, 125));
        this.iconButton.setBorder(BorderFactory.createEmptyBorder());
        this.iconButton.setHorizontalTextPosition(JButton.CENTER);
        this.iconButton.setContentAreaFilled(false);
        this.iconButton.setForeground(Color.RED);
        this.iconButton.setFocusable(false);
        this.iconButton.addActionListener(this);
        this.iconButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(final MouseEvent e) {
                ConnectionPanel.this.iconButton.setText("");
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                ConnectionPanel.this.iconButton.setText("Changer");
            }
        });

        new DropTarget(this.iconButton, new DropTargetListener() {
            @Override
            public void drop(final DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    final List<?> files = (List<?>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    final File file = (File) (files.get(0));

                    //Get the type of the file (e.g : image/jpeg")
                    final String type = Files.probeContentType(file.toPath()).split("/")[0];

                    if (!"image".equals(type)) {
                        ConnectionPanel.this.iconButton.setIcon(UserImage.create(ConnectionPanel.this.icon, 125));
                        return;
                    }

                    ConnectionPanel.this.icon = file;
                    ConnectionPanel.this.iconButton.setIcon(UserImage.create(ConnectionPanel.this.icon, 125));

                    Storage.store(Storage.Data.ICON, ConnectionPanel.this.icon.getPath());

                } catch (final Exception e) {
                    ConnectionPanel.this.iconButton.setIcon(UserImage.create(ConnectionPanel.this.icon, 125));
                    e.printStackTrace();
                }
            }

            @Override
            public void dragEnter(final DropTargetDragEvent e) {
                ConnectionPanel.this.iconButton.setIcon(UserImage.create(new File(this.getClass().getResource("/drop_icon.png").getFile()), 125));
            }

            @Override
            public void dragExit(final DropTargetEvent e) {
                ConnectionPanel.this.iconButton.setIcon(UserImage.create(ConnectionPanel.this.icon, 125));
            }

            @Override
            public void dragOver(final DropTargetDragEvent e) { }

            @Override
            public void dropActionChanged(final DropTargetDragEvent e) { }
        });

        mainPanel.add(this.iconButton, BorderLayout.PAGE_START);

        final JPanel loginPanel = new JPanel(new GridLayout(2, 2, 20, 25));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        loginPanel.setOpaque(false);

        /*Pseudo Panel*/
        final JLabel name = new JLabel("Pseudo :", JLabel.RIGHT);
        name.setForeground(Color.BLACK);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        loginPanel.add(name);

        this.nameField = new JFormattedTextField(Storage.getData(Storage.Data.PSEUDO));
        this.nameField.addKeyListener(this);
        loginPanel.add(this.nameField);

        /*IP Panel*/
        final JLabel ip = new JLabel("IP du Serveur :", JLabel.RIGHT);
        ip.setForeground(Color.BLACK);
        ip.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        loginPanel.add(ip);

        this.ipField = new JFormattedTextField(Storage.getData(Storage.Data.IP));
        this.ipField.addKeyListener(this);
        loginPanel.add(this.ipField);

        mainPanel.add(loginPanel, BorderLayout.CENTER);

        final JPanel buttons = new JPanel(new GridLayout(0, 2, 20, 0));
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttons.setOpaque(false);

        /*Create Chat Room Button Panel*/
        final JPanel createPanel = new JPanel(new BorderLayout());
        createPanel.setOpaque(false);
        this.create = new Button("create", "Créer un salon", Button.Size.NORMAL, this);
        createPanel.add(this.create, BorderLayout.LINE_END);
        buttons.add(createPanel);

        /*Connection Button Panel*/
        final JPanel connectPanel = new JPanel(new BorderLayout());
        connectPanel.setOpaque(false);
        this.connect = new Button("validate", "Connexion", Button.Size.NORMAL, this);
        connectPanel.add(this.connect, BorderLayout.LINE_START);
        buttons.add(connectPanel);

        mainPanel.add(buttons, BorderLayout.PAGE_END);

        this.add(mainPanel);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final JButton bu = (JButton) event.getSource();
        if (bu == this.iconButton) {
            final JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                this.icon = chooser.getSelectedFile();
                this.iconButton.setIcon(UserImage.create(this.icon, 125));
                Storage.store(Storage.Data.ICON, this.icon.getPath());
            }

        } else if (bu == this.connect) {
            this.connection();

        } else if (bu == this.create) {
            if (!ServerManager.getInstance().isStarted()) {
                ServerManager.getInstance().start();
            } else {
                ServerManager.getInstance().getFrame().setVisible(true);
                ServerManager.getInstance().getFrame().toFront();
            }
        }
    }

    private void connection() {

        final String pseudo = this.nameField.getText().trim();
        final String ip = this.ipField.getText().trim();

        //Test if name contains others caracter than letters or number
        if (pseudo.isEmpty() || ip.isEmpty() || !pseudo.replaceAll("[^0-9a-zA-Z]", "").equals(pseudo)) {
            Main.showErrorDialog(new Exception("Champs incorrects"), "Merci de remplir tous les champs correctement. (Les pseudos ne " +
                    "peuvent contenir que des lettres et des chiffres)");

        } else {
            this.connect.setText("Connexion...");
            this.connect.setEnabled(false);

            new Thread(() -> {
                final ConnectedPanel pane = new ConnectedPanel(); //Sinon users est null et il y a une erreur lors du launch
                if (Client.connect(pseudo, ConnectionPanel.this.icon, ip)) {
                    Main.getFrame().setPanel(pane);
                    Main.getFrame().setTitle(Main.getFrame().getTitle() + " - " + pseudo);
                } else {
                    Main.showErrorDialog(new Exception("Serveur indisponible"), "Serveur indisponible ou inexistant.");
                    ConnectionPanel.this.connect.setText("Connexion");
                    ConnectionPanel.this.connect.setEnabled(true);
                }
            }).start();
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.background, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.background.getWidth(null),
                this.background.getHeight(null), this);
    }

    @Override
    public void keyPressed(final KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            this.connection();
        }
    }

    @Override
    public void keyReleased(final KeyEvent arg0) { }

    @Override
    public void keyTyped(final KeyEvent arg0) { }
}
