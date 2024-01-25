package me.donut.ramolos.window;

import javax.swing.*;

public class StatsTab extends JPanel {

	public StatsTab() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		String[][] data = {
			{ "Kills", "Coming soon!" },
			{ "Nemesiskills", "Coming soon!" },
			{ "Deaths", "Coming soon!" },
			{ "Axe Deaths", "Coming soon!" },
			{ "Axe Deaths %", "Coming soon!" },
			{ "Selfdestructs", "Coming soon!" },
			{ "Playtime", "Coming soon!" },
			{ "Rounds played", "Coming soon!" },
			{ "KD", "Coming soon!" },
			{ "highest Killstreak", "Coming soon!" },
			{ "average Killstreak", "Coming soon!" },
			{ "average kills / round", "Coming soon!" },
			{ "average kills / minute", "Coming soon!" },
			{ "average deaths / round", "Coming soon!" },
			{ "average deaths / minute", "Coming soon!" },
			{ "average killstreak on death", "Coming soon!" },
			{ "minest gotten", "Coming soon!" },
			{ "speed gotten", "Coming soon!" },
			{ "dogs gotten", "Coming soon!" },
			{ "rapidfire gotten", "Coming soon!" },
			{ "armor gotten", "Coming soon!" },
			{ "angels gotten", "Coming soon!" },
			{ "nukes gotten", "Coming soon!" },
			{ "classic rounds won", "Coming soon!" }
		};

		JTable infoTable = new JTable(data, new String[] {"", ""});
		infoTable.setFont(Window.getCustomFont());
		infoTable.setTableHeader(null);
		infoTable.setFocusable(false);
		infoTable.setRowSelectionAllowed(false);
		// infoTable.setPreferredSize(new Dimension(250, 560));
		infoTable.setRowHeight(23);
		JScrollPane listScroller = new JScrollPane(infoTable);
		add(listScroller);

		add(new JButton("Stats gegen Spieler X"));
	}
}
