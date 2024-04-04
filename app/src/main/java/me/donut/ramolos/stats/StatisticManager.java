package me.donut.ramolos.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.Statistic.StatisticType;
import static me.donut.ramolos.stats.Statistic.StatisticType.*;
import me.donut.ramolos.stats.types.*;

public class StatisticManager {
	
	private LinkedHashMap<StatisticType, Statistic> stats;
	public static int orderCounter = 0;
	private final String STATS_FILTER = "stats.filter";
	// NICHT VERÄNDERN
	public static final List<StatisticType> defaultStats = Arrays.asList(
		KILLS, DEATHS, KD, NEMESIS, AXE_DEATHS, AXE_DEATHS_RATIO, KILLSTREAK,
		KILLSTREAK_STRICT, KILLSTREAK_MAX, KILLS_THIS_ROUND, KILLS_PER_ROUND,
		KILLS_THIS_MINUTE, KILLS_PER_MINUTE, KILLS_THIS_MINUTE_MAX,
		DEATHSTREAK_STRICT, DEATHSTREAK_MAX, DEATHS_THIS_ROUND,
		DEATHS_PER_ROUND, DEATHS_THIS_MINUTE, DEATHS_PER_MINUTE,
		DEATHS_THIS_MINUTE_MAX, KS_ON_DEATH, PLAYTIME, ROUNDS,
		PLAYTIME_PER_ROUND, CLASSIC_ROUNDS, ITEM_MINE, ITEM_SPEED, ITEM_DOGS,
		ITEM_MINIGUN, ITEM_ARMOR, ITEM_ANGEL, ITEM_NUKE, KILLSTREAK_STRICT_MAX
	);

	public StatisticManager() {
		stats = new LinkedHashMap<>();
		
		createStats();
		readStatsFilter();
		assignFilteredIndice();
	}

	private void createStats() {
		stats.clear();
		orderCounter = 0;

		stats.put(KILLS, new CounterStatistic(KILLS));
		stats.put(DEATHS, new CounterStatistic(DEATHS));
		stats.put(KD, new RatioStatistic(KD, KILLS, DEATHS, false));
		stats.put(NEMESIS, new CounterStatistic(NEMESIS));
		stats.put(AXE_DEATHS, new CounterStatistic(AXE_DEATHS));
		stats.put(AXE_DEATHS_RATIO, new RatioStatistic(AXE_DEATHS_RATIO, AXE_DEATHS, DEATHS, true));
		stats.put(KILLSTREAK, new SimpleStatistic(KILLSTREAK));
		stats.put(KILLSTREAK_MAX, new MaximumStatistic(KILLSTREAK_MAX, KILLSTREAK));
		stats.put(KILLSTREAK_STRICT, new SimpleStatistic(KILLSTREAK_STRICT));
		stats.put(KILLSTREAK_STRICT_MAX, new MaximumStatistic(KILLSTREAK_STRICT_MAX, KILLSTREAK_STRICT));
		stats.put(KILLS_THIS_ROUND, new SimpleStatistic(KILLS_THIS_ROUND));
		stats.put(KILLS_PER_ROUND, new RatioStatistic(KILLS_PER_ROUND, KILLS, ROUNDS, false));
		stats.put(KILLS_THIS_MINUTE, new SimpleStatistic(KILLS_THIS_MINUTE));
		stats.put(KILLS_PER_MINUTE, new RatioStatistic(KILLS_PER_MINUTE, KILLS, PLAYTIME, false));
		stats.put(KILLS_THIS_MINUTE_MAX, new MaximumStatistic(KILLS_THIS_MINUTE_MAX, KILLS_THIS_MINUTE));
		stats.put(DEATHSTREAK_STRICT, new SimpleStatistic(DEATHSTREAK_STRICT));
		stats.put(DEATHSTREAK_MAX, new MaximumStatistic(DEATHSTREAK_MAX, DEATHSTREAK_STRICT));
		stats.put(DEATHS_THIS_ROUND, new SimpleStatistic(DEATHS_THIS_ROUND));
		stats.put(DEATHS_PER_ROUND, new RatioStatistic(DEATHS_PER_ROUND, DEATHS, ROUNDS, false));
		stats.put(DEATHS_THIS_MINUTE, new SimpleStatistic(DEATHS_THIS_MINUTE));
		stats.put(DEATHS_PER_MINUTE, new RatioStatistic(DEATHS_PER_MINUTE, DEATHS, PLAYTIME, false));
		stats.put(DEATHS_THIS_MINUTE_MAX, new MaximumStatistic(DEATHS_THIS_MINUTE_MAX, DEATHS_THIS_MINUTE));
		stats.put(KS_ON_DEATH, new AverageStatistic(KS_ON_DEATH, DEATHS, KILLSTREAK));
		stats.put(PLAYTIME, new CounterStatistic(PLAYTIME));
		stats.put(ROUNDS, new CounterStatistic(ROUNDS));
		stats.put(PLAYTIME_PER_ROUND, new RatioStatistic(PLAYTIME_PER_ROUND, PLAYTIME, ROUNDS, false));
		stats.put(CLASSIC_ROUNDS, new CounterStatistic(CLASSIC_ROUNDS));
		stats.put(ITEM_MINE, new CounterStatistic(ITEM_MINE));
		stats.put(ITEM_SPEED, new CounterStatistic(ITEM_SPEED));
		stats.put(ITEM_DOGS, new CounterStatistic(ITEM_DOGS));
		stats.put(ITEM_MINIGUN, new CounterStatistic(ITEM_MINIGUN));
		stats.put(ITEM_ARMOR, new CounterStatistic(ITEM_ARMOR));
		stats.put(ITEM_ANGEL, new CounterStatistic(ITEM_ANGEL));
		stats.put(ITEM_NUKE, new CounterStatistic(ITEM_NUKE));

		setDependents();
	}

	private void setDependents() {
		for (Statistic parent : getStatistics().values()) {
			ArrayList<StatisticType> dependents = new ArrayList<>();
			for (Statistic child : getStatistics().values()) {
				if (child.getType() == parent.getType()) continue;
				if (child.getDependencies() == null) continue;
				if (!Arrays.asList(child.getDependencies()).contains(parent.getType())) continue;
				dependents.add(child.getType());
			}
			parent.setDependents(dependents.toArray(new StatisticType[dependents.size()]));
		}
	}

	public Statistic getStatistic(StatisticType type) {
		return stats.getOrDefault(type, null);
	}
	
	public LinkedHashMap<StatisticType, Statistic> getStatistics() {
		return stats;
	}

	public void assignFilteredIndice() {
		int f = 0, h = 0;
		for (Statistic s : stats.values()) {
			s.setFilteredIndex(s.isVisible() ? f++ : -1);
			s.setHiddenIndex(!s.isVisible() ? h++ : -1);
		}
	}

	public Statistic getStatisticByFilteredIndex(int index, boolean hidden) {
		for (Statistic s : stats.values()) {
			if (!hidden && s.getFilteredIndex() == index) return s;
			if (hidden && s.getHiddenIndex() == index) return s;
		}
		return null;
	}

	public void saveStatsFiltering() {
		String result = "";
		// NOTHING = 0, HIGHLIGHT = 1, HIDDEN = 2, BOTH = 3
		for (Statistic stat : Ramolos.getInstance().getStatisticManager().getStatistics().values()) {
			int value = 0;
			if (stat.isHighlighted()) value += 1;
			if (!stat.isVisible()) value += 2;
			result += value;
		}
		Ramolos.getInstance().getSettings().getPreferences().put(STATS_FILTER, result);
	}

	public void readStatsFilter() {
		Statistic[] stats = getStatistics().values().toArray(new Statistic[getStatistics().size()]);
		String input = Ramolos.getInstance().getSettings().getPreferences().get(STATS_FILTER, "");
		// If there are new stats, reset filter
		if (input.length() != stats.length) return;
		char[] letters = input.toCharArray();
		for (int i = 0; i < letters.length; i++) {
			char c = letters[i];
			if (c == '2' || c == '3') stats[i].toggleVisibility();
			if (c == '1' || c == '3') stats[i].toggleHighlight();
		}
	}
}