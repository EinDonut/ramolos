package me.donut.ramolos.connection;

public class DeathPacket extends Packet {

	@Override
	public int getID() {
		return 3;
	}

	public DeathPacket(String playerName, boolean self) {
		send(new String[] { playerName, Boolean.toString(self) });
	}
}
