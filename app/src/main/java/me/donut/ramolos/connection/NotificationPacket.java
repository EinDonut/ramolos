package me.donut.ramolos.connection;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;

public class NotificationPacket extends Packet {

	@Override
	public int getID() {
		return 8;
	}

	public NotificationPacket(String[] args) {
		Utils.playNotificationSound();
		if (args.length != 3) return;
		Ramolos.getInstance().getToastNotifier().sendNotification(args[1], args[2]);
	}
}
