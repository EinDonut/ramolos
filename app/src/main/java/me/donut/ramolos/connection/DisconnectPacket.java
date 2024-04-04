package me.donut.ramolos.connection;

import me.donut.ramolos.Ramolos;

public class DisconnectPacket extends Packet {

	@Override
	public int getID() {
		return 9;
	}

	public DisconnectPacket(String[] args) {
		Ramolos.getInstance().getConnector().disconnect();
	}
}
