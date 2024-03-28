package me.donut.ramolos.events;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.events.chat.ItemEvent.Item;
import me.donut.ramolos.stats.Statistic;
import me.donut.ramolos.stats.StatisticManager;
import me.donut.ramolos.stats.Statistic.StatisticType;
import me.donut.ramolos.stats.types.CounterStatistic;
import me.donut.ramolos.stats.types.SimpleStatistic;

public class AxeDetector {
	
	private int killStreak = 0;
	private int killStreakStrict = 0;
	private int deathStreakStrict = 0;

	// True wenn man mit 2+ Streak gestorben ist
	private boolean watchingAxe;
	private boolean gotMine;
	private int safe;
	private int killsSinceDeath;
	// Die aktuelle Streak, falls der letzte Tod eine Axt war
	private int altKillStreak;

	public AxeDetector() {
		watchingAxe = false;
		gotMine = false;
		killsSinceDeath = 0;
		altKillStreak = 0;
	}

	public void onKill() {
		killsSinceDeath++;
		killStreak++;
		killStreakStrict++;
		altKillStreak++;
		deathStreakStrict = 0;
		updateStreak();

		// Spieler ist mit 2 oder mehr Punkten gestorben, sollte jetzt 4 Punkte
		// haben, hat aber keine Mine erhalten
		if (killStreak % 50 != 4 || gotMine || !watchingAxe) return;
		watchingAxe = false;
		killStreak = altKillStreak;
		axeKill();
	}

	public void onDeath() {
		killStreakStrict = 0;
		deathStreakStrict++;
		int killStreakMod = killStreak % 50;
		// Spieler ist gestorben, hatte vorher 3 Punkte, 
		// aber hat keine Mine bekommen
		if (killStreakMod == 3 && !gotMine) {
			watchingAxe = false;
			killStreak = altKillStreak;
			axeKill();
		}

		// Gestorben mit 2+ Streak -> aufpassen!
		if (killStreak >= 2 || watchingAxe) {
			watchingAxe = true;
			gotMine = false;
			killsSinceDeath = 0;
			altKillStreak = 0;
		} else watchingAxe = false;

		killStreak /= 2;
		altKillStreak /= 2;
		if (killStreakMod == 3) gotMine = true;
		if (safe > 0) safe = -1;
		if (killStreak == 0) watchingAxe = false;

		updateStreak();
	}

	public void onItem(Item item) {
		if (item.getKillstreak() == 3) {
			gotMine = true;
			// Spieler ist mit 2+ KS gestorben, hat 3 Kills gemacht und bekommt eine Mine
			if (watchingAxe && killsSinceDeath == 3 && safe != 0) {
				axeKill();
				watchingAxe = false;
				killStreak = altKillStreak;
			// Spieler bekommt Mine, aber KS ist nicht bei 3
			} else if (watchingAxe && killStreak % 50 != 3) {
				axeKill();
				watchingAxe = false;
				killStreak = altKillStreak;
			}
			safe = -1;
		}

		watchingAxe = false;
		int oldKillStreak = killStreak;
		// -1 weil die Item Nachricht vor der eigentlich Killnachricht kommt
		killStreak = (killStreak / 50) * 50 + item.getKillstreak() - 1;
		if (oldKillStreak != killStreak) updateStreak();
	}

	public void onKillStreakConfirm(int streak) {
		int oldKillStreak = killStreak;
		killStreak = streak;
		if (killStreak % 50 == 0 && killStreak > 0) safe = 3;
		if (oldKillStreak != killStreak) updateStreak();
	}

	private void axeKill() {
		StatisticManager manager = Ramolos.getInstance().getStatisticManager();
		Statistic stat = manager.getStatistic(StatisticType.AXE_DEATHS);
		if (stat instanceof CounterStatistic) ((CounterStatistic) stat).increment();
	}

	private void updateStreak() {
		getSimpleStatistic(StatisticType.KILLSTREAK).setValue(killStreak);
		getSimpleStatistic(StatisticType.KILLSTREAK_STRICT).setValue(killStreakStrict);
		getSimpleStatistic(StatisticType.DEATHSTREAK_STRICT).setValue(deathStreakStrict);
	}

	private SimpleStatistic getSimpleStatistic(StatisticType type) {
		Statistic stat = Ramolos.getInstance().getStatisticManager().getStatistic(type);
		if (!(stat instanceof SimpleStatistic)) return null;
		return (SimpleStatistic) stat;
	}
}
