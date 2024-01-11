package me.donut.ramolos.events;

import me.donut.ramolos.Ramolos;

public class InactiveTimer extends Thread {
	
	private final int AFK_TIME = 60;
	private boolean afk = true;
	private int timer = 0;

	@Override
	public void run() {
		while (true) {
			try { Thread.sleep(1000); } catch (Exception e) {e.printStackTrace();}
			timer += timer > AFK_TIME ? 0 : 1;
			if (timer == AFK_TIME) setAFK(true);
		}
	}

	public void wake() {
		timer = 0;
		setAFK(false);
	}

	public void setAFK(boolean afk) {
		this.afk = afk;
		Ramolos.getInstance().getWindow().getChatTab().setActive(!afk);
	}

	public boolean isAFK() {
		return afk;
	}

}
