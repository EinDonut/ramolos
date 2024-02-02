package me.donut.ramolos;

import java.util.ArrayList;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

public class Utils {

	public static final String[] HL_NAME = new String[]{ "<b class=\"hl_name\">", "</b>" };
	public static final String[] HL_NEMESIS = new String[]{ "<b class=\"hl_nemesis\">", "</b>" };
	public static final String[] HL_ITEM = new String[]{ "<b class=\"hl_item\">", "</b>" };
	public static final String[] HL_JOIN = new String[]{ "<b class=\"hl_join\">", "</b>" };
	public static final String[] HL_PARTICIPATE = new String[]{ "<b class=\"hl_participate\">", "</b>" };

	public static final String COLOR_PLACEHOLDER = "<html><font color='%s'>%s</font></html>";
	public static final String COLOR_RED = "#bd3c5f";
	public static final String COLOR_GREEN = "#239E62";
	
	public static ImageIcon getIcon(String path) {
        URL imgURL = Utils.class.getResource(path);
        return imgURL == null ? null : new ImageIcon(imgURL);
    }

	@SuppressWarnings("rawtypes")
	public static <E extends Enum<?>, C extends Class> int getIndexByValue(C type, E instance) {
		if (!type.isEnum()) return -1;
		for (int i = 0; i < type.getEnumConstants().length; i++) {
			Object o = type.getEnumConstants()[i];
			if (o == instance) return i;
		}
		return -1;
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

	public static void playSound(String file) {
		URL url;
		try {
			url = Utils.class.getResource("/sounds/" + file + ".wav").toURI().toURL();
			System.out.println(url.toString());
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);  
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
