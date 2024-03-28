package me.donut.ramolos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

public class Utils {

	public static final String[] HL_NAME = new String[]{ "<b class=\"hl_name\">", "</b>" };
	public static final String[] HL_NEMESIS = new String[]{ "<b class=\"hl_nemesis\">", "</b>" };
	public static final String[] HL_ITEM = new String[]{ "<b class=\"hl_item\">", "</b>" };
	public static final String[] HL_JOIN = new String[]{ "<b class=\"hl_join\">", "</b>" };
	public static final String[] HL_PARTICIPATE = new String[]{ "<b class=\"hl_participate\">", "</b>" };

	public static final String COLOR_PLACEHOLDER = "<html><font color='%s'>%s</font></html>";
	public static final String COLOR_RED = "#bd3c5f";
	public static final String COLOR_GREEN = "#239E62";
	public static final String COLOR_FG = "#BBBBBB";
	public static final String COLOR_HIGHLIGHT = "#568AF2";

	private static Clip clip;
	
	public static ImageIcon getIcon(String path) {
        URL imgURL = Utils.class.getResource(path);
        return imgURL == null ? null : new ImageIcon(imgURL);
    }

	public static <E extends Enum<?>, C extends Class> int getIndexByValue(C type, E instance) {
		if (!type.isEnum()) return -1;
		for (int i = 0; i < type.getEnumConstants().length; i++) {
			Object o = type.getEnumConstants()[i];
			if (o == instance) return i;
		}
		return -1;
	}

	public static String getDateString() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	public static ArrayList<String> readRessource(String path) {
		InputStream inputstream = Utils.class.getResourceAsStream(path);
		ArrayList<String> result = new ArrayList<>();

		if (inputstream == null) return result;
		try {
			InputStreamReader isr = new InputStreamReader(inputstream, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			String s;
			while ((s = br.readLine()) != null)	result.add(s);
			br.close();
			isr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String capitalizeFirst(String input) {
		if (input.length() == 0) return "";
		String[] split = input.toLowerCase().split("");
		split[0] = split[0].toUpperCase();
		return String.join("", split);
	}

	public static boolean fileExists(String path) {
		File f = new File(path);
		return f.exists();
	}

	public static String insertText(String input, int[] indice, String[] insertions) {
		ArrayList<String> parts = new ArrayList<>();
		int last = 0;

		for (int i = 0; i < indice.length; i++) {
			parts.add(input.substring(last, indice[i]));
			parts.add(insertions[i]);
			last = indice[i];
		}

		if (last != input.length() - 1) parts.add(input.substring(last));

		return String.join("", parts);
	}

	public static void playNotificationSound() {
		if (clip != null && clip.isActive()) clip.close();

		URL url;
		int sound = Ramolos.getInstance().getSettings().getNotificationSound();
		float volume = Ramolos.getInstance().getSettings().getNotificationVolume() / 100.0f;
		if (volume < 0.0 || volume > 1.0) return;
		try {
			url = Utils.class.getResource("/sounds/notif" + sound + ".wav").toURI().toURL();
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);  
			clip = AudioSystem.getClip();
			clip.open(audioIn);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
    		float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = (range * volume) + gainControl.getMinimum();
			gainControl.setValue(gain);
			clip.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
