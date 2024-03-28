package me.donut.ramolos.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import me.donut.ramolos.Ramolos;

public class Updater {

	public static final String URL_BASE = "https://ragearrows.de/media/minecraft/ramolos/";
	public static final String URL_VERSION = "version.txt";
	public static final String URL_DOWNLOAD = "Ramolos$.jar";
	private String version = "";
	private String latest_version = "";
	private String build_time = "";
	private String changelog = ""; 
	private boolean dev = false;
	
	public Updater() {
		loadBuildNr();
		readChangelog();
		String versionNews = "";
		try {
			versionNews = fetchLatestVersion();
		} catch (Exception ex) {
			Ramolos.getInstance().getWindow().showUpdateCheckFailed();
		}

		if (versionNews.equals("")) return;
		String split[] = versionNews.split("\n", 2);
		if (split.length != 0) latest_version = split[0];
		if (split.length > 1) changelog = split[1].trim();
	}

	private String fetchLatestVersion() throws Exception {
		URL url = new URL(URL_BASE + URL_VERSION);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		if (con.getResponseCode() != 200) {
			return "";
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String result = in.readLine();
		in.close();
		con.disconnect();
		return result;
	}

	public void readChangelog() {
		changelog = "";
		InputStream is = this.getClass().getResourceAsStream("/changelog.txt");
		try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
			BufferedReader reader = new BufferedReader(streamReader)) {
			String line;
			while ((line = reader.readLine()) != null) {
				changelog += line + "\n";
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getCurrentVersion() {
		return version;
	}

	public String getLatestVersion() {
		return latest_version;
	}

	public String getBuildTime() {
		return build_time;
	}

	public String getChangelog() {
		return changelog;
	}

	public boolean isUpToDate() {
		if (isDevMode()) return true;
		return version.equals(latest_version);
	}

	public boolean shouldSeeChangelog() {
		return isUpToDate() && !changelog.equals("") && !Ramolos.getInstance().getSettings().getLastUsedVersion().equals(version);
	}

	public void setChangelogSeen() {
		Ramolos.getInstance().getSettings().setLastUsedVersion(version);
	}

	private void loadBuildNr() {
		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/app.properties"));
		} catch (Exception e) {}
		version = String.join(".", 
			properties.getProperty("major", "0"),
			properties.getProperty("minor", "0"),
			properties.getProperty("build", "0"));
		build_time = properties.getProperty("time");
		dev = Boolean.parseBoolean(properties.getProperty("dev"));
	}

	public boolean isFeatureLocked() {
		if (isUpToDate()) return false;
		if (isDevMode()) return false;
		Ramolos.getInstance().getWindow().showSimpleInfoDialog( 
			"Diese Funktion ist gesperrt, da dein Client veraltet ist. Starte "
			+ "das Programm neu und installiere die neuste Version um "
			+ "fortzufahren", "Funktion gesperrt");
		return true;
	}

	public boolean isDevMode() {
		return dev;
	}
}
