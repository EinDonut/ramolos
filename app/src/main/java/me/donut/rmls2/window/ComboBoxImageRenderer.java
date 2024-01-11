package me.donut.rmls2.window;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ComboBoxImageRenderer extends JLabel implements ListCellRenderer<Object> {

	private String[] labels;
	private ImageIcon[] icons;

	public ComboBoxImageRenderer(String[] labels, ImageIcon[] icons) {
		this.labels = labels;
		this.icons = icons;
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		int selectedIndex = ((Integer)value).intValue();

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		ImageIcon icon = icons[selectedIndex];
		String label = labels[selectedIndex];
		
		if (icon != null) setIcon(icon);
		setText(label);
		setFont(list.getFont());

		return this;
	}

	
}