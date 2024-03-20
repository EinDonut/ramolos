package me.donut.ramolos;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;

import me.donut.ramolos.Translator.Language;

public class Settings {
	
	private Preferences prefs;
	private boolean autoDetectOS;
	private boolean autoDetectClient;
	private boolean autoDetectPath;
	private boolean autoDetectLanguage;
	private Client client;
	private OsSystem osSystem;
	private String logPath;
	private boolean validPath = false;
	private int port;
	private Language language;
	private String userID;
	private int notifSound = 0;
	private int notifSoundVolume = 50;
	private String lastVersion;
	private Point windowLocation;
	private boolean useToast;

	private final String CLIENT_AUTO = "settings.autoDetect.client";
	private final String OS_AUTO = "settings.autoDetect.os";
	private final String CLIENT = "settings.client";
	private final String OS = "settings.os";
	private final String LOG_PATH = "settings.logPath";
	private final String LOG_PATH_AUTO = "settings.autoDetect.logPath";
	private final String PORT = "settings.port";
	private final String USERID = "settings.userId";
	private final String NOTIF_SOUND = "settings.notifSound";
	private final String NOTIF_SOUND_VOLUME = "settings.notifSoundVolume";
	private final String LAST_VERSION = "version";
	private final String LANGUAGE_AUTO = "settings.autoDetect.language";
	private final String LANGUAGE = "settings.language";
	private final String WINDOW_LOCATION = "settings.windowPosition";
	private final String TOAST = "settings.toast";

	public Settings() {
		initPreferences();
	}

	private void initPreferences() {
		prefs = Preferences.userRoot().node("donut.rmls2");

		autoDetectClient = prefs.getBoolean(CLIENT_AUTO, false);
		autoDetectOS = prefs.getBoolean(OS_AUTO, true);
		autoDetectPath = prefs.getBoolean(LOG_PATH_AUTO, true);
		port = prefs.getInt(PORT, 4000);
		userID = prefs.get(USERID, "");
		notifSoundVolume = prefs.getInt(NOTIF_SOUND_VOLUME, 50);
		notifSound = prefs.getInt(NOTIF_SOUND, 0);
		lastVersion = prefs.get(LAST_VERSION, "");
		autoDetectLanguage = prefs.getBoolean(LANGUAGE_AUTO, true);
		language = Language.valueOf(prefs.get(LANGUAGE, Language.GERMAN.name()));
		useToast = prefs.getBoolean(TOAST, true);
		windowLocation = new Point(
			prefs.getInt(WINDOW_LOCATION + ".x", -9999), 
			prefs.getInt(WINDOW_LOCATION + ".y", -9999));

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

		changeClient(Client.values()[lastIndex == -1 ? 0 : lastIndex]);
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

	public Point getWindowLocation() {
		if (windowLocation.equals(new Point(-9999, -9999))) return null;
		return windowLocation;
	}

	public void setWindowLocation(Point location) {
		windowLocation = location;
		prefs.putInt(WINDOW_LOCATION + ".x", location.x);
		prefs.putInt(WINDOW_LOCATION + ".y", location.y);
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language lang) {
		if (lang != language) {
			Ramolos.getInstance().getTranslator().changeLanguage(lang);
			Ramolos.getInstance().getWindow().getSettingsTab().updateLanguageDisplay(lang);
		}
		language = lang;
		prefs.put(LANGUAGE, lang.name());
	}

	public boolean isAutoDetectLanguage() {
		return autoDetectLanguage;
	}

	public void setLanguageAutoDetect(boolean value) {
		autoDetectLanguage = value;
		prefs.putBoolean(LANGUAGE_AUTO, value);
	}

	public boolean useToastNotification() {
		return useToast;
	}

	public void setUseToastNotification(boolean value) {
		useToast = value;
		prefs.putBoolean(TOAST, value);
		if (value) Ramolos.getInstance().getToastNotifier().addTrayIcon();
		else Ramolos.getInstance().getToastNotifier().removeTrayIcon();
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
				if (!pathPart.equals("&home&"))
					path += pathPart.equals("&mcdir&") ? mcdir : pathPart;
				path += pathPart.endsWith(".log") ? "" : File.separator;
			}
			result[i] = path;
		}

		return result;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
		prefs.putInt(PORT, port);
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
		prefs.put(USERID, userID);
	}

	public int getNotificationVolume() {
		return notifSoundVolume;
	}

	public void setNotificationSoundVolume(int volume) {
		this.notifSoundVolume= volume;
		prefs.putInt(NOTIF_SOUND_VOLUME, volume);
	}

	public int getNotificationSound() {
		return notifSound;
	}

	public void setNotificationSound(int sound) {
		this.notifSound = sound;
		prefs.putInt(NOTIF_SOUND, sound);
	}

	public String getLastUsedVersion() {
		return lastVersion;
	}

	public void setLastUsedVersion(String lastVersion) {
		this.lastVersion = lastVersion;
		prefs.put(LAST_VERSION, lastVersion);
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
}