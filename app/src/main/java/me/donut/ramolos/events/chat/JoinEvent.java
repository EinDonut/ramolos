package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Settings;
import me.donut.ramolos.Utils;
import me.donut.ramolos.Translator.Language;

public class JoinEvent extends ChatEvent {

	@Override
	public String[] getTranslationKeys() {
		return Ramolos.getInstance().getTranslator().getDetectionKeys();
	}

	@Override
	public boolean hasPrefix() {
		return false;
	}

	@Override
	public boolean getsSent() {
		return false;
	}

	@Override
	public String analyze(Matcher match, int key) {
		Settings settings = Ramolos.getInstance().getSettings();
		if (settings.isAutoDetectLanguage()) settings.setLanguage(Language.values()[key]);
		return Utils.HL_JOIN[0] + match.group(0) + Utils.HL_JOIN[1];
 	}
}
