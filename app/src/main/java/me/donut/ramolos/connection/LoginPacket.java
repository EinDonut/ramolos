package me.donut.ramolos.connection;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.window.ConnectionTab;

public class LoginPacket extends Packet {

	@Override
	public int getID() {
		return 1;
	}

	public LoginPacket(String[] args) {
		if (args.length != 4) return;
		ConnectionTab ct = Ramolos.getInstance().getWindow().getConnectionTab();
		boolean loggedIn = Boolean.valueOf(args[1]);
		boolean isAdmin = Boolean.valueOf(args[3]);
		Ramolos.getInstance().getConnector().setAuthenticated(loggedIn);
		if (!loggedIn) return;
		Ramolos.getInstance().getSettings().setUserID(Ramolos.getInstance().getWindow().getConnectionTab().getUserIdEntry());
		ct.updateConnectionStatus(true);
		ct.updateUserName(args[2]);
		Ramolos.getInstance().getConnector().setAdminMode(isAdmin);
	}

	public LoginPacket(String userID) {
		send(new String[] { userID });
	}
}
