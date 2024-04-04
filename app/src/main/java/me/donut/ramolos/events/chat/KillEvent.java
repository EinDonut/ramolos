package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;
import me.donut.ramolos.connection.KillPacket;
import me.donut.ramolos.stats.AdditionalStats;
import me.donut.ramolos.stats.Statistic.StatisticType;
import me.donut.ramolos.stats.types.CounterStatistic;

public class KillEvent extends ChatEvent {

	private String opponent;
	private boolean nemesis;

	@Override
	public String[] getTranslationKeys() {
		return new String[] { "killed" };
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
	public boolean interruptsAFK() {
		return true;
	}

	@Override
	public String analyze(Matcher match, int key) {
		opponent = match.group(2);
		nemesis = match.group(1) != null;

		new KillPacket(opponent, nemesis);
		((CounterStatistic) statsManager.getStatistic(StatisticType.KILLS)).increment();
		if (nemesis) ((CounterStatistic) statsManager.getStatistic(StatisticType.NEMESIS)).increment();

		int[] indice = new int[] {
			nemesis ? match.start(1) : 0, nemesis ? match.end(1) - 2 : 0,
			match.start(2), match.end(2)
		};

		String[] insertions = new String[] {
			Utils.HL_NEMESIS[0], Utils.HL_NEMESIS[1],
			Utils.HL_NAME[0], Utils.HL_NAME[1]
		};
		
		Ramolos.getInstance().getAxeDetector().onKill();
		Ramolos.getInstance().getPlayerStats().incrementStats(opponent, 1, 0);
		AdditionalStats.onKill();

		return Utils.insertText(getRaw(), indice, insertions);
	}

	public String getOpponent() {
		return opponent;
	}

	public boolean isNemesis() {
		return nemesis;
	}
}
