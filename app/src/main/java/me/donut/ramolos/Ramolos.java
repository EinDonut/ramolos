package me.donut.ramolos;

import me.donut.ramolos.chatevents.Listener;
import me.donut.ramolos.window.Window;

public class Ramolos {
	
	private static Ramolos instance;
	public static String VERSION = "";
	public static String BUILD_TIME = "";
	private Window window;
	private Settings settings;
	private LogWatcher logWatcher;
	private Listener listener;

	public Ramolos() {
		instance = this;
		settings = new Settings();
		window = new Window();
		logWatcher = new LogWatcher();
		listener = new Listener();
	}

	public static void main(String[] args) {
		new Ramolos();
	}

	public static Ramolos getInstance() {
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
