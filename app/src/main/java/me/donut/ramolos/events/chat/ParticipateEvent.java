package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Utils;
import me.donut.ramolos.connection.TournmanetPacket;

public class ParticipateEvent extends ChatEvent {

	private String sender;

	@Override
	public String[] getTranslationKeys() {
		return new String[] {
			"\\W*(\\w+): ((?i)go!)\\W*"
		};
	}

	@Override
	public boolean hasPrefix() {
		return false;
	}

	@Override
	public boolean getsSent() {
		return true;
	}

	@Override
	public boolean interruptsAFK() {
		return false;
	}

	@Override
	public String analyze(Matcher match, int key) {
		sender = match.group(1);
		new TournmanetPacket("participate", sender);

		int[] indice = new int[] {
			match.start(2), match.end(2)
		};

		String[] insertions = new String[] {
			Utils.HL_PARTICIPATE[0], Utils.HL_PARTICIPATE[1]
		};

		return Utils.insertText(getRaw(), indice, insertions);
 	}
}
