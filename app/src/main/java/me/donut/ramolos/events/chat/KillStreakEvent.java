package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;

public class KillStreakEvent extends ChatEvent {

	private int streak;

	@Override
	public String[] getTranslationKeys() {
		return new String[] {
			"killstreak"
		};
	}

	@Override
	public boolean hasPrefix() {
		return true;
	}
	
	@Override
	public boolean getsSent() {
		return false;
	}

	@Override
	public boolean interruptsAFK() {
		return true;
	}

	@Override
	public String analyze(Matcher match, int key) {
		streak = Integer.valueOf(match.group(1));

		int[] indice = new int[] {
			match.start(1), match.end(1)
		};

		String[] insertions = new String[] {
			Utils.HL_ITEM[0], Utils.HL_ITEM[1]
		};

		Ramolos.getInstance().getAxeDetector().onKillStreakConfirm(streak);

		return Utils.insertText(getRaw(), indice, insertions);
	}
}
