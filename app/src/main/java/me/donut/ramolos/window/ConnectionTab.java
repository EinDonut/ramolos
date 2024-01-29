package me.donut.ramolos.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;
import me.donut.ramolos.connection.TournmanetPacket;

public class ConnectionTab extends JPanel {
	
	private JTextField tfPort;
	private JPasswordField tfID;
	private JButton connect;
	private JTable infoTable;
	private JTextField serverMessage;
	private JPanel adminTools;
	private AdminWindow adminWindow;

	public ConnectionTab() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Font panelTitleFont = new Font(Window.getCustomFont().getName(), Font.PLAIN, 18);
		Font defaultFont = Window.getCustomFont();
		Color borderColor = Color.decode("#505254");

		/******* Parameter *******/
		JPanel params = new JPanel();
		params.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		params.setToolTipText(
			"Hier kannst du dich mit unserem Server verbinden, wenn du sowohl\n"
			+ "Port, als auch deine NutzerID von einem der Veranstalter bekommen hast.");
		params.setMaximumSize(new Dimension(300, 140));
		params.setAlignmentX(Component.CENTER_ALIGNMENT);
		params.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		params.setBorder(BorderFactory.createTitledBorder(
			params.getBorder(), "Parameter", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		JLabel lblPort = new JLabel("Port");
		lblPort.setFont(defaultFont);
		
		JLabel lblID = new JLabel("NutzerID");
		lblID.setFont(defaultFont);
		
		KeyListener resetErrorListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}	

			@Override
			public void keyPressed(KeyEvent e) {
				e.getComponent().setBackground(Color.decode("#282C34"));
			}

		};

		tfPort = new JTextField("" + Ramolos.getInstance().getSettings().getPort());
		tfPort.setFont(defaultFont);
		tfPort.addKeyListener(resetErrorListener);

		tfID = new JPasswordField(Ramolos.getInstance().getSettings().getUserID());
		tfID.setFont(defaultFont);
		tfID.addKeyListener(resetErrorListener);

		connect = new JButton("Verbinden");
		connect.setFont(defaultFont);
		connect.addActionListener(e -> {
			if (Ramolos.getInstance().getUpdater().isFeatureLocked()) return;

			if (connect.getText().equals("Verbinden"))
				Ramolos.getInstance().getConnector().connect();
			else
				Ramolos.getInstance().getConnector().disconnect();

		});

