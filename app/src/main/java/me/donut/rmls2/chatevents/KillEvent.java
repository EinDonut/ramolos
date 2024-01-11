package me.donut.rmls2.chatevents;

import java.util.regex.Matcher;

import me.donut.rmls2.Utils;

public class KillEvent extends ChatEvent {

	private String opponent;
	private boolean nemesis;

	@Override
	public String[] getTranslationKeys() {
		return new String[] {"Du hast (deinen Nemesisgegner \\()?(\\w+)\\)? getötet"};
	}

	@Override
	public boolean hasPrefix() {
		return true;
	}

	@Override
	public String analyze(Matcher match, int key) {
		opponent = match.group(2);
		nemesis = match.group(1) != null;

		int[] indice = new int[] {
			nemesis ? match.start(1) : 0, nemesis ? match.end(1) - 2 : 0,
			match.start(2), match.end(2)
		};

		String[] insertions = new String[] {
			Utils.HL_NEMESIS[0], Utils.HL_NEMESIS[1],
			Utils.HL_NAME[0], Utils.HL_NAME[1]
		};

		return Utils.insertText(getRaw(), indice, insertions);
	}

	public String getOpponent() {
		return opponent;
	}

	public boolean isNemesis() {
		return nemesis;
	}
}
