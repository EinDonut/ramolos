package me.donut.ramolos;

import javafx.scene.chart.PieChart.Data;
import me.donut.ramolos.connection.Connector;
import me.donut.ramolos.events.InactiveTimer;
import me.donut.ramolos.events.Listener;
import me.donut.ramolos.stats.DatabaseConnection;
import me.donut.ramolos.window.Window;

public class Ramolos {
	
	private static Ramolos instance;
	public static String VERSION = "";
	public static String BUILD_TIME = "";
	private Window window;
	private Settings settings;
	private LogWatcher logWatcher;
	private Listener listener;
	private InactiveTimer afkTimer;
	private Connector connector;

	public Ramolos() {
		instance = this;
		settings = new Settings();
		window = new Window();
		logWatcher = new LogWatcher();
		listener = new Listener();
		afkTimer = new InactiveTimer();
		connector = new Connector();
		
		afkTimer.start();
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
}
