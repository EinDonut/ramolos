package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Utils;
import me.donut.ramolos.connection.DeathPacket;

public class DeathEvent extends ChatEvent {

	private String opponent;
	private boolean self;

	@Override
	public String[] getTranslationKeys() {
		return new String[] { "Du wurdest von \\W*(\\w+) getötet.*", "Du bist (gestorben)" };
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
		self = key == 1;
		opponent = self ? "" : match.group(0);

		new DeathPacket(opponent, self);

		int[] indice = new int[] {
			match.start(1), match.end(1)
		};

		String[] insertions = new String[] {
			Utils.HL_NAME[0], Utils.HL_NAME[1]
		};

		return Utils.insertText(getRaw(), indice, insertions);
	}

	public String getOpponent() {
		return opponent;
	}

	public boolean isSelf() {
		return self;
	}
}
