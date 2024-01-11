package me.donut.ramolos.chatevents;

import java.util.regex.Matcher;

public abstract class ChatEvent {

	private int time;
	private String raw;

	abstract String[] getTranslationKeys();
	abstract boolean hasPrefix();
	abstract String analyze(Matcher match, int key);

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
