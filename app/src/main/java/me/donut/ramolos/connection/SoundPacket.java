package me.donut.ramolos.connection;

import me.donut.ramolos.Utils;

public class SoundPacket extends Packet {

	@Override
	public int getID() {
		return 8;
	}

	public SoundPacket(String[] args) {
		Utils.playNotificationSound();
	}
}
