package me.donut.ramolos;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class Translator {

	private final String DETECTION_KEY = "teams-not-allowed";
	private final String LANGUAGE_CHANGE_KEY = "language-change";
	private final String TRANSLATION_FILE = "/translations.csv";

	private ArrayList<String> detectionStrings;
	private HashMap<String, String> translations;
	private ArrayList<String> languageChangeMessages;
	
	public Translator() {
		detectionStrings = new ArrayList<>();
		languageChangeMessages = new ArrayList<>();
		translations = new HashMap<>();

		changeLanguage(Ramolos.getInstance().getSettings().getLanguage());
	}

	public String[] getDetectionKeys() {
		return detectionStrings.toArray(new String[detectionStrings.size()]);
	}

	public String[] getLanguageChangeMessages() {
		return languageChangeMessages.toArray(new String[languageChangeMessages.size()]);
	}

	public String translate(String key) {
		return translations.getOrDefault(key, key);
	}

	public void changeLanguage(Language language) {
		ArrayList<String> translationLines = Utils.readRessource(TRANSLATION_FILE);
		if (translationLines.size() < 2) return;
		ArrayList<String> langKeys = new ArrayList<String>(Arrays.asList(translationLines.get(0).split(";")));
		int languageIndex = langKeys.indexOf(language.name());
		for (String line : translationLines) {
			String[] split = line.split(";");
			String translationKey = split[0];
			if (languageIndex >= split.length) continue;
			translations.put(translationKey, split[languageIndex]);

			if (translationKey.equals(DETECTION_KEY)) {
				detectionStrings.clear();
				for (int i = 1; i < split.length && i < langKeys.size(); i++)
					detectionStrings.add(split[i]);
			} else if (translationKey.equals(LANGUAGE_CHANGE_KEY)) {
				languageChangeMessages.clear();
				for (int i = 1; i < split.length && i < langKeys.size(); i++)
					languageChangeMessages.add(split[i]);
			}
		}
	}

	public enum Language {
		GERMAN(),
		ENGLISH(),
		FRENCH(),
		AUSTRIAN(),
		SPANISH();
	}
}
