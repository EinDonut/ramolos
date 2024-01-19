package me.donut.ramolos.connection;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.window.ConnectionTab;

public class LoginPacket extends Packet {

	@Override
	public int getID() {
		return 1;
	}

	public LoginPacket(String[] args) {
		// id authenticated name
		if (args.length != 3) return;
		ConnectionTab ct = Ramolos.getInstance().getWindow().getConnectionTab();
		boolean loggedIn = Boolean.valueOf(args[1]);
		Ramolos.getInstance().getConnector().setAuthenticated(loggedIn);
		if (!loggedIn) return;
		ct.updateConnectionStatus(true);
		ct.updateUserName(args[2]);
	}

	public LoginPacket(String userID) {
		send(new String[] { userID });
	}
}
