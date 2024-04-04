package me.donut.ramolos.connection;

import java.io.File;
import java.io.FileWriter;

import me.donut.ramolos.Ramolos;

public class AdminControlPacket extends Packet {

	@Override
	public int getID() {
		return 7;
	}

	public AdminControlPacket(String[] args) {
		if (args.length >= 4 && args[1].equals("download")) {
			receiveFile(args[2], args[3]);
		}
	}

	public AdminControlPacket(String action) {
		send(new String[] { action });
	}

	private void receiveFile(String fileName, String content) {
		try {
			if (!fileName.contains(".")) {
				Ramolos.getInstance().getWindow().showSimpleInfoDialog("Die Datei " 
					+ fileName + " konnte nicht heruntergeladen werden.", "Herunterladen fehlgeschlagen");
				return;
			}
			content = content.replace("$", System.lineSeparator());

			File file;
			int c = 0;
			String[] split = fileName.split("\\.");
			while((file = new File(fileName)).exists())
				fileName = split[0] + "_" + c++ + "." + split[1];
			
			System.out.println("Downloading file: " + fileName);
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
			System.out.println("File '" + fileName + "' downloaded");
			Ramolos.getInstance().getWindow().showSimpleInfoDialog("Die Datei " 
				+ file.getAbsolutePath() + " wurde erfolgreich heruntergeladen.", "Datei heruntergeladen");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
