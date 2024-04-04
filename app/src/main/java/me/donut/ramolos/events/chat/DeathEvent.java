package me.donut.ramolos.events.chat;

import java.util.regex.Matcher;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;
import me.donut.ramolos.connection.DeathPacket;
import me.donut.ramolos.stats.AdditionalStats;
import me.donut.ramolos.stats.Statistic.StatisticType;
import me.donut.ramolos.stats.types.CounterStatistic;

public class DeathEvent extends ChatEvent {

	private String opponent;
	private boolean self;

	@Override
	public String[] getTranslationKeys() {
		return new String[] { "killed-by", "player-died" };
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
		opponent = self ? "" : match.group(1);

		new DeathPacket(opponent, self);
		((CounterStatistic) statsManager.getStatistic(StatisticType.DEATHS)).increment();

		int[] indice = new int[] {
			match.start(1), match.end(1)
		};

		String[] insertions = new String[] {
			Utils.HL_NAME[0], Utils.HL_NAME[1]
		};

		Ramolos.getInstance().getAxeDetector().onDeath();
		Ramolos.getInstance().getPlayerStats().incrementStats(opponent, 0, 1);
		AdditionalStats.onDeath();

		return Utils.insertText(getRaw(), indice, insertions);
	}

	public String getOpponent() {
		return opponent;
	}

	public boolean isSelf() {
		return self;
	}

	@Override
	public boolean interruptsAFK() {
		return true;
	}
}
