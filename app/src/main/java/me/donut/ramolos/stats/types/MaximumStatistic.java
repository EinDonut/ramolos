package me.donut.ramolos.stats.types;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.Statistic;

public class MaximumStatistic extends Statistic {

	private StatisticType target;

	public MaximumStatistic(StatisticType type, StatisticType target) {
		super(type);

		this.target = target;
	}

	@Override
	public int getRawValues(int interval) {
		return values[interval];
	}

	@Override
	public StatisticType[] getDependencies() {
		return new StatisticType[] { target };
	}

	@Override
	public void update(boolean displayOnly, boolean ignoreDependencies) {
		if (!displayOnly) {
			int value = getStats(target, INTERVAL_TODAY);

			values[INTERVAL_TODAY] = Math.max(values[INTERVAL_TODAY], value);
			values[INTERVAL_30_D] = Math.max(values[INTERVAL_30_D], value);
			values[INTERVAL_ALLTIME] = Math.max(values[INTERVAL_ALLTIME], value);
		}

		if (Ramolos.getInstance().getWindow() != null) 
			updateDisplay(getFormattedValues(getSelectedInterval()));
		if (ignoreDependencies) return;
		refreshDependencies();
	}
}
