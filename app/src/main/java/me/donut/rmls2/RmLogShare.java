package me.donut.rmls2;

import me.donut.rmls2.chatevents.Listener;
import me.donut.rmls2.window.Window;

public class RmLogShare {
	
	private static RmLogShare instance;
	public static String VERSION = "";
	public static String BUILD_TIME = "";
	private Window window;
	private Settings settings;
	private LogWatcher logWatcher;
	private Listener listener;

	public RmLogShare() {
		instance = this;
		settings = new Settings();
		window = new Window();
		logWatcher = new LogWatcher();
		listener = new Listener();
	}

	public static void main(String[] args) {
		new RmLogShare();
	}

	public static RmLogShare getInstance() {
		return instance;
	}

	public Window getWindow() {
		return window;
	}

	public Settings getSettings() {
		return settings;
	}

	public LogWatcher getLogWatcher() {
		return logWatcher;
	}

	public Listener getListener() {
		return listener;
	}

	/**
	 * Represent the minecraft client type
	 */
	public enum Client {
		VANILLA,
		BADLION,
		LUNAR,
		LABYMOD
	}
}
