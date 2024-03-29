package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;
import me.donut.ramolos.stats.Statistic;
import me.donut.ramolos.stats.StatisticManager;
import me.donut.ramolos.stats.Statistic.StatisticType;
import me.donut.ramolos.stats.types.CounterStatistic;

public class ItemEvent extends ChatEvent {

	private Item item;

	@Override
	public String[] getTranslationKeys() {
		return new String[] {
			"item-mine", 
			"item-speed",
			"item-dogs",
			"item-quickfire",
			"item-armor",
			"item-angel",
			"item-nuke"
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
	public String analyze(Matcher match, int key) {
		item = Item.values()[key];

		int[] indice = new int[] {
			match.start(1), match.end(1)
		};

		String[] insertions = new String[] {
			Utils.HL_ITEM[0], Utils.HL_ITEM[1]
		};

		Ramolos.getInstance().getAxeDetector().onItem(item);
		StatisticType statsType = StatisticType.valueOf("ITEM_" + item.name());
		if (statsType != null) {
			StatisticManager sm = Ramolos.getInstance().getStatisticManager();
			Statistic stat = sm.getStatistic(statsType);
			if (stat != null && stat instanceof CounterStatistic)
				((CounterStatistic) stat).increment();
		}


		return Utils.insertText(getRaw(), indice, insertions);
	}

	@Override
	public boolean interruptsAFK() {
		return true;
	}

	public Item getItem() {
		return item;
	}

	public enum Item {

		MINE("Mine", 3),
		SPEED("Schnelligkeit", 5),
		DOGS("Hundestaffel", 10),
		MINIGUN("Minigun", 15),
		ARMOR("Rüstung", 25),
		ANGEL("Engel", 35),
		NUKE("Nuke", 50);

		private String name;
		private int killstreak;

		Item(String name, int killstreak) {
			this.name = name;
			this.killstreak = killstreak;
		}

		public String getName() {
			return name;
		}

		public int getKillstreak() {
			return killstreak;
		}

	}
}
