package me.donut.ramolos.stats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.window.PlayerStatsWindow;

public class PlayerStats {

	private final String MOJANG_URL = "https://api.mojang.com/users/profiles/minecraft/";
	public static final int STATS_LENGTH = 3;
	private HashMap<String, String> players;
	private HashMap<String, int[]> stats;
	
	public PlayerStats() {
		players = new HashMap<>();
		stats = new HashMap<>();
	}

	public String getUUIDByName(String username, boolean save) {
		username = username.toLowerCase();
		// CACHE
		if (players.containsKey(username)) {
			return players.get(username);
		}

		// DB
		String dbUUID = Ramolos.getInstance().getDatabaseConnection().readPlayer(username);
		if (dbUUID != null) {
			addPlayer(dbUUID, username.toLowerCase());
			return dbUUID;
		}

		// API
		String result = fetch(MOJANG_URL + username);
		String[] split = result.split("\"");
		if (split.length != 9) return "";

		String uuid = split[3];
		String name = split[7];

		addPlayer(uuid, name);
		if (save) Ramolos.getInstance().getDatabaseConnection().addPlayer(uuid, name);
		return uuid;
	}

	public int[] getStats(String uuid) {
		if (stats.containsKey(uuid)) return stats.get(uuid);
		int[] values = Ramolos.getInstance().getDatabaseConnection().getPlayerStats(uuid);
		if (values != null) stats.put(uuid, values);
		return values;
	}

	public HashMap<String, int[]> getStats() {
		return stats;
	}

	public void incrementStats(String name, int kills, int deaths) {
		String uuid = getUUIDByName(name, true);
		if (uuid.equals("")) return;

		int[] stat = stats.getOrDefault(uuid, new int[3]);
		stat[0] += kills;
		stat[1] += deaths;
		stats.put(uuid, stat);
		PlayerStatsWindow psw = Ramolos.getInstance().getWindow().getStatsTab().getPlayerStatsWindow();
		if (psw != null) psw.setStats(uuid, stat);
	}

	private String fetch(String urlText) {
		String result = "";
		try {
			URL url = new URL(urlText);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			if (con.getResponseCode() != 200) return result;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			con.disconnect();
			return result;
		} catch (Exception ex) { ex.printStackTrace(); }
		return result;
	}

	public void clearCache() {
		players.clear();
	}

	public void addPlayer(String uuid, String name) {
		players.put(name.toLowerCase(), uuid);
	}
}
