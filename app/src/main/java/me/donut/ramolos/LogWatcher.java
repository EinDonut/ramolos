package me.donut.ramolos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.nio.file.attribute.FileTime;

import java.nio.file.attribute.BasicFileAttributes;

public class LogWatcher extends Thread {

	private int lastFileLines = -1;
	private Path currentFile = null;
	private FileTime lastUpdateTime;
	private FileTime lastCreateTime;

	private boolean paused = false;
	private boolean forcePause = false;
	private boolean stop = false;

	public LogWatcher() {
		start();
	}
	
	public void run() {

		Settings settings = Ramolos.getInstance().getSettings();
		String lastPath = "";
		FileTime updateTime;
		FileTime createTime;

		while(!stop) {
			try {
				if (!settings.isValidPath()) forcePause = true;
				if (paused || forcePause) return;

				if (!lastPath.equals(settings.getPath()) || currentFile == null) {
					currentFile = Paths.get(settings.getPath());
					lastPath = settings.getPath();
				}

				BasicFileAttributes attr = Files.readAttributes(currentFile, BasicFileAttributes.class);
				updateTime = attr.lastModifiedTime();
				createTime = attr.creationTime();

				if (lastCreateTime == null || createTime.compareTo(lastCreateTime) != 0) {
					lastCreateTime = createTime;
					lastFileLines = -1;
				}

				if (lastUpdateTime == null || updateTime.compareTo(lastUpdateTime) != 0) {
					lastUpdateTime = updateTime;
					readChanges();
				}

				Thread.sleep(100);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void readChanges() throws IOException, FileNotFoundException {
		BufferedReader in = new BufferedReader(
			new FileReader(currentFile.toAbsolutePath().toString()));

		String line;
		int lineCount = 0;
		while ((line = in.readLine()) != null) {
			if (lineCount++ < lastFileLines || lastFileLines == -1) continue;
			Ramolos.getInstance().getListener().callChatEvent(line);
		}
		lastFileLines = lineCount;
		in.close();
	}

	public void terminate() {
		stop = true;
	}
}
