package me.donut.ramolos.window;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;


public class ChatTab extends JPanel {
	
	private JEditorPane editorPane;
	private ArrayList<String> log = new ArrayList<>();

	public ChatTab() {
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
		editorPane.setEditorKit(kit);
		editorPane.setDocument(doc);
		
		listScroller.setLocation(25, 50);
		listScroller.setAutoscrolls(true);
		listScroller.setPreferredSize(editorPane.getPreferredSize());
 		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		add(listScroller);
	}

	public void appendLine(String text) {
		log.add("<p>" + text + "</p>");
		editorPane.setText("<html><body>" + String.join("", log) + "</body></html>");
	}

}
