package me.donut.ramolos;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class ToastNotifier {

	private boolean supported;
	private TrayIcon trayIcon;
	private Image logoImg;

	public ToastNotifier() {
		logoImg = Toolkit.getDefaultToolkit()
			.createImage(getClass().getResource("/images/ramolos_icon.png"));
		if (!Ramolos.getInstance().getSettings().useToastNotification()) return;
		addTrayIcon();
	}

	public boolean isSupported() {
		return supported;
	}

	public void sendNotification(String caption, String text) {
		if (trayIcon == null || !isSupported()) return;
		trayIcon.displayMessage(caption, text, MessageType.NONE);
		
		if (Ramolos.getInstance().getSettings().getNotificationVolume() <= 0) return;
		// Utils.playNotificationSound();
	}

	public void addTrayIcon() {
		if (!(supported = SystemTray.isSupported() && logoImg != null))	return;
		if (trayIcon != null) return;

		SystemTray tray = SystemTray.getSystemTray();
		int trayIconWidth = new TrayIcon(logoImg).getSize().width;
		trayIcon = new TrayIcon(logoImg.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));

		try {
			trayIcon.setImageAutoSize(false);
			tray.add(trayIcon);
		} catch (IllegalArgumentException | AWTException e) {
			e.printStackTrace();
			supported = false;
		}
	}

	public void removeTrayIcon() {
		if (!supported || trayIcon == null) return;
		SystemTray.getSystemTray().remove(trayIcon);
		trayIcon = null;
	}
}
