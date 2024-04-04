package me.donut.ramolos.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;
import me.donut.ramolos.stats.PlayerStats;
import me.donut.ramolos.stats.Statistic;

public class PlayerStatsWindow extends JFrame {

	private JTable stats;
	private JTextField headerSearch;
	private JButton headerSearchGo;
	private FocusListener headerFocusListener;
	private DocumentListener searchChangeListener;
	private ActionListener searchKeyListener;
	private String currentUUID = "";

	public PlayerStatsWindow() {
		setTitle("Spielerstatistiken");
		setSize(300, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(Ramolos.getInstance().getWindow());
		setResizable(false);
		setIconImage(Ramolos.getInstance().getWindow().getIconImage());
		initListener();

		Font defaultFont = Window.getCustomFont();

		JPanel base = new JPanel();
		base.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		JPanel header = new JPanel();
		header.setLayout(new GridLayout(1, 2));
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		headerSearch = new JTextField("Spielername");
		headerSearch.setForeground(Color.GRAY);
		headerSearch.setFont(defaultFont);
		headerSearch.addFocusListener(headerFocusListener);
		headerSearch.getDocument().addDocumentListener(searchChangeListener);
		headerSearch.addActionListener(searchKeyListener);

		headerSearchGo = new JButton("Suche");
		headerSearchGo.setPreferredSize(new Dimension(150, 40));
		headerSearchGo.setFont(defaultFont);
		headerSearchGo.addActionListener(searchKeyListener);

		header.add(headerSearch);
		header.add(headerSearchGo);

		String[][] data = {
            { "Kills", "0" },
            { "Tode", "0" },
			{ "K/D", "0.0" }
        };

		stats = new JTable(data, new String[] {"", ""});
		stats.setFont(Window.getCustomFont());
		stats.setTableHeader(null);
		stats.setFocusable(false);
		stats.setRowSelectionAllowed(false);
		stats.setDefaultEditor(Object.class, null);
		stats.setRowHeight(23);

		base.add(header);
		base.add(stats);
		add(base);
		pack();
		setVisible(true);
	}

	private void initListener() {
		headerFocusListener = new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (headerSearch.getText().equals("Spielername")) {
					headerSearch.setText("");
					headerSearch.setForeground(Color.decode("#BBBBBB"));
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (headerSearch.getText().isEmpty()) {
					headerSearch.setForeground(Color.decode("#878787"));
					headerSearch.setText("Spielername");
				}
			}
		};

		searchChangeListener = new DocumentListener() {
			public void onChange() {
				headerSearch.setForeground(Color.decode("#BBBBBB"));
			}

			@Override public void changedUpdate(DocumentEvent e) { onChange(); }
			@Override public void removeUpdate(DocumentEvent e) { onChange(); }
			@Override public void insertUpdate(DocumentEvent e) { onChange(); }
		};

		searchKeyListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSearchColor(Utils.COLOR_GREEN);
				PlayerStats ps = Ramolos.getInstance().getPlayerStats();
				String name = headerSearch.getText();
				if (name.equals("Spielername") || name.length() == 0) return;
				String uuid = ps.getUUIDByName(name, false);
				
				if (uuid == null || uuid.equals("")) {
					setSearchColor(Utils.COLOR_RED);
					setStats(uuid, new int[3]);
					currentUUID = "";
					return;
				}
				currentUUID = uuid;
				int[] stats = ps.getStats(uuid);
				if (stats == null) {
					setSearchColor(Utils.COLOR_YELLOW);
					stats = new int[3];
				}
				setStats(uuid, stats);
			}
		};
	}

	public void setSearchColor(String color) {
		headerSearch.setForeground(Color.decode(color));
	}

	public void setStats(String uuid, int[] values) {
		if (!currentUUID.equals("") && !currentUUID.equals(uuid)) return;
		stats.setValueAt("" + values[0], 0, 1);
		stats.setValueAt("" + values[1], 1, 1);
		if (values[1] == 0) values[1] = 1;
		double kd = (double) values[0] / (double) values[1];
		stats.setValueAt(Statistic.defaultFormat.format(kd), 2, 1);
	}
}
