package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.StatisticManager;

public abstract class ChatEvent {

	private int time;
	private String raw;
	protected StatisticManager statsManager = Ramolos.getInstance().getStatisticManager();

	abstract public String[] getTranslationKeys();
	abstract public boolean hasPrefix();
	abstract public String analyze(Matcher match, int key);
	abstract public boolean getsSent();
	abstract public boolean interruptsAFK();

	public void setTime(int time) {
		this.time = time;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public int getTime() {
		return time;
	}

	public String getRaw() {
		return raw;
	}
}