		c.insets = new Insets(0, 10, 0, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		params.add(lblPort, c);	
		c.gridx = 1;
		params.add(tfPort, c);
		c.gridx = 0;
		c.gridy = 1;
		params.add(lblID, c);
		c.gridx = 1;
		params.add(tfID, c);
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 2;
		params.add(connect, c);

		/******* Message *******/
		serverMessage = new JTextField();
		serverMessage.setHorizontalAlignment(JTextField.CENTER);
		serverMessage.setMaximumSize(new Dimension(Window.WINDOW_SIZE.width, 20));
		serverMessage.setFont(defaultFont);
		serverMessage.setEditable(false);
		serverMessage.setBorder(null);

		/******* Status *******/
		JPanel status = new JPanel();

		status.setToolTipText(
			"Ist nicht wirklich wichtig, aber sieht halt cooler aus.");
		status.setMaximumSize(new Dimension(300, 160));
		status.setAlignmentX(Component.CENTER_ALIGNMENT);
		status.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		status.setBorder(BorderFactory.createTitledBorder(
			status.getBorder(), "Statistiken", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));


		String[][] data = {
			{ "Verbindungsstatus", "-" },
			{ "Dein Name", "-" },
			{ "Pakete empfangen", "0" },
			{ "Pakete gesendet", "0"}
		};

		infoTable = new JTable(data, new String[] {"", ""});
		infoTable.setFont(defaultFont);
		infoTable.setFocusable(false);
		infoTable.setRowSelectionAllowed(false);
		infoTable.setPreferredSize(new Dimension(250, 100));
		infoTable.setRowHeight(23);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		infoTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
		status.add(infoTable);

		/******* AdminTools *******/
		adminTools = new JPanel();
		adminTools.setVisible(false);
		adminTools.setLayout(new GridBagLayout());
		GridBagConstraints atc = new GridBagConstraints();

		adminTools.setToolTipText(
			"Hier können Veranstalter von Turnieren und Events\n"
			+ "diese steuern und Einstellungen vornehmen");
		adminTools.setMaximumSize(new Dimension(300, 120));
		adminTools.setAlignmentX(Component.CENTER_ALIGNMENT);
		adminTools.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		adminTools.setBorder(BorderFactory.createTitledBorder(
			adminTools.getBorder(), "Admin-Tools", 
			TitledBorder.DEFAULT_JUSTIFICATION, 
			TitledBorder.DEFAULT_POSITION, panelTitleFont));

		JButton btnStart = new JButton("Start");
		btnStart.setFont(defaultFont);
		btnStart.addActionListener(e -> {
			if (!Ramolos.getInstance().getConnector().isAdminMode()) return;
			new TournmanetPacket("start");
		});

		JButton btnStop = new JButton("Stopp");
		btnStop.setFont(defaultFont);
		btnStop.addActionListener(e -> {
			if (!Ramolos.getInstance().getConnector().isAdminMode()) return;
			new TournmanetPacket("stop");
		});

		JButton btnAdvanced = new JButton("Erweitert");
		btnAdvanced.setFont(defaultFont);
		btnAdvanced.addActionListener(e -> {
			if (Ramolos.getInstance().getSetup().isRunning()) return;
			if (!Ramolos.getInstance().getConnector().isAdminMode()) return;
			adminWindow = new AdminWindow();
		});

		// atc.insets = new Insets(0, 10, 0, 10);
		atc.fill = GridBagConstraints.HORIZONTAL;
		atc.gridx = 0;
		atc.gridy = 0;
		adminTools.add(btnStart, atc);	
		atc.gridx = 1;
		adminTools.add(btnStop, atc);
		atc.gridx = 0;
		atc.gridy = 1;
		atc.gridwidth = 2;
		adminTools.add(btnAdvanced, atc);		

		add(Box.createRigidArea(new Dimension(0, 10)));
		add(params);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(status);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(adminTools);
		add(Box.createVerticalGlue());
		add(serverMessage);

		updateConnectionStatus(false);
		updateServerMessage("Testmessage!", Utils.COLOR_GREEN);
	}

	public void updateConnectionStatus(boolean connected) {
		String display = String.format("<html><font color='%s'>%s</font></html>",
			connected ? Utils.COLOR_GREEN : Utils.COLOR_RED,
			connected ? "Verbunden" : "Nicht verbunden"
		);
		
		infoTable.setValueAt(display, 0, 1);
		tfID.setEditable(!connected);
		tfPort.setEditable(!connected);
		tfID.setFocusable(!connected);
		tfPort.setFocusable(!connected);
		connect.setText(connected ? "Trennen" : "Verbinden");
	}

	public void updateReceivedPackets(int packets) {
		infoTable.setValueAt("" + packets, 2, 1);
	}

	public void updateSentPackets(int packets) {
		infoTable.setValueAt("" + packets, 3, 1);
	}

	public void updateUserName(String username) {
		infoTable.setValueAt(username, 1, 1);
	}

	public void updateServerMessage(String message, String color) {
		serverMessage.setText(message);
		serverMessage.setForeground(color.equals("") ? null : Color.decode(color));
	}

	public void setAdminToolsVisible(boolean enabled) {
		adminTools.setVisible(enabled);
	}

	public String getUserIdEntry() {
		return new String(tfID.getPassword());
	}

	public String getPortEntry() {
		return tfPort.getText();
	}

	public AdminWindow getAdminWindow() {
		return adminWindow;
	}

	public void updatePortValidity(boolean valid) {
		tfPort.setBackground(valid ? Color.decode("#282C34") : Color.decode(Utils.COLOR_RED));
	}

	public void updateUserIdValidity(boolean valid) {
		tfID.setBackground(valid ? Color.decode("#282C34") : Color.decode(Utils.COLOR_RED));
	}

	public void blockConnectButton(boolean block) {
		connect.setEnabled(!block);
	}
}
