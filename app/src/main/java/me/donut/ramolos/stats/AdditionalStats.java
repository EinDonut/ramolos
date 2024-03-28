package me.donut.ramolos.stats;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.Statistic.StatisticType;
import me.donut.ramolos.stats.types.CounterStatistic;
import me.donut.ramolos.stats.types.SimpleStatistic;

public class AdditionalStats {
	
	private static StatisticManager sm = Ramolos.getInstance().getStatisticManager();
	private static int killsThisMinute = 0;
	private static int killsThisRound = 0;
	private static int deathsThisMinute = 0;
	private static int deathsThisRound = 0;
	private static int classicPoints = 0;
	private static int classicKsDifference = 0;

	public static void onMinute() {
		update(StatisticType.KILLS_THIS_MINUTE, killsThisMinute = 0);
		update(StatisticType.DEATHS_THIS_MINUTE, deathsThisMinute = 0);
		Statistic stat = sm.getStatistic(StatisticType.PLAYTIME);
		if (stat instanceof CounterStatistic) ((CounterStatistic) stat).increment();
	}

	public static void onRound() {
		update(StatisticType.KILLS_THIS_ROUND, killsThisRound = 0);
		update(StatisticType.DEATHS_THIS_ROUND, deathsThisRound = 0);
		Statistic stat = sm.getStatistic(StatisticType.ROUNDS);
		if (stat instanceof CounterStatistic) ((CounterStatistic) stat).increment();
		classicPoints = 0;
	}

	public static void onKill() {
		update(StatisticType.KILLS_THIS_MINUTE, ++killsThisMinute);
		update(StatisticType.KILLS_THIS_ROUND, ++killsThisRound);
		calcClassicPoints();	
	}

	public static void onDeath() {
		update(StatisticType.DEATHS_THIS_MINUTE, ++deathsThisMinute);
		update(StatisticType.DEATHS_THIS_ROUND, ++deathsThisRound);
		calcClassicPoints();
	}

	private static void update(StatisticType type, int value) {
		StatisticManager sm = Ramolos.getInstance().getStatisticManager();
		Statistic stat = sm.getStatistic(type);
		if (stat instanceof SimpleStatistic) ((SimpleStatistic) stat).setValue(value);
	}

	private static void calcClassicPoints() {
		int ks = sm.getStatistic(StatisticType.KILLSTREAK).getRawValues(Statistic.INTERVAL_TODAY);
		classicPoints = ks - classicKsDifference;
		if (classicPoints < 25) return;
		Statistic stat = sm.getStatistic(StatisticType.CLASSIC_ROUNDS);
		if (stat instanceof CounterStatistic) ((CounterStatistic) stat).increment();
		classicKsDifference = ks;
	}

}
