package me.shadorc.client.frame;


import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserList extends JList<String> {

    private final Map<String, ImageIcon> imageMap;

    public UserList() {
        super();
        this.setCellRenderer(new ListRenderer());

        imageMap = new HashMap<>();
    }

    public class ListRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(imageMap.get((String) value));
            label.setHorizontalTextPosition(JLabel.RIGHT);
            return label;
        }
    }

    public String[] getUsersArray() {
        String[] users = imageMap.keySet().toArray(new String[0]);
        Arrays.sort(users, String.CASE_INSENSITIVE_ORDER);
        return users;
    }

    public void addUser(String name, ImageIcon icon) {
        imageMap.put(name, new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        this.setListData(this.getUsersArray());
    }

    public void removeUser(String name) {
        imageMap.remove(name);
        this.setListData(this.getUsersArray());
    }

    public void replaceUser(String oldName, String newName) {
        imageMap.put(newName, imageMap.get(oldName));
        imageMap.remove(oldName);
        this.setListData(this.getUsersArray());
    }
}
