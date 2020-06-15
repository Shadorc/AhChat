package com.shadorc.ahchat.client.frame;

import com.shadorc.ahchat.client.Client;
import com.shadorc.ahchat.server.ServerMain;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Frame extends JFrame {

    private static final long serialVersionUID = 1L;

    public Frame() {
        super("AhChat");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //Don't exit if Server is launched
                Client.exit(!ServerMain.isOpen());
            }
        });

        this.setContentPane(new ConnectionPanel());
        this.pack();

        this.setIconImage(new ImageIcon(this.getClass().getResource("/icon.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        this.setMinimumSize(new Dimension(800, 600));
        this.setPreferredSize(new Dimension(800, 600));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void setPanel(JPanel panel) {
        this.setContentPane(panel);
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }
}