package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;

public class JoinMeEvent extends ChatEvent {

	@Override
	public String[] getTranslationKeys() {
		return new String[] { "joinme" };
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
		String sender = match.group(1);

		int[] indice = new int[] {
			match.start(1), match.end(1)
		};

		String[] insertions = new String[] {
			Utils.HL_PARTICIPATE[0], Utils.HL_PARTICIPATE[1]
		};

		Ramolos.getInstance().getToastNotifier().sendNotification("JoinMe", 
			sender + " läd zum RageModeFFA Spielen ein!");

		return Utils.insertText(getRaw(), indice, insertions);
	}
}
