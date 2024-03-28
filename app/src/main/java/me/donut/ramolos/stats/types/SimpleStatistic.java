package me.donut.ramolos.stats.types;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.Statistic;

public class SimpleStatistic extends Statistic {

	public SimpleStatistic(StatisticType type) {
		super(type);
	}

	@Override
	public int getRawValues(int interval) {
		return values[interval];
	}

	public void setValue(int value) {
		for (int i = 0; i < 3; i++) values[i] = value;
		update(false, false);
	}

	@Override
	public StatisticType[] getDependencies() {
		return null;
	}

	@Override
	public void update(boolean displayOnly, boolean ignoreDependencies) {
		if (Ramolos.getInstance().getWindow() != null) 
			updateDisplay(getFormattedValues(getSelectedInterval()));
		if (ignoreDependencies) return;
		refreshDependencies();
	}
	
}
