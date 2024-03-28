package me.donut.ramolos.stats.types;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.Statistic;

public class RatioStatistic extends Statistic {

	private StatisticType dividend;
	private StatisticType divisor;

	public RatioStatistic(StatisticType type, StatisticType dividend, StatisticType divisor) {
		super(type);

		this.dividend = dividend;
		this.divisor = divisor;
	}

	@Override
	public int getRawValues(int interval) {
		int dividendStats = getStats(dividend, interval);
		int divisorStats = getStats(divisor, interval);
		if (divisorStats == 0) divisorStats = 1;

		return (int) (100 * ((double) dividendStats / (double) divisorStats));
	}

	@Override
	public StatisticType[] getDependencies() {
		return new StatisticType[] { dividend, divisor };
	}

	@Override
	public void update(boolean displayOnly, boolean ignoreDependencies) {
		if (Ramolos.getInstance().getWindow() != null) 
			updateDisplay(getFormattedValues(getSelectedInterval()));
		if (ignoreDependencies) return;
		refreshDependencies();
	}
}
