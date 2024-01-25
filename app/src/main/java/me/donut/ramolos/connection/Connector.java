package me.donut.ramolos.connection;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.window.ConnectionTab;

public class Connector {
	
	private final String HOST = "ragearrows.de";
	private boolean connected;
	private Socket clientSocket;
	private SocketReader reader;
	private SocketWriter writer;
	private boolean authenticated;
	private boolean adminMode;
	private int packetsSent;
	private int packetsReceived;
	private int port = 0;

	private ConnectionTab ct = Ramolos.getInstance().getWindow().getConnectionTab();

	public Connector() {
		reader = new SocketReader();
		writer = new SocketWriter();
	}

	public void connect() {
		if (!validatePort()) return;
		Ramolos.getInstance().getSettings().setPort(port);
		blockConnectionButton(true);

		try {
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(HOST, port), 1000);
			clientSocket.setKeepAlive(true);
			reader = new SocketReader();
			writer.initStream(clientSocket);
			reader.initStream(clientSocket);
			reader.startReading();
			connected = true;
			ct.updatePortValidity(true);
			ct.updateUserIdValidity(true);
			new AuthenticationChecker().start();
		} catch (Exception ex) {
			ct.updateUserIdValidity(true);
			ct.updatePortValidity(false);
			disconnect();
			connected = false;
		}
		
		blockConnectionButton(false);
	}
	
	public void disconnect() {
		blockConnectionButton(true);

		if (clientSocket != null) {
			try {
				clientSocket.close();
				reader.stopReading();
				writer.close();
				reader = null;
			} catch (Exception ex) { }
			connected = !clientSocket.isClosed();
		}
		setAuthenticated(false);
		setAdminMode(false);
		updateConnectionStatus(connected);
		blockConnectionButton(false);
		updatePacketsSent(-1);
		updatePacketsReceived(-1);
		ct.updateUserName("-");
		ct.updateServerMessage("", "");
	}
	
	private void blockConnectionButton(boolean block) {
		ct.blockConnectButton(block);
	}

	private void updateConnectionStatus(boolean connected) {
		ct.updateConnectionStatus(connected);
	}

	public boolean validatePort() {
		String suggestedPortString = ct.getPortEntry();
		int suggestedPort = 0;
		try {
			suggestedPort = Integer.valueOf(suggestedPortString);
		} catch (NumberFormatException e) {
			suggestedPort = -1;
		}

		if (!suggestedPortString.equals(suggestedPort + "")) {
			suggestedPort = -1;
		}

		if (suggestedPort < 1024 || suggestedPort > 49151) {
			ct.updatePortValidity(false);
			return false;
		}
		port = suggestedPort;
		return true;
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean value) {
		this.authenticated = value;
	}

	public boolean isAdminMode() {
		return adminMode;
	}

	public void setAdminMode(boolean value) {
		adminMode = value;
		Ramolos.getInstance().getWindow().getConnectionTab().setAdminToolsVisible(value);
		if (!adminMode) return;
		new SetupPacket();
	}

	public void write(String message) {
		if (!connected) return;
		writer.write(message);
	}

	public int getPacketsReceived() {
		return packetsReceived;
	}

	public void updatePacketsReceived(int value) {
		packetsReceived += value;
		if (value == -1) packetsReceived = 0;
		ct.updateReceivedPackets(packetsReceived);
	}

	public int getPacketsSent() {
		return packetsSent;
	}

	public void updatePacketsSent(int value) {
		packetsSent += value;
		if (value == -1) packetsSent = 0;
		ct.updateSentPackets(packetsSent);
	}

	private class SocketReader extends Thread {

		private BufferedReader br;

		public void initStream(Socket socket) throws IOException {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}

		public void startReading() {
			start();
		}

		public void stopReading() throws IOException, InterruptedException {
			if (br == null) return;
			br.close();
		}

		public void run() {
			if (br == null) return;
			String inputLine;
			try {
				while ((inputLine = br.readLine()) != null) {
					// System.out.println("> " + inputLine);
					String[] args = inputLine.split(";");
					if (args.length <= 1) continue;
					int packetID = 0;
					try { packetID = Integer.valueOf(args[0]); }
					catch (NumberFormatException ex) { continue; }

					switch (packetID) {
						case 1:
							new LoginPacket(args);
							break;
						case 4:
							new TournmanetPacket(args);
							break;
						case 5:
							new SetupPacket(args);
						default:
							continue;
					}
					updatePacketsReceived(1);
				}
			} catch (IOException ex) { }
		}
	}

	private class SocketWriter {

		private PrintWriter pw;

		public void initStream(Socket socket) throws IOException{
			pw = new PrintWriter(socket.getOutputStream(), true);
		}

		public void write(String message) {
			if (pw == null) return;
			pw.println(message);
		}

		public void close() {
			if (pw == null) return;
			pw.close();
		}
	}

	private class AuthenticationChecker extends Thread {
		@Override
		public void run() {
			try {
				new LoginPacket(Ramolos.getInstance().getWindow().getConnectionTab().getUserIdEntry());
				Thread.sleep(1000);
				if (isAuthenticated()) return;
				ct.updateUserIdValidity(false);
				disconnect();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
