package me.donut.ramolos.window;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.List;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.awt.event.WindowAdapter;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.connection.Updater;

public class Window extends JFrame {

	public static final Dimension WINDOW_SIZE = new Dimension(416, 550);
	private static final String TITLE = "Ramolos";
	private static final String THEME_PATH = "/themes/one_dark.theme.json";

	private static Font font;

	private JFrame frame;
	private ChatTab chatTab;
	private SettingsTab settingsTab;
	private ConnectionTab connectionTab;
	private StatsTab statsTab;
	
	public Window() {
		frame = this;
		FlatLightLaf.setup();
		IntelliJTheme.setup(Window.class.getResourceAsStream(THEME_PATH));
		setupFont();

		setTitle(TITLE + " - " + Ramolos.getInstance().getUpdater().getCurrentVersion());
		setLayout(new BorderLayout());
		setSize(WINDOW_SIZE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
				if (!Ramolos.getInstance().getConnector().isConnected()) {
					Ramolos.getInstance().terminate();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					e.getWindow().dispose();;
					return;
				}

				int response = JOptionPane.showConfirmDialog(
					frame, 
					"Die Verbindung zum Server wird abgebrochen wenn du das Fenster schließt, trotzdem schließen?",
					"Verbindung trennen?", 
					JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					Ramolos.getInstance().terminate();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} else {
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		
		JTabbedPane tabs = new JTabbedPane();
		addTab(tabs, "Chat", (chatTab = new ChatTab()), 0);
		addTab(tabs, "Statistiken", (statsTab = new StatsTab()), 1);
		addTab(tabs, "Verbindung", (connectionTab = new ConnectionTab()), 2);
		addTab(tabs, "Einstellungen", (settingsTab = new SettingsTab()), 3);

		setContentPane(tabs);
		setVisible(true);
	}

	private void addTab(JTabbedPane tabs, String title, JPanel panel, int index) {
		JLabel label = new JLabel(title, SwingConstants.CENTER);
		label.setFont(new Font(font.getName(), Font.PLAIN, 20));
		label.setPreferredSize(new Dimension(76, 30));
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
	public static Font getCustomFont() {
		return font;
	}

	public ChatTab getChatTab() {
		return chatTab;
	}

	public SettingsTab getSettingsTab() {
		return settingsTab;
	}

	public ConnectionTab getConnectionTab() {
		return connectionTab;
	}

	public StatsTab getStatsTab() {
		return statsTab;
	}

	public void showSimpleInfoDialog(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public void showOutdatedClientMessage() {
		Updater updater = Ramolos.getInstance().getUpdater();
		if (updater.isUpToDate()) return;

		int doUpdate = JOptionPane.showConfirmDialog(frame, "Eine neue Version von Ramolos ist"
			+ " verfügbar. Einige Funktionen sind gesperrt und es können "
			+ "Fehler aufteten. Möchtest du die neuste Version installieren?",
			"Neue Version verfügbar", JOptionPane.YES_NO_OPTION);
		
		if (doUpdate != JOptionPane.YES_OPTION) {
			setTitle(getTitle() + " (outdated)");
			return;
		}
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try { 
				Desktop.getDesktop().browse(new URI(Updater.URL_BASE + Updater.URL_DOWNLOAD));
				Ramolos.getInstance().terminate();
				dispose();
				return; 
			} catch (Exception ex) {}
		}
		showSimpleInfoDialog("Die neuste Version von Ramolos ist unter dieser URL verfügbar: " +
		Updater.URL_BASE + Updater.URL_DOWNLOAD
		, "Update herunterladen");
	}

	public void showChangelog() {
		Updater updater = Ramolos.getInstance().getUpdater();
		if (!updater.shouldSeeChangelog()) return;
		
		JOptionPane.showMessageDialog(frame, updater.getChangelog(), 
			"Changelog - " + updater.getCurrentVersion(), JOptionPane.PLAIN_MESSAGE);

		updater.setChangelogSeen();
	}

	public void showUpdateCheckFailed() {
		JOptionPane.showConfirmDialog(frame, "Es konnte nicht überprüft werden,"
			+ " ob die Version von Ramolos aktuell ist. Einige Funktionen sind"
			+ " gesperrt und es können Fehler aufteten.",
			"Update nicht abrufbar", JOptionPane.ERROR_MESSAGE);
		
	}

}
