package me.donut.ramolos;

import me.donut.ramolos.connection.Connector;
import me.donut.ramolos.connection.Updater;
import me.donut.ramolos.events.InactiveTimer;
import me.donut.ramolos.events.Listener;
import me.donut.ramolos.window.Window;

public class Ramolos {
	
	private static Ramolos instance;
	private Window window;
	private Settings settings;
	private LogWatcher logWatcher;
	private Listener listener;
	private InactiveTimer afkTimer;
	private Connector connector;
	// private DatabaseConnection dbConnection;
	private Setup setup;
	private Updater updater;

	public Ramolos() {
		instance = this;
		settings = new Settings();
		updater = new Updater();
		window = new Window();
		logWatcher = new LogWatcher();
		listener = new Listener();
		afkTimer = new InactiveTimer();
		connector = new Connector();
		// dbConnection = new DatabaseConnection();
		setup = new Setup();

		afkTimer.start();
		window.showOutdatedClientMessage();
		window.showChangelog();
	}

	public static void main(String[] args) {
		new Ramolos();
	}

	public static Ramolos getInstance() {
		return instance;
	}

	public void terminate() {
		logWatcher.terminate();
		connector.disconnect();
		// dbConnection.disconnect();
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

	public InactiveTimer getInactiveTimer() {
		return afkTimer;
	}

	public Connector getConnector() {
		return connector;
	}

	public Setup getSetup() {
		return setup;
	}

	public Updater getUpdater() {
		return updater;
	}
}
