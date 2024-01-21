package me.donut.ramolos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.donut.ramolos.window.AdminWindow;

public class Setup {
	
	private HashMap<String, String> settings;
	private ArrayList<Player> players;
	private boolean running = false;

	public Setup() {
		settings = new HashMap<>();
		players = new ArrayList<>();
	}

	public HashMap<String, String> getSettings() {
		return settings;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setRunning(boolean running) {
		this.running = running;
		AdminWindow aw = Ramolos.getInstance().getWindow().getConnectionTab().getAdminWindow();
		if (aw != null) aw.dispose();
	}

	public boolean isRunning() {
		return running;
	}

	public String generateUnusedId() {
		String result = "";
		Random r = new Random();
		a: while (true) {
			result = "";
			for (int i = 0; i < 8; i++) {
				result += (char)(r.nextInt(26) + 'A');
			}
			for (Player p : getPlayers()) {
				if (p.getCode().equals(result)) continue a;
			}
			break;
		}
		return result;
	}

	public void addSetting(String data) {
		String[] split = data.split(":");
		if (split.length != 2) return;
		settings.put(split[0], split[1]);
		AdminWindow aw = Ramolos.getInstance().getWindow().getConnectionTab().getAdminWindow();
		if (aw != null) aw.changeSettings(split[0], split[1]);
	}

	public void addPlayer(String data) {
		String[] split = data.split(":");
		if (split.length != 5) return;

		int targetIndex = -1;
		Player p = null;
		for (int i = 0; i < getPlayers().size(); i++) {
			p = getPlayers().get(i);
			if (!p.getCode().equals(split[0])) continue;
			targetIndex = i;
			break;
		}

		if (targetIndex == -1) {
			p= new Player(
				split[0], 
				split[1], 
				split[2], 
				Boolean.valueOf(split[3]), 
				Boolean.valueOf(split[4])
			);
			players.add(p);
		} else {
			p.setName(split[1]);
			p.setClan(split[2]);
			p.setAdmin(Boolean.valueOf(split[3]));
			p.setOnline(Boolean.valueOf(split[4]));
			players.set(targetIndex, p);
		}
		AdminWindow aw = Ramolos.getInstance().getWindow().getConnectionTab().getAdminWindow();
		if (aw != null) aw.changePlayer(p);
	}

	public class Player {

		private String name;
		private String clan;
		private String code;
		private boolean admin;
		private boolean online;

		public Player(String code, String name, String clan, boolean admin, boolean online) {
			this.name = name;
			this.clan = clan;
			this.code = code;
			this.admin = admin;
			this.online = online;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClan() {
			return clan;
		}

		public void setClan(String clan) {
			this.clan = clan;
		}

		public String getCode() {
			return code;
		}

		public boolean isAdmin() {
			return admin;
		}

		public void setAdmin(boolean admin) {
			this.admin = admin;
		}

		public boolean isOnline() {
			return online;
		}

		public void setOnline(boolean online) {
			this.online = online;
		}
	}
}
