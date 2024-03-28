package me.donut.ramolos.stats.types;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.Statistic;

public class AverageStatistic extends Statistic {

	private StatisticType trigger;
	private StatisticType target;

	public AverageStatistic(StatisticType type, StatisticType trigger, StatisticType target) {
		super(type);

		this.trigger = trigger;
		this.target = target;
	}

	@Override
	public int getRawValues(int interval) {
		int x = getStats(trigger, interval);
		if (x == 0) x = 1;
		return (int) (100 * ((double) values[interval] / (double) x));
	}

	@Override
	public StatisticType[] getDependencies() {
		return new StatisticType[] { trigger };
	}

	@Override
	public void update(boolean displayOnly, boolean ignoreDependencies) {
		if (!displayOnly) {
			System.out.println(values[INTERVAL_TODAY] + " " + getStats(target, INTERVAL_TODAY));
			values[INTERVAL_TODAY] += getStats(target, INTERVAL_TODAY);
			values[INTERVAL_30_D] += getStats(target, INTERVAL_30_D);
			values[INTERVAL_ALLTIME] += getStats(target, INTERVAL_ALLTIME);
		}

		if (Ramolos.getInstance().getWindow() != null) 
			updateDisplay(getFormattedValues(getSelectedInterval()));
		if (ignoreDependencies) return;
		refreshDependencies();
	}
	
}
