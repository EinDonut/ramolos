package me.donut.ramolos.connection;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;

public class TournmanetPacket extends Packet {

	@Override
	public int getID() {
		return 4;
	}

	public TournmanetPacket(String[] args) {
		// id running message
		boolean running = Boolean.valueOf(args[1]);
		String message = args[2];
		String color = args.length == 4 ? args[3] : "";

		if (Ramolos.getInstance().getSetup().isRunning() && !running)
			Utils.playSound("doggo");

		Ramolos.getInstance().getSetup().setRunning(running);
		Ramolos.getInstance().getWindow().getConnectionTab().updateServerMessage(message, color);
	}

	public TournmanetPacket(String action, String data) {
		// start stop startnow participate
		send(new String[] { action, data });
	}
}
