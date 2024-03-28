package me.donut.ramolos.stats;

import java.text.DecimalFormat;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;

public abstract class Statistic {

	protected int[] values = new int[3];
	private int index = -1; 
	private StatisticType type;
	private boolean hidden;
	private boolean highlighted;
	private int filteredIndex;
	private int hiddenIndex;
	private StatisticType[] dependents;
	protected DecimalFormat defaultFormat = new DecimalFormat("#.##");

	public static final int INTERVAL_ALLTIME = 0;
	public static final int INTERVAL_30_D = 1;
	public static final int INTERVAL_TODAY = 2;
	public static final int HIDDEN = 3;

	public Statistic(StatisticType type) {
		this.type = type;
		index = StatisticManager.orderCounter++;
		filteredIndex = index;
	}
	
	public StatisticType getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public int getFilteredIndex() {
		return filteredIndex;
	}

	public void setFilteredIndex(int filteredIndex) {
		this.filteredIndex = filteredIndex;
	}

	public int getHiddenIndex() {
		return hiddenIndex;
	}

	public void setHiddenIndex(int hiddenIndex) {
		this.hiddenIndex = hiddenIndex;
	}

	public void setRawValues(int[] values) {
		this.values = values;
	}

	public void setRawValue(int interval, int value) {
		values[interval] = value;
	}

	public void addRawValue(int interval, int value) {
		values[interval] += value;
	}

	protected int getStats(StatisticType type, int interval) {
		return Ramolos.getInstance().getStatisticManager().getStatistic(type).getRawValues(interval);
	}

	protected void updateDisplay(String value) {
		Ramolos.getInstance().getWindow().getStatsTab().updateTableCell(index, getName(), value);
	}

	protected int getSelectedInterval() {
		return Ramolos.getInstance().getWindow().getStatsTab().getSelectedInterval();
	}

	protected String getStringFormat(int value) {
		if (getType().isFloatingPoint()) return defaultFormat.format(value / 100.0d);
		else return String.valueOf(value);
	}

	protected void refreshDependencies() {
		if (dependents == null) return;
		for (StatisticType type : dependents) {
			Ramolos.getInstance().getStatisticManager().getStatistic(type).update(false, false);
		}
	}

	public void setDependents(StatisticType[] dependents) {
		this.dependents = dependents;
	}

	public String getName() {
		return String.format("<html><span style='color: %s'>%s%s</span></html>",
			highlighted ? Utils.COLOR_HIGHLIGHT : Utils.COLOR_FG, 
			hidden ? "‎" : "",
			getType().getName()
		);
	}

	public void toggleHighlight() {
		highlighted = !highlighted;
		update(true, true);
	}

	public boolean isHighlighted() {
		return highlighted;
	}
	
	public void toggleVisibility() {
		hidden = !hidden;
		StatisticManager sm = Ramolos.getInstance().getStatisticManager();
		if (sm != null) sm.assignFilteredIndice();
		update(true, true);
	}

	public boolean isVisible() {
		return !hidden;
	}

	public String getFormattedValues(int interval) {
		String diff = "";
		if (getType().isComparable() && interval != INTERVAL_ALLTIME) {
			int difference = getRawValues(interval) - getRawValues(INTERVAL_ALLTIME);
			String sign = difference == 0 ? "±" : (difference > 0 ? "+" : "-");
			String color = difference == 0 ? "#7A7A7A" : 
				(difference > 0 ? Utils.COLOR_GREEN : Utils.COLOR_RED);
			difference = Math.abs(difference);
			diff = String.format("<span style='color: %s'> (%s %s%s)</span>",
				color, sign, getStringFormat(difference), getType().getSuffix());
		}
		
		return "<html>" + getStringFormat(getRawValues(interval)) 
				+ getType().getSuffix() +  diff + "</html>";
	}

	public abstract int getRawValues(int interval);
	public abstract StatisticType[] getDependencies();
	public abstract void update(boolean displayOnly, boolean ignoreDependencies);
	
	public enum StatisticType {
		KILLS("Kills", "", false, false, false),
		DEATHS("Tode", "", false, false, false),
		NEMESIS("Nemesiskills", "", false, false, false),
		AXE_DEATHS("Axttode", "", false, false, true),
		AXE_DEATHS_RATIO("Anteil Axttode", "%", true, true, true),
		KD("K/D", "", true, true, false),
		KILLSTREAK("Killstreak", "", false, false, true),
		KILLSTREAK_STRICT("Killstreak (streng)", "", false, true, false),
		DEATHSTREAK_STRICT("Todesstreak (streng)", "", false, true, false),
		KILLSTREAK_MAX("Höchste Killstreak", "", false, true, true),
		KILLSTREAK_STRICT_MAX("Höchste Killstreak (streng)", "", false, true, false),
		DEATHSTREAK_MAX("Höchste Todesstreak (streng)", "", false, true, false),
		PLAYTIME("Spielzeit", "min", false, false, false),
		ROUNDS("Runden betreten", "", false, false, false),
		PLAYTIME_PER_ROUND("Spielzeit pro Runde", "min", true, true, false),
		KILLS_THIS_ROUND("Kills diese Runde", "", false, false, false),
		KILLS_THIS_MINUTE("Kills diese Minute", "", false, false, false),
		DEATHS_THIS_ROUND("Tode diese Runde", "", false, false, false),
		DEATHS_THIS_MINUTE("Tode diese Minute", "", false, false, false),
		CLASSIC_ROUNDS("Classic Runden Gewonnen", "", false, false, true),
		KILLS_PER_ROUND("Kills pro Runde", "", true, true, false),
		KILLS_PER_MINUTE("Kills pro Minute", "", true, true, false),
		DEATHS_PER_ROUND("Tode pro Runde", "", true, true, false),
		DEATHS_PER_MINUTE("Tode pro Minute", "", true, true, false),
		KILLS_THIS_MINUTE_MAX("Höchste Kills pro Minute", "", false, true, false),
		DEATHS_THIS_MINUTE_MAX("Höchste Tode pro Minute", "", false, true, false),
		KS_ON_DEATH("Ø Killstreak beim Tod", "", true, true, true),
		ITEM_MINE("Item erhalten: Mine", "", false, false, false),
		ITEM_SPEED("Item erhalten: Schnelligkeit", "", false, false, false),
		ITEM_DOGS("Item erhalten: Hundestaffel", "", false, false, false),
		ITEM_MINIGUN("Item erhalten: Minigun", "", false, false, false),
		ITEM_ARMOR("Item erhalten: Rüstung", "", false, false, false),
		ITEM_ANGEL("Item erhalten: Engel", "", false, false, false),
		ITEM_NUKE("Item erhalten: Nuke", "", false, false, false)
		;
		
		private String name;
		private String suffix;
		private boolean floating;
		private boolean comparable;
		private boolean axeInfluence;
		
		private StatisticType(String name, String suffix, boolean floating, boolean comparable, boolean axeInfluence) {
			this.name = name;
			this.suffix = suffix;
			this.floating = floating;
			this.comparable = comparable;
			this.axeInfluence = axeInfluence;
		}
		
		public String getName() {
			return name;
		}

		public String getSuffix() {
			return suffix;
		}

		public boolean isFloatingPoint() {
			return floating;
		}

		public boolean isComparable() {
			return comparable;
		}

		public boolean isAxeInfluenced() {
			return axeInfluence;
		}
	}
}
