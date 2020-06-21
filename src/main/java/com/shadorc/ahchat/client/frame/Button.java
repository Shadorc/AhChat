package com.shadorc.ahchat.client.frame;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton {

    private final String name;
    private final Size size;

    public enum Size {
        SMALL, NORMAL;
    }

    public Button(final String name, final String info, final Size size, final ActionListener listener) {
        this.name = name;
        this.size = size;

        final ImageIcon icon1 = this.generateIcon("1");
        final ImageIcon icon2 = this.generateIcon("2");
        final ImageIcon icon3 = this.generateIcon("3");

        this.setIcon(icon1);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent event) {
                final JButton button = (JButton) event.getSource();
                button.setIcon(icon3);
            }

            @Override
            public void mouseExited(final MouseEvent event) {
                final JButton button = (JButton) event.getSource();
                button.setIcon(icon1);
            }
        });
        this.setPressedIcon(icon2);
        this.setToolTipText(info);
        this.setBackground(Color.WHITE);
        this.addActionListener(listener);
        this.setFocusable(false);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
    }

    private ImageIcon generateIcon(final String number) {
        final ImageIcon icon = new ImageIcon(this.getClass()
                .getResource(String.format("/Button_%s_%d.png", this.name, number)));
        final int dimension = this.size == Size.NORMAL ? 50 : 30;
        return new ImageIcon(icon.getImage().getScaledInstance(dimension, dimension, Image.SCALE_SMOOTH));
    }
}