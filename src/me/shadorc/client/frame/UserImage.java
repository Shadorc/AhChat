package me.shadorc.client.frame;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

public class UserImage {

	public static ImageIcon create(File file, int width, int height) {
		ImageIcon icon =  new ImageIcon(file.getPath());

		if(width != -1 || height != -1) {
			icon = new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
		}

		/* Make Rounded Icon*/
		int cornerRadius = Math.max(icon.getIconWidth(), icon.getIconHeight());
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();

		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(icon.getImage(), 0, 0, null);
		g2.dispose();

		return new ImageIcon(output);
	}
}
