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

	public static ImageIcon create(File file, int dimension) {
		ImageIcon icon = new ImageIcon(file.getPath());

		//Resize image with ratio aspect
		int width = (icon.getIconWidth() >= icon.getIconHeight()) ? -1 : dimension;;
		int height = (icon.getIconWidth() >= icon.getIconHeight()) ? dimension : -1;

		icon = new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));

		//Get image's center
		int size = Math.min(icon.getIconWidth(), icon.getIconHeight());

		BufferedImage image = toBufferedImage(icon.getImage());
		image = image.getSubimage(icon.getIconWidth()/2-size/2, icon.getIconHeight()/2-size/2, size, size);

		icon = new ImageIcon(image);

		/* Make Rounded Icon*/
		int cornerRadius = icon.getIconHeight();
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

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
}
