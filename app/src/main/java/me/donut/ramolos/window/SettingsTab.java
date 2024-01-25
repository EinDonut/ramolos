package me.donut.ramolos.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Settings;
import me.donut.ramolos.Utils;
import me.donut.ramolos.Settings.Client;
import me.donut.ramolos.connection.Updater;

public class SettingsTab extends JPanel {
	
	private Settings settings = Ramolos.getInstance().getSettings();
	private JComboBox<Integer> osCombo;
	private JComboBox<Integer> clientCombo;
	private JTextArea pathText;
	private JLabel pathHint;
	private JButton pathButton;
	private JCheckBox osAuto;
	private JCheckBox clientAuto;

	public SettingsTab() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Font panelTitleFont = new Font(Window.getCustomFont().getName(), Font.PLAIN, 18);
		Font hintFont = new Font(Window.getCustomFont().getName(), Font.PLAIN, 16);
		Color borderColor = Color.decode("#505254");

		/******* CLIENT *******/
		JPanel client = new JPanel();
		client.setToolTipText(
			"Wähle den verwendeten Client aus. Mit der Schaltfläche 'Auto' wird\n" +
			"versucht den aktuellen Client automatisch zu erkennen, dazu ist es\n" +
			"gegebenfalls nötig, das Häkchen zu entfernen und neu zu setzen. Ist\n" +
			"der verwendete Client nicht aufgeführt, muss der Pfad im Bereich\n" +
			"'Log-Datei' manuell bearbeitet werden.");
		client.setMaximumSize(new Dimension(300, 110));
		client.setAlignmentX(Component.CENTER_ALIGNMENT);
		client.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		client.setBorder(BorderFactory.createTitledBorder(
			client.getBorder(), "Client", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		int clientValueSize = Settings.Client.values().length;
		Integer[] clientIndice = new Integer[clientValueSize];
		String[] clientLabels = new String[clientValueSize];
		ImageIcon[] clientIcons = new ImageIcon[clientValueSize];
		for (int i = 0; i < clientValueSize; i++) {
			Client c = Settings.Client.values()[i];
			clientIndice[i] = i;
			clientLabels[i] = c.getName();
			clientIcons[i] = c.getIcon();
		}

		clientAuto = new JCheckBox("Auto");
		clientCombo = new JComboBox<>(clientIndice);
		clientAuto.setSelected(settings.isAutoDetectClient());
		clientAuto.setFont(Window.getCustomFont());
		clientAuto.addActionListener(e -> {
			settings.setClientAutoDetect(clientAuto.isSelected());
			updateClientDisplay();
		});

		ComboBoxImageRenderer clientComboRenderer = new ComboBoxImageRenderer(clientLabels, clientIcons);
		clientComboRenderer.setHorizontalAlignment(ComboBoxImageRenderer.LEFT);
		clientCombo.setEnabled(!settings.isAutoDetectClient());
		clientCombo.setPreferredSize(new Dimension(150, 40));
		clientCombo.setRenderer(clientComboRenderer);
		clientCombo.setFont(Window.getCustomFont());
		clientCombo.addActionListener(e -> {
			settings.changeClient(Settings.Client.values()[clientCombo.getSelectedIndex()]);
		});
		updateClientDisplay();

		JLabel clientHint = new JLabel("*Vanilla und LabyMod unterscheiden sich nicht ");
		clientHint.setFont(hintFont);
		clientHint.setForeground(Color.decode("#BBBBBB"));

		client.add(clientAuto);
		client.add(clientCombo);
		client.add(clientHint);

		/******* OS *******/
		JPanel os = new JPanel();
		os.setToolTipText(
			"Wähle das verwendete Betriebssystem aus. Mit der Schaltfläche\n" +
			"'Auto' wird versucht das aktuellee Betriebssystem automatisch zu\n" +
			"erkennen, dazu ist es gegebenfalls nötig, das Häkchen zu entfernen\n" +
			"und neu zu setzen.");
		os.setFont(panelTitleFont);
		os.setAlignmentX(Component.CENTER_ALIGNMENT);
		os.setMaximumSize(new Dimension(300, 85));
		os.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		os.setBorder(BorderFactory.createTitledBorder(
			os.getBorder(), "Betriebssystem", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		int osValueSize = Settings.OsSystem.values().length;
		Integer[] osIndice = new Integer[osValueSize];
		String[] osLabels = new String[osValueSize];
		ImageIcon[] osIcons = new ImageIcon[osValueSize];
		for (int i = 0; i < osValueSize; i++) {
			Settings.OsSystem c = Settings.OsSystem.values()[i];
			osIndice[i] = i;
			osLabels[i] = c.getName();
			osIcons[i] = c.getIcon();
		}

		osAuto = new JCheckBox("Auto");
		osCombo = new JComboBox<>(osIndice);
		osAuto.setSelected(settings.isAutoDetectSystem());
		osAuto.setFont(Window.getCustomFont());
		osAuto.addActionListener(e -> {
			settings.setOsAutoDetect(osAuto.isSelected());
			updateOsDisplay();
		});

		ComboBoxImageRenderer osComboRenderer = new ComboBoxImageRenderer(osLabels, osIcons);
		osComboRenderer.setHorizontalAlignment(ComboBoxImageRenderer.LEFT);
		osCombo.setPreferredSize(new Dimension(150, 40));
		osCombo.setRenderer(osComboRenderer);
		osCombo.setFont(Window.getCustomFont());
		osCombo.addActionListener(e -> {
			settings.changeOS(Settings.OsSystem.values()[osCombo.getSelectedIndex()]);
		});
		updateOsDisplay();

		os.add(osAuto);
		os.add(osCombo);

		/******* PATH *******/
		JPanel path = new JPanel();
		path.setToolTipText(
			"Sind die oberen beiden Einstellungen gesetzt, wird daraus der Pfad\n" + 
			"zur Log-Datei automatisch erzeugt. Ist dies nicht möglich oder wird\n" +
			"ein falscher Pfad angezeigt, so kann das Häkchen 'Auto' entfernt\n" +
			"werden und der Pfad manuell bearbeitet werden.");
		path.setFont(panelTitleFont);
		path.setAlignmentX(Component.CENTER_ALIGNMENT);
		path.setMaximumSize(new Dimension(300, 210));
		path.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		path.setBorder(BorderFactory.createTitledBorder(
			path.getBorder(), "Log-Datei", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		JCheckBox pathAuto = new JCheckBox("Auto");
		pathAuto.setSelected(settings.isAutoDetectPath());
		pathAuto.setFont(Window.getCustomFont());
		pathAuto.addActionListener(e -> {
			settings.setPathAutoDetect(pathAuto.isSelected());
		});
		
		pathButton = new JButton("Datei wählen");
		pathButton.setPreferredSize(new Dimension(150, 40));
		pathButton.setFont(Window.getCustomFont());
		pathButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setApproveButtonText("Auswählen");
			fileChooser.setDialogTitle("Aktive Log-Datei auswählen");
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileNameExtensionFilter("Log-Dateien (*.log)", "log"));
			int result = fileChooser.showDialog(this, null);
			if (result == JFileChooser.ERROR_OPTION || result == JFileChooser.CANCEL_OPTION) return;
			settings.changePath(fileChooser.getSelectedFile().getAbsolutePath());
		});

		pathText = new JTextArea(settings.getPath());
		pathText.setPreferredSize(new Dimension(250, 40));
		pathText.setLineWrap(true);
		pathText.setRows(2);
		pathText.setEditable(false);
		pathText.setFont(panelTitleFont);

		pathHint = new JLabel("", SwingConstants.CENTER);
		pathHint.setFont(panelTitleFont);
		pathHint.setPreferredSize(new Dimension(150, 40));
		updatePathDisplay();
		
		path.add(pathAuto);
		path.add(pathButton);
		path.add(pathText);
		path.add(pathHint);

		JPanel footer = new JPanel();
		footer.setAlignmentX(Component.CENTER_ALIGNMENT);
		footer.setMaximumSize(new Dimension(300, 80));
		JLabel credits = new JLabel("by donut - V." + 
			Ramolos.getInstance().getUpdater().getCurrentVersion(), SwingConstants.CENTER);
		credits.setToolTipText("Build-Time: " + 
			Ramolos.getInstance().getUpdater().getBuildTime());
		credits.setPreferredSize(new Dimension(300, 20));
		credits.setFont(hintFont);
		JButton sources = new JButton("Open Source Lizenzen");
		sources.setFont(hintFont);
		sources.addActionListener(e -> {
			JOptionPane.showMessageDialog(Ramolos.getInstance().getWindow(), 
				"Gradle Shadow - johnrengelman\n" +
				"FlatLaf - JFormDesigner\n" + 
				"One Dark Scheme - Mark Skelton", 
				"Verwendete Open Source Software", JOptionPane.PLAIN_MESSAGE);
		});

		JButton changelog = new JButton("Changelog");
		changelog.setFont(hintFont);
		changelog.addActionListener(e -> {
			Updater updater = Ramolos.getInstance().getUpdater();
			JOptionPane.showMessageDialog(Ramolos.getInstance().getWindow(), updater.getChangelog(), 
				"Changelog - " + updater.getCurrentVersion(), JOptionPane.PLAIN_MESSAGE);
		});

		footer.add(credits);
		footer.add(sources);
		footer.add(changelog);

		add(Box.createRigidArea(new Dimension(0, 10)));
		add(client);
		add(Box.createRigidArea(new Dimension(0 , 10)));
		add(os);
		add(Box.createRigidArea(new Dimension(0 , 10)));
		add(path);
		add(Box.createRigidArea(new Dimension(0 , 30)));
		add(footer);

	}

	public void updateClientDisplay() {
		int newIndex = Utils.getIndexByValue(Settings.Client.class, settings.getClient());
		int oldIndex = clientCombo.getSelectedIndex();
		if (newIndex != oldIndex) clientCombo.setSelectedIndex(newIndex);
		clientCombo.setEnabled(!settings.isAutoDetectClient() && settings.isAutoDetectPath());
	}

	public void updateOsDisplay() {
		int newIndex = Utils.getIndexByValue(Settings.OsSystem.class, settings.getOsSystem());
		int oldIndex = osCombo.getSelectedIndex();
		if (newIndex != oldIndex) osCombo.setSelectedIndex(newIndex);
		osCombo.setEnabled(!settings.isAutoDetectSystem() && settings.isAutoDetectPath());
	}

	public void updatePathDisplay() {
		pathText.setText(settings.getPath());
		pathHint.setText(settings.isValidPath() ? "Datei gefunden" : "Datei nicht gefunden");
		pathHint.setForeground(Color.decode(settings.isValidPath() ? Utils.COLOR_GREEN : Utils.COLOR_RED));

		pathButton.setEnabled(!settings.isAutoDetectPath());
		osCombo.setEnabled(!settings.isAutoDetectSystem() && settings.isAutoDetectPath());
		clientCombo.setEnabled(!settings.isAutoDetectClient() && settings.isAutoDetectPath());
		osAuto.setEnabled(settings.isAutoDetectPath());
		clientAuto.setEnabled(settings.isAutoDetectPath());
	}
}
