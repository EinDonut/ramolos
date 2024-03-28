package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Settings;
import me.donut.ramolos.Utils;
import me.donut.ramolos.Translator.Language;

public class LanguageEvent extends ChatEvent {

	@Override
	public String[] getTranslationKeys() {
		return Ramolos.getInstance().getTranslator().getLanguageChangeMessages();
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
	public boolean interruptsAFK() {
		return false;
	}

	@Override
	public String analyze(Matcher match, int key) {
		int[] indice = new int[] {
			match.start(1), match.end(1)
		};

		String[] insertions = new String[] {
			Utils.HL_JOIN[0], Utils.HL_JOIN[1]
		};

		Settings settings = Ramolos.getInstance().getSettings();
		if (settings.isAutoDetectLanguage()) settings.setLanguage(Language.values()[key]);
		return Utils.insertText(getRaw(), indice, insertions);
	}
}
