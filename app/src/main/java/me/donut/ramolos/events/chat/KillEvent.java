package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import java.util.Arrays;
import me.donut.ramolos.Utils;
import me.donut.ramolos.connection.KillPacket;

public class KillEvent extends ChatEvent {

	private String opponent;
	private boolean nemesis;

	@Override
	public String[] getTranslationKeys() {
		return new String[] {"killed"};
	}

	@Override
	public boolean hasPrefix() {
		return true;
	}
	
	@Override
	public boolean getsSent() {
		return true;
	}

	@Override
	public String analyze(Matcher match, int key) {
		opponent = match.group(2);
		nemesis = match.group(1) != null;

		new KillPacket(opponent, nemesis);

		int[] indice = new int[] {
			nemesis ? match.start(1) : 0, nemesis ? match.end(1) - 2 : 0,
			match.start(2), match.end(2)
		};

		String[] insertions = new String[] {
			Utils.HL_NEMESIS[0], Utils.HL_NEMESIS[1],
			Utils.HL_NAME[0], Utils.HL_NAME[1]
		};
		System.out.println(match);
		System.out.println(getRaw());
		System.out.println(Arrays.toString(indice));
		System.out.println(Arrays.toString(insertions));
		return Utils.insertText(getRaw(), indice, insertions);
	}

	public String getOpponent() {
		return opponent;
	}

	public boolean isNemesis() {
		return nemesis;
	}
}
