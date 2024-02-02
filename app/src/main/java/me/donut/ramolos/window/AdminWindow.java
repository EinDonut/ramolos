package me.donut.ramolos.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Setup;
import me.donut.ramolos.Utils;
import me.donut.ramolos.Setup.Player;
import me.donut.ramolos.connection.AdminControlPacket;

public class AdminWindow extends JFrame {

	private JTable settings;
	private DefaultTableModel settingsModel;
	private JTable players;
	private DefaultTableModel playersModel;

	public AdminWindow() {
		setTitle("Erweiterte Einstellungen");
		setSize(300, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(Ramolos.getInstance().getWindow());
		setResizable(false);

		Font panelTitleFont = new Font(Window.getCustomFont().getName(), Font.PLAIN, 18);
		Font defaultFont = Window.getCustomFont();
		Color borderColor = Color.decode("#505254");

		JPanel base = new JPanel();
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		JPanel settingsPanel = new JPanel();
		settingsPanel.setToolTipText(
			"Wähle den verwendeten Client aus. Mit der Schaltfläche 'Auto' wird" +
			"versucht den aktuellen Client automatisch zu erkennen, dazu ist es" +
			"gegebenfalls nötig, das Häkchen zu entfernen und neu zu setzen. Ist" +
			"der verwendete Client nicht aufgeführt, muss der Pfad im Bereich " +
			"'Log-Datei' manuell bearbeitet werden.");
		// settingsPanel.setMaximumSize(new Dimension(300, 100));
		settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingsPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		settingsPanel.setBorder(BorderFactory.createTitledBorder(
			settingsPanel.getBorder(), "Einstellungen", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		settings = new JTable(new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int col) {
                return col != 0;
			}
		});
		settings.setFont(Window.getCustomFont());
		settings.setPreferredSize(new Dimension(250, 50));
		settings.setRowHeight(23);

		settingsModel = (DefaultTableModel) settings.getModel();
		settingsModel.addColumn("key"); 
		settingsModel.addColumn("value"); 

		settingsPanel.add(settings);

		JPanel playersPanel = new JPanel();
		playersPanel.setLayout(new GridBagLayout());
		GridBagConstraints ppc = new GridBagConstraints();
		playersPanel.setToolTipText(
			"Wähle den verwendeten Client aus. Mit der Schaltfläche 'Auto' wird" +
			"versucht den aktuellen Client automatisch zu erkennen, dazu ist es" +
			"gegebenfalls nötig, das Häkchen zu entfernen und neu zu setzen. Ist" +
			"der verwendete Client nicht aufgeführt, muss der Pfad im Bereich " +
			"'Log-Datei' manuell bearbeitet werden.");
		playersPanel.setMaximumSize(new Dimension(300, 200));
		playersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		playersPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		playersPanel.setBorder(BorderFactory.createTitledBorder(
			playersPanel.getBorder(), "Spieler", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		players = new JTable(new DefaultTableModel() {
			@Override
            public boolean isCellEditable(int row, int col) {
                return col != 3 && col != 0;
			}
		});
		playersModel = (DefaultTableModel) players.getModel();
		playersModel.addColumn("Online");
		playersModel.addColumn("Name");
		playersModel.addColumn("Clan");
		playersModel.addColumn("Code"); 
		playersModel.addColumn("Admin"); 
		players.setFont(Window.getCustomFont());
		players.setRowHeight(23);
		players.setRowSelectionAllowed(true);
		players.setAutoCreateRowSorter(true);
		players.getTableHeader().setFont(defaultFont);

		JPanel playerButtonPanel = new JPanel();
		playerButtonPanel.setLayout(new BoxLayout(playerButtonPanel, BoxLayout.Y_AXIS));
		JButton btnAddUser = new JButton("Hinzufügen");
		btnAddUser.setMaximumSize(new Dimension(200, 30));
		btnAddUser.addActionListener(e -> {
			playersModel.addRow(new String[] {
			String.format(
				Utils.COLOR_PLACEHOLDER, Utils.COLOR_RED, "Nein"),
				"",
				"",
				Ramolos.getInstance().getSetup().generateUnusedId(),
				"Nein", 
		});
		});

		JButton btnRemoveUser = new JButton("Entfernen");
		btnRemoveUser.setMaximumSize(btnAddUser.getMaximumSize());
		btnRemoveUser.addActionListener(e -> {
			if (!players.hasFocus()) return;
			if (players.getSelectedRow() == -1) return;
			playersModel.removeRow(players.getSelectedRow());
		});

		playerButtonPanel.add(btnAddUser);
		playerButtonPanel.add(btnRemoveUser);
		
		ppc.insets = new java.awt.Insets(0, 10, 0, 10);
		ppc.fill = GridBagConstraints.VERTICAL;
		ppc.gridx = 0;
		ppc.gridy = 0;
		playersPanel.add(add(new JScrollPane(players)), ppc);
		ppc.gridx = 1;
		playersPanel.add(playerButtonPanel, ppc);	

		JPanel actionsPanel = new JPanel();
		actionsPanel.setLayout(new GridBagLayout());
		// GridBagConstraints apc = new GridBagConstraints();

		actionsPanel.setToolTipText(
			"Wähle den verwendeten Client aus. Mit der Schaltfläche 'Auto' wird" +
			"versucht den aktuellen Client automatisch zu erkennen, dazu ist es" +
			"gegebenfalls nötig, das Häkchen zu entfernen und neu zu setzen. Ist" +
			"der verwendete Client nicht aufgeführt, muss der Pfad im Bereich " +
			"'Log-Datei' manuell bearbeitet werden.");
		// playersPanel.setMaximumSize(new Dimension(300, 160));
		actionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		actionsPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		actionsPanel.setBorder(BorderFactory.createTitledBorder(
			actionsPanel.getBorder(), "Aktionen", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		
		JButton btnSave = new JButton("Speichern");
		btnSave.setMaximumSize(btnAddUser.getMaximumSize());
		btnSave.addActionListener(e -> {
			if (!players.hasFocus()) return;
			if (players.getSelectedRow() == -1) return;
			playersModel.removeRow(players.getSelectedRow());
		});


		JButton btnStartNow = new JButton("Sofort starten");
		btnStartNow.setMaximumSize(btnAddUser.getMaximumSize());
		btnStartNow.addActionListener(e -> {
			new AdminControlPacket("startnow");
		});
		actionsPanel.add(btnStartNow);

		JButton btnExportRes = new JButton("Ergebnisse exportieren");
		btnExportRes.setMaximumSize(btnAddUser.getMaximumSize());
		btnExportRes.addActionListener(e -> {
			new AdminControlPacket("export");
		});
		actionsPanel.add(btnExportRes);

		JButton btnExportLog = new JButton("Log exportieren");
		btnExportLog.setMaximumSize(btnAddUser.getMaximumSize());
		btnExportLog.addActionListener(e -> {
			new AdminControlPacket("exportLog");
		});
		actionsPanel.add(btnExportLog);

		// c.insets = new Insets(0, 10, 0, 10);
		// apc.fill = GridBagConstraints.HORIZONTAL;
		// apc.gridx = 0;
		// apc.gridy = 0;
		// .add(lblPort, apc);	
		// apc.gridx = 1;
		// params.add(tfPort, apc);
		// apc.gridx = 0;
		// apc.gridy = 1;
		// params.add(lblID, apc);
		// apc.gridx = 1;
		// params.add(tfID, apc);
		// apc.gridwidth = 2;
		// apc.gridx = 0;
		// apc.gridy = 2;
		// params.add(connect, c);

		base.add(settingsPanel);
		base.add(Box.createRigidArea(new Dimension(0, 10)));
		base.add(playersPanel);
		base.add(Box.createRigidArea(new Dimension(0, 10)));
		base.add(actionsPanel);

		Setup setup = Ramolos.getInstance().getSetup();

		for (String key : setup.getSettings().keySet()) {
			changeSettings(key, setup.getSettings().get(key));
		}

		setup.getPlayers().forEach(p -> changePlayer(p));

		add(base);
		pack();
		setVisible(true);
	}

	public void changeSettings(String key, String value) {
		for (int i = 0; i < settingsModel.getRowCount(); i++) {
			if (!settingsModel.getValueAt(i, 0).equals(key)) continue;
			settingsModel.setValueAt(value, i, 1);
			return;
		}
		settingsModel.addRow(new String[] {key, value});
	}

	public void changePlayer(Player p) {
		for (int i = 0; i < playersModel.getRowCount(); i++) {
			if (!playersModel.getValueAt(i, 3).equals(p.getCode())) continue;
			playersModel.setValueAt(String.format(
				Utils.COLOR_PLACEHOLDER, 
				p.isOnline() ? Utils.COLOR_GREEN : Utils.COLOR_RED, 
				p.isOnline() ? "Ja" : "Nein"
			), i, 0);
			playersModel.setValueAt(p.getName(), i, 1);
			playersModel.setValueAt(p.getClan(), i, 2);
			playersModel.setValueAt(p.isAdmin() ? "Ja" : "Nein", i, 4);
			return;
		}
		playersModel.addRow(new String[] {
			String.format(
				Utils.COLOR_PLACEHOLDER, 
				p.isOnline() ? Utils.COLOR_GREEN : Utils.COLOR_RED, 
				p.isOnline() ? "Ja" : "Nein"),
			p.getName(),
			p.getClan(),
			p.getCode(),
			p.isAdmin() ? "Ja" : "Nein", 
		});
	}
}
