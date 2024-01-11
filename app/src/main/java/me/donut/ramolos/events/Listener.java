package me.donut.ramolos.events;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.events.chat.ChatEvent;
import me.donut.ramolos.events.chat.DeathEvent;
import me.donut.ramolos.events.chat.ItemEvent;
import me.donut.ramolos.events.chat.JoinEvent;
import me.donut.ramolos.events.chat.KillEvent;
import me.donut.ramolos.window.ChatTab;

public class Listener {
	
	private ArrayList<ChatEvent> chatEvents = new ArrayList<>();

	public Listener() {
		registerChatEvent(new KillEvent());
		registerChatEvent(new DeathEvent());
		registerChatEvent(new ItemEvent());
		registerChatEvent(new JoinEvent());
	}

	public void registerChatEvent(ChatEvent event) {
		chatEvents.add(event);
	}

	public void callChatEvent(String text) {

		ChatTab ct = Ramolos.getInstance().getWindow().getChatTab();
		ct.updateLinesRead(1);

		boolean hasPrefix = false;
		String message = "";
		String timeString = "";
		int time = 0;
		
		String[] split = text.split(" ");
		if (split[0].length() != 10) return;
		if (!split[2].equals("thread/INFO]:")) return;
		if (!split[3].equals("[CHAT]")) return;
		hasPrefix = split[4].equals("[RageMode]");
		timeString = split[0].substring(1, split[0].length() - 2);
		String[] timeSplit = timeString.split(":");
		if (timeSplit.length != 3) return;

		text = clearText(text);

		time = Integer.valueOf(timeSplit[0]) * 60 * 60
			 + Integer.valueOf(timeSplit[1]) * 60
			 + Integer.valueOf(timeSplit[2]);
		for (int i = (hasPrefix ? 5 : 4); i < split.length; i++)
			message += split[i] + " ";
		if (message.length() > 0) message = message.substring(0, message.length() - 1);
		
		for (ChatEvent event : chatEvents) {
			if (hasPrefix != event.hasPrefix()) continue;
			for (int i = 0; i < event.getTranslationKeys().length; i++) {
				String regex = event.getTranslationKeys()[i];
				final Pattern pattern = Pattern.compile(regex);
				final Matcher matcher = pattern.matcher(message);

				if (!matcher.find()) continue;
				event.setTime(time);
				event.setRaw(message);
				Ramolos.getInstance().getWindow().getChatTab().appendLine(event.analyze(matcher, i));
				ct.updateLinesEval(1);
				System.out.println(Ramolos.getInstance().getInactiveTimer());
				Ramolos.getInstance().getInactiveTimer().wake();
				return;
			}
		}
		Ramolos.getInstance().getWindow().getChatTab().appendLine(message);
	}

	private String clearText(String input) {
		return input.trim().replaceAll("[^\\w_\\ \\[\\]:\\/!üÜäÄöÖ()]", "").trim();
	}
}
