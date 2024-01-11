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


public class ChatTab extends JPanel {
	
	private final int LOG_SIZE = 100;
	private JEditorPane editorPane;
	private JTable infoTable;
	private ArrayList<String> log = new ArrayList<>();

	private boolean active = false;
	private int readLines = 0;
	private int evalLocal = 0;
	private int transmitted = 0;

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

		editorPane.setEditorKit(kit);
		editorPane.setDocument(doc);
		
		listScroller.setLocation(25, 50);
		listScroller.setAutoscrolls(true);
		listScroller.setPreferredSize(editorPane.getPreferredSize());
 		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		 String[][] data = {
            { "Status", "aktiv" },
            { "Zeilen gelesen", "25" },
			{ "Auswertung lokal", "1" },
			{ "Synchronisiert", "3"}
        };

		infoTable = new JTable(data, new String[] {"", ""});
		infoTable.setFont(font);
		infoTable.setFocusable(false);
		infoTable.setPreferredSize(new Dimension(150, 100));
		infoTable.setRowHeight(23);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		infoTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);

		setActive(isActive());
		updateLinesRead(getLinesRead());
		updateLinesEval(getLinesEval());
		updateLinesTransmitted(getLinesTransmitted());

		add(listScroller);
		add(infoTable);
		// add(Box.createRigidArea(new Dimension(400, 200)));
	}

	public void appendLine(String text) {
		log.add("<p>🔁 " + text + "</p>");
		while(log.size() > LOG_SIZE) log.remove(0);
		editorPane.setText("<html><body>" + String.join("", log) + "</body></html>");
	}

	public void setActive(boolean active) {
		this.active = active;
		String display = active ? "<html><font color='#239E62'>Aktiv</font></html>" : "<html><font color='#bd3c5f'>Inaktiv</font></html>";
		infoTable.setValueAt(display, 0, 1);
	}

	public boolean isActive() {
		return active;
	}

	public int updateLinesRead(int add) {
		readLines += add;
		if (add < 0) readLines = 0;
		infoTable.setValueAt("" + readLines, 1, 1);
		return readLines;
	}

	public int getLinesRead() {
		return readLines;
	}

	public int updateLinesEval(int add) {
		evalLocal += add;
		if (add < 0) evalLocal = 0;
		infoTable.setValueAt("" + evalLocal, 2, 1);
		return evalLocal;
	}

	public int getLinesEval() {
		return evalLocal;
	}

	public int updateLinesTransmitted(int add) {
		transmitted += add;
		if (add < 0) transmitted = 0;
		infoTable.setValueAt("" + transmitted, 3, 1);
		return transmitted;
	}

	public int getLinesTransmitted() {
		return transmitted;
	}

}
