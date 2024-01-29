package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

public abstract class ChatEvent {

	private int time;
	private String raw;

	abstract public String[] getTranslationKeys();
	abstract public boolean hasPrefix();
	abstract public String analyze(Matcher match, int key);
	abstract public boolean getsSent();

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
