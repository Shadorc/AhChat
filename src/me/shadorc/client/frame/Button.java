package me.shadorc.client.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton {

    private static final long serialVersionUID = 1L;

    private String name;
    private Size size;

    public enum Size {
        SMALL, NORMAL;
    }

    public Button(String name, String info, Size size, ActionListener al) {
        super();

        this.name = name;
        this.size = size;

        final ImageIcon icon1 = this.getIcon("1");
        final ImageIcon icon2 = this.getIcon("2");
        final ImageIcon icon3 = this.getIcon("3");

        this.setIcon(icon1);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton bu = (JButton) e.getSource();
                bu.setIcon(icon3);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton bu = (JButton) e.getSource();
                bu.setIcon(icon1);
            }
        });
        this.setPressedIcon(icon2);
        this.setToolTipText(info);
        this.setBackground(Color.WHITE);
        this.addActionListener(al);
        this.setFocusable(false);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
    }

    private ImageIcon getIcon(String number) {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/res/Button_" + name + "_" + number + ".png"));
        int dimension = (size == Size.NORMAL) ? 50 : 30;
        return new ImageIcon(icon.getImage().getScaledInstance(dimension, dimension, Image.SCALE_SMOOTH));
    }
}