package com.shadorc.ahchat.client.frame;


import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Component;
import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;

public class UserList extends JList<String> {

    private static final long serialVersionUID = 1L;
    private final HashMap<String, ImageIcon> imageMap;

    public UserList() {
        super();
        this.setCellRenderer(new ListRenderer());

        this.imageMap = new HashMap<>();
    }

    public class ListRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected,
                                                      final boolean cellHasFocus) {
            final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(UserList.this.imageMap.get(value));
            label.setHorizontalTextPosition(JLabel.RIGHT);
            return label;
        }
    }

    public String[] getUsersArray() {
        final String[] users = this.imageMap.keySet().toArray(new String[this.imageMap.size()]);
        Arrays.sort(users, String.CASE_INSENSITIVE_ORDER);
        return users;
    }

    public void addUser(final String name, final ImageIcon icon) {
        this.imageMap.put(name, new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        this.setListData(this.getUsersArray());
    }

    public void removeUser(final String name) {
        this.imageMap.remove(name);
        this.setListData(this.getUsersArray());
    }

    public void replaceUser(final String oldName, final String newName) {
        this.imageMap.put(newName, this.imageMap.get(oldName));
        this.imageMap.remove(oldName);
        this.setListData(this.getUsersArray());
    }
}
