package me.donut.ramolos.connection;

import me.donut.ramolos.Ramolos;

public abstract class Packet {
	
	public abstract int getID();

	protected void send(String[] message) {
		System.out.println("< " + getID() + ";" + String.join(";", message));
		Ramolos.getInstance().getConnector().write(getID() + ";" + String.join(";", message));
		Ramolos.getInstance().getConnector().updatePacketsSent(1);
	}
}
