package me.donut.ramolos.connection;

public class KillPacket extends Packet {

	@Override
	public int getID() {
		return 2;
	}

	public KillPacket(String playerName, boolean nemesis) {
		send(new String[] { playerName, Boolean.toString(nemesis) });
	}
}
