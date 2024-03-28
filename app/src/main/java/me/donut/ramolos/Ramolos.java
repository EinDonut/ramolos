package me.donut.ramolos;

import me.donut.ramolos.connection.Connector;
import me.donut.ramolos.connection.Updater;
import me.donut.ramolos.events.AxeDetector;
import me.donut.ramolos.events.Ticker;
import me.donut.ramolos.events.Listener;
import me.donut.ramolos.stats.DatabaseConnection;
import me.donut.ramolos.stats.StatisticManager;
import me.donut.ramolos.window.Window;

public class Ramolos {
	
	private static Ramolos instance;
	private Window window;
	private Settings settings;
	private LogWatcher logWatcher;
	private Listener listener;
	private Ticker afkTimer;
	private Connector connector;
	private Translator translator;
	private ToastNotifier toastNotifier;
	private DatabaseConnection dbConnection;
	private StatisticManager statisticManager;
	private AxeDetector axeDetector;
	private Setup setup;
	private Updater updater;

	public Ramolos() {
		instance = this;
		settings = new Settings();
		updater = new Updater();
		statisticManager = new StatisticManager();
		window = new Window();
		logWatcher = new LogWatcher();
		listener = new Listener();
		afkTimer = new Ticker();
		connector = new Connector();
		translator = new Translator();
		toastNotifier = new ToastNotifier();
		dbConnection = new DatabaseConnection();
		axeDetector = new AxeDetector();
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
		settings.setWindowLocation(window.getLocation());
		toastNotifier.removeTrayIcon();
		statisticManager.saveStatsFiltering();
		dbConnection.writeStats();
		dbConnection.disconnect();
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

	public Ticker getInactiveTimer() {
		return afkTimer;
	}

	public Connector getConnector() {
		return connector;
	}

	public DatabaseConnection getDatabaseConnection() {
		return dbConnection;
	}

	public Setup getSetup() {
		return setup;
	}

	public Updater getUpdater() {
		return updater;
	}

	public Translator getTranslator() {
		return translator;
	}

	public ToastNotifier getToastNotifier() {
		return toastNotifier;
	}

	public StatisticManager getStatisticManager() {
		return statisticManager;
	}

	public AxeDetector getAxeDetector() {
		return axeDetector;
	}
}
