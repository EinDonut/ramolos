package me.donut.ramolos.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.List;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;

import me.donut.ramolos.Ramolos;

public class Window extends JFrame {

	private static final Dimension WINDOW_SIZE = new Dimension(400, 530);
	private static final String TITLE = "Ramolos";
	private static final String THEME_PATH = "/themes/one_dark.theme.json";

	private static Font font;

	private ChatTab chatTab;
	private SettingsTab settingsTab;
	
	public Window() {
		FlatLightLaf.setup();
		IntelliJTheme.setup(Window.class.getResourceAsStream(THEME_PATH));
		setupFont();

		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(WINDOW_SIZE);
		setUndecorated(false);
		setLocationRelativeTo(null);
		setResizable(false);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				toFront();
				requestFocus();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Ramolos.getInstance().getLogWatcher().terminate();
				e.getWindow().dispose();
			}
		});
		
		JTabbedPane tabs = new JTabbedPane();
		addTab(tabs, "Chat", (chatTab = new ChatTab()), 0);
		addTab(tabs, "Verbindung", setupConnectionTab(), 1);
		addTab(tabs, "Einstellungen", (settingsTab = new SettingsTab()), 2);

		setContentPane(tabs);
		setVisible(true);
	}

	private void addTab(JTabbedPane tabs, String title, JPanel panel, int index) {
		JLabel label = new JLabel(title, SwingConstants.CENTER);
		label.setFont(font);
		label.setPreferredSize(new Dimension(104, 30));
		tabs.add(panel);
		tabs.setTabComponentAt(index, label);
	}

	private void setupFont() {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			List<String> availableFonts = Arrays.asList(ge.getAvailableFontFamilyNames());
			Font f = Font.createFont(Font.TRUETYPE_FONT, Window.class.getResourceAsStream("/fonts/AGENCYR.TTF"));
			if (!availableFonts.contains(f.getFontName())) ge.registerFont(f);
			font = new Font(f.getName(), Font.PLAIN, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JPanel setupConnectionTab() {
		JPanel result = new JPanel();

		return result;
	}

	public static Font getCustomFont() {
		return font;
	}

	public ChatTab getChatTab() {
		return chatTab;
	}

	public SettingsTab getSettingsTab() {
		return settingsTab;
	}

}
