package me.donut.ramolos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;


public class Settings {
	
	private Preferences prefs;
	private boolean autoDetectOS;
	private boolean autoDetectClient;
	private boolean autoDetectPath;
	private Client client;
	private OsSystem osSystem;
	private String logPath;
	private boolean validPath = false;

	private final String CLIENT_AUTO = "settings.autoDetect.client";
	private final String OS_AUTO = "settings.autoDetect.os";
	private final String CLIENT = "settings.client";
	private final String OS = "settings.os";
	private final String LOG_PATH = "settings.logPath";
	private final String LOG_PATH_AUTO = "settings.autoDetect.logPath";

	public Settings() {
		loadBuildNr();
		initPreferences();
	}

	private void initPreferences() {
		prefs = Preferences.userRoot().node("donut.rmls2");

		autoDetectClient = prefs.getBoolean(CLIENT_AUTO, false); // TODO
		autoDetectOS = prefs.getBoolean(OS_AUTO, true);
		autoDetectPath = prefs.getBoolean(LOG_PATH_AUTO, true);
		System.out.println("read " + autoDetectPath);

		client = Client.values()[prefs.getInt(CLIENT, 0)];
		osSystem = OsSystem.values()[prefs.getInt(OS, 0)];
		logPath = prefs.get(LOG_PATH, "");
		validPath = Utils.fileExists(logPath);

		if (autoDetectClient) detectClient();
		if (autoDetectOS) detectOS();
		detectPath();
	}

	public void detectClient() {
		if (!autoDetectClient) return;

		FileTime lastTime = null;
		int lastIndex = -1;

		String[] files = generatePaths();
		for (int i = 0; i < files.length; i++) {
			if (!Utils.fileExists(files[i])) continue;
			try {
				BasicFileAttributes attr = Files.readAttributes(Paths.get(files[i]), BasicFileAttributes.class);
				FileTime updateTime = attr.lastModifiedTime();
				if (lastTime == null || updateTime.compareTo(lastTime) >= 0) {
					lastTime = updateTime;
					lastIndex = i;
				}
			} catch (IOException e) {
				continue;
			}
		}

		changeClient(Client.values()[lastIndex]);
	}

	public void detectOS() {
		if (!autoDetectOS) return;
		final String read_os = System.getProperty("os.name").toLowerCase();
		if (read_os.indexOf("win") >= 0) osSystem = OsSystem.WINDOWS;
		else if (read_os.indexOf("mac") >= 0) osSystem = OsSystem.MACOS;
		else if (read_os.indexOf("nix") >= 0 || read_os.indexOf("nux") >= 0 || read_os.indexOf("aix") > 0) osSystem = OsSystem.LINUX;
		else osSystem = OsSystem.WINDOWS;

		changeOS(osSystem);
	}

	public void detectPath() {
		if (!autoDetectPath) return;
		String[] paths = generatePaths();
		String path = paths[Utils.getIndexByValue(Client.class, client)];

		changePath(path);
	}

	public void changeOS(OsSystem os) {
		osSystem = os;
		prefs.putInt(OS, Utils.getIndexByValue(OsSystem.class, osSystem));
		detectPath();
	}

	public void changeClient(Client client) {
		this.client = client;
		prefs.putInt(CLIENT, Utils.getIndexByValue(Client.class, this.client));
		detectPath();
	}

	public void changePath(String path) {
		logPath = path;
		prefs.put(LOG_PATH, logPath);

		validPath = Utils.fileExists(path);

		if (Ramolos.getInstance().getWindow() == null) return;
		Ramolos.getInstance().getWindow().getSettingsTab().updatePathDisplay();
	}

	public void setOsAutoDetect(boolean value) {
		autoDetectOS = value;
		prefs.putBoolean(OS_AUTO, autoDetectOS);
		if (value) detectOS();
		else changeOS(osSystem);
	}

	public void setClientAutoDetect(boolean value) {
		autoDetectClient = value;
		prefs.putBoolean(CLIENT_AUTO, autoDetectClient);
		if (value) detectClient();
		else changeClient(client);
	}

	public void setPathAutoDetect(boolean value) {
		autoDetectPath = value;
		prefs.putBoolean(LOG_PATH_AUTO, autoDetectPath);
		if (value) detectPath();
		else changePath(logPath);
	}

	public Client getClient() {
		return client;
	}

	public OsSystem getOsSystem() {
		return osSystem;
	}

	public String getPath() {
		return logPath;
	}

	public boolean isAutoDetectClient() {
		return autoDetectClient;
	}

	public boolean isAutoDetectSystem() {
		return autoDetectOS;
	}

	public boolean isAutoDetectPath() {
		return autoDetectPath;
	}

	public boolean isValidPath() {
		return validPath;
	}

	private String[] generatePaths() {
		String[] result = new String[Client.values().length];
		String mcdir = "";

		for (String pathPartOs : osSystem.path) {
			mcdir += (pathPartOs.equals("&home&")) ? System.getProperty("user.home") : pathPartOs;	
			mcdir += File.separator;
		}
		if (mcdir.endsWith(File.separator)) mcdir = mcdir.substring(0, mcdir.length() - 1);

		for (int i = 0; i < result.length; i++) {
			Client c = Client.values()[i];
			String path = "";

			for (String pathPart : c.getPath()) {
				path += pathPart.equals("&home&") ? System.getProperty("user.home") : "";	
				path += pathPart.equals("&mcdir&") ? mcdir : pathPart;
				path += pathPart.endsWith(".log") ? "" : File.separator;
			}
			result[i] = path;
		}

		return result;
	}

	public enum OsSystem {

		WINDOWS("Windows", "/images/os/windows.png", "&home&", "AppData", "Roaming", ".minecraft"),
		MACOS("macOS", "/images/os/macos.png", "&home&", "Library", "Application Support", "minecraft"),
		LINUX("Linux", "/images/os/linux.png", "&home&", ".minecraft");
		
		String name;
		String[] path;
		ImageIcon icon;
		
		OsSystem(String name, String icon, String... path) {
			this.name = name;
			this.path = path;	
			this.icon = Utils.getIcon(icon);
		}

		public String getName() {
			return name;
		}

		public String[] getPath() {
			return path;
		}

		public ImageIcon getIcon() {
			return icon;
		}
	}

	public enum Client {

		VANILLA("Vanilla*", "/images/clients/vanilla.png", "&mcdir&", "logs", "latest.log"),
		LabyMod("LabyMod*", "/images/clients/labymod.png", "&mcdir&", "logs", "latest.log"),
		Badlion("Badlion", "/images/clients/badlion.png", "&mcdir&", "logs", "blclient", "minecraft", "latest.log"),
		Lunar("Lunar", "/images/clients/lunar.png", "&home&", ".lunarclient", "offline", "multiver", "logs", "latest.log");
		
		String name;
		String[] path;
		ImageIcon icon;
		
		Client(String name, String icon, String... path) {
			this.name = name;
			this.path = path;	
			this.icon = Utils.getIcon(icon);
		}

		public String getName() {
			return name;
		}

		public String[] getPath() {
			return path;
		}

		public ImageIcon getIcon() {
			return icon;
		}
	}

	private void loadBuildNr() {
		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/app.properties"));
		} catch (Exception e) {}
		Ramolos.VERSION = String.join(".", 
			properties.getProperty("major", "0"),
			properties.getProperty("minor", "0"),
			properties.getProperty("build", "0"));
		Ramolos.BUILD_TIME = properties.getProperty("time");
	}

}