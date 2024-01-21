package me.donut.ramolos.connection;

import java.util.ArrayList;
import java.util.HashMap;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Setup;
import me.donut.ramolos.Setup.Player;
import me.donut.ramolos.window.ConnectionTab;

public class SetupPacket extends Packet {

	@Override
	public int getID() {
		return 5;
	}

	public SetupPacket(String[] args) {
		boolean foundPlayers = false;
		Setup setup = Ramolos.getInstance().getSetup();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.length() == 0) continue;
			if (arg.equals("|")) {
				foundPlayers = true;
				continue;
			}

			if (!foundPlayers) setup.addSetting(arg);
			else setup.addPlayer(arg);
		}
		if (args.length != 4) return;
		ConnectionTab ct = Ramolos.getInstance().getWindow().getConnectionTab();
		boolean loggedIn = Boolean.valueOf(args[1]);
		boolean isAdmin = Boolean.valueOf(args[3]);
		Ramolos.getInstance().getConnector().setAuthenticated(loggedIn);
		if (!loggedIn) return;
		Ramolos.getInstance().getSettings().setUserID(Ramolos.getInstance().getWindow().getConnectionTab().getUserIdEntry());
		ct.updateConnectionStatus(true);
		ct.updateUserName(args[2]);
		Ramolos.getInstance().getConnector().setAdminMode(isAdmin);
	}

	public SetupPacket(HashMap<String, String> settings, ArrayList<Player> players) {
		String result = "";

		for (String key : settings.keySet()) {
			result += key + ":" + settings.get(key) + ";";
		}
		result += ";|;";
		for (Player p : players) {
			result += String.format("%s:%s:%s:%s;",
				p.getCode(),
				p.getName(),
				p.getClan(),
				p.isAdmin()
			);
		}

		send(new String[] { "true", result });
	}

	public SetupPacket() {
		send(new String[] { "false" });
	}
}
