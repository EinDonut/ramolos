package me.donut.ramolos.connection;

import me.donut.ramolos.Ramolos;

public class TerminatePacket extends Packet {

	@Override
	public int getID() {
		return 6;
	}

	public TerminatePacket(String[] args) {
		Ramolos.getInstance().getConnector().disconnect();
	}
}
