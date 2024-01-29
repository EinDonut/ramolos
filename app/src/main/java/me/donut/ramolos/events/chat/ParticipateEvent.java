package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Utils;
import me.donut.ramolos.connection.TournmanetPacket;

public class ParticipateEvent extends ChatEvent {

	@Override
	public String[] getTranslationKeys() {
		return new String[] { // TODO
			//"Teams sind auf diesem Server VERBOTEN und werden mit einem Ban bestraft!"
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
	public String analyze(Matcher match, int key) {
		new TournmanetPacket("participate");

		return Utils.HL_JOIN[0] + match.group(0) + Utils.HL_JOIN[1];
 	}
}
