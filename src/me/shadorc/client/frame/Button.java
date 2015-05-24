package me.shadorc.client.frame;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Button extends JButton {

	private static final long serialVersionUID = 1L;

	private String name;

	public Button(String name, String info, ActionListener al) {
		super();

		this.name = name;

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
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/res/Bouton " + name + number + ".png"));
		return new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
	}
}