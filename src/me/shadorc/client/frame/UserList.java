package me.shadorc.client.frame;


import java.awt.Component;
import java.awt.Image;
import java.util.HashMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class UserList extends JList <String> {

	private static final long serialVersionUID = 1L;
	private static HashMap <String, ImageIcon> imageMap;

	public UserList() {
		super();
		this.setCellRenderer(new ListRenderer());

		imageMap = new HashMap <String, ImageIcon> ();
	}

	public class ListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList <?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setIcon(imageMap.get((String) value));
			label.setHorizontalTextPosition(JLabel.RIGHT);
			return label;
		}
	}

	public String[] getUsers() {
		return imageMap.keySet().toArray(new String[imageMap.size()]);
	}

	public void addUser(String name, ImageIcon icon) {
		imageMap.put(name, new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));
		this.setListData(imageMap.keySet().toArray(new String[imageMap.size()]));
	}

	public void removeUser(String name) {
		imageMap.remove(name);
		this.setListData(imageMap.keySet().toArray(new String[imageMap.size()]));
	}

	public void replaceUser(String oldName, String newName) {
		imageMap.put(newName, imageMap.get(oldName));
		imageMap.remove(oldName);
		this.setListData(imageMap.keySet().toArray(new String[imageMap.size()]));
	}
}
