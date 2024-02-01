package me.donut.ramolos.window;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import me.donut.ramolos.Utils;


public class ChatTab extends JPanel {
	
	private final int LOG_SIZE = 100;
	private JEditorPane editorPane;
	private JTable infoTable;
	private ArrayList<String> log = new ArrayList<>();

	public ChatTab() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setFocusable(false);
		editorPane.setContentType("text/html");
		editorPane.setPreferredSize(new Dimension(350, 200));

		JScrollPane listScroller = new JScrollPane(editorPane);
		HTMLEditorKit kit = new HTMLEditorKit();
		StyleSheet styleSheet = kit.getStyleSheet();
		Document doc = kit.createDefaultDocument();
		DefaultCaret caret = (DefaultCaret) editorPane.getCaret();

		Font font = Window.getCustomFont();
		styleSheet.addRule("p {font-family: \"" 
			+ font.getName()
			+ "\"; font-size: 13px; margin: 0, padding: 0}");

		styleSheet.addRule("b.hl_name {color: #568AF2}");
		styleSheet.addRule("b.hl_nemesis {color: #4E576A}");
		styleSheet.addRule("b.hl_item {color: #692746}");
		styleSheet.addRule("b.hl_join {color: #89ca78}");
		styleSheet.addRule("b.hl_participate {color: #d9a343}");

		editorPane.setEditorKit(kit);
		editorPane.setDocument(doc);
		
		listScroller.setLocation(25, 50);
		listScroller.setAutoscrolls(true);
		listScroller.setPreferredSize(editorPane.getPreferredSize());
 		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		String[][] data = {
			{ "Status", "aktiv" },
			{ "Zeilen gelesen", "0" },
			{ "Auswertung lokal", "0" },
			{ "Synchronisiert", "0"}
	};

		infoTable = new JTable(data, new String[] {"", ""});
		infoTable.setFont(font);
		infoTable.setFocusable(false);
		infoTable.setRowSelectionAllowed(false);
		infoTable.setPreferredSize(new Dimension(150, 100));
		infoTable.setRowHeight(23);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		infoTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);

		setActive(false);

		add(listScroller);
		add(infoTable);
	}

	public void appendLine(String text, boolean sent) {
		log.add("<p>" + (sent ? "🔁 " : "") + text + "</p>");
		while(log.size() > LOG_SIZE) log.remove(0);
		if (sent) updateLinesTransmitted(1);
		editorPane.setText("<html><body>" + String.join("", log) + "</body></html>");
	}

	public void setActive(boolean active) {
		String display = String.format("<html><font color='%s'>%s</font></html>",
			active ? Utils.COLOR_GREEN : Utils.COLOR_RED,
			active ? "Aktiv" : "Inaktiv"
		);
		infoTable.setValueAt(display, 0, 1);
	}

	public void updateLinesRead(int readLines) {
		infoTable.setValueAt("" + readLines, 1, 1);
	}

	public void updateLinesEval(int evalLocal) {
		infoTable.setValueAt("" + evalLocal, 2, 1);
	}

	public void updateLinesTransmitted(int transmitted) {
		infoTable.setValueAt("" + transmitted, 3, 1);
	}
}
