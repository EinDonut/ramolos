package me.donut.ramolos.events;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.stats.AdditionalStats;

public class Ticker extends Thread {
	
	private final int AFK_TIME = 60;
	private int generalTimer = 0;
	private int activeTimer = 0;
	private int afkTimer = 0;
	private boolean afk = true;

	@Override
	public void run() {
		while (true) {
			try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }

			afkTimer += afkTimer > AFK_TIME ? 0 : 1;
			if (afkTimer == AFK_TIME) setAFK(true);
			else if (!isAFK() && ++activeTimer >= 60) {
				activeTimer = 0;
				AdditionalStats.onMinute();
			}
			if (++generalTimer >= 15) {
				generalTimer = 0;
				Ramolos.getInstance().getDatabaseConnection().checkTime();
			}
		}
	}

	public void wake() {
		afkTimer = 0;
		setAFK(false);
	}

	public void setAFK(boolean afk) {
		this.afk = afk;
		Ramolos.getInstance().getWindow().getChatTab().setActive(!afk);
		if (afk) Ramolos.getInstance().getDatabaseConnection().writeStats();
	}

	public boolean isAFK() {
		return afk;
	}

}
