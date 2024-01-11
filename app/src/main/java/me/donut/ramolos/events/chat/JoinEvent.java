package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Utils;

public class JoinEvent extends ChatEvent {

	@Override
	public String[] getTranslationKeys() {
		return new String[] {
			"Teams sind auf diesem Server VERBOTEN und werden mit einem Ban bestraft!", 
		};
	}

	@Override
	public boolean hasPrefix() {
		return false;
	}

	@Override
	public String analyze(Matcher match, int key) {
		return Utils.HL_JOIN[0] + match.group(0) + Utils.HL_JOIN[1];
 	}
}
