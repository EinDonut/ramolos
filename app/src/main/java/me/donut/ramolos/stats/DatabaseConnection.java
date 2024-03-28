package me.donut.ramolos.stats;

import java.io.File;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;
import me.donut.ramolos.stats.Statistic.StatisticType;

public class DatabaseConnection {
	
	private final String PREF_DB_COUNT = "db.count";
	private final String PREF_DB_PATH = "db.path";
	private final String PREF_DB_DATE = "db.date";
	private final String PREF_DB_PASSWORD = "db.password";
	private final String FILE_NAME = "ramolos.db";
	private final SimpleDateFormat df = new SimpleDateFormat ("YYYY-MM-dd HH:mm");

	private boolean dailyValuesLoaded;
	private boolean alltimeValuesLoaded;
	private String dailyDate = Utils.getDateString();
	private Connection conn;
	private String filePath;
	private String dbDate;
	private int dbCount;
	private Preferences prefs = Ramolos.getInstance().getSettings().getPreferences();

	public DatabaseConnection() {
		connect();
	}

	public void connect() {
		Date today = new Date();
		String password = prefs.get(PREF_DB_PASSWORD, "");
		filePath = prefs.get(PREF_DB_PATH, createPath());
		dbDate = prefs.get(PREF_DB_DATE, "");
		dbCount = prefs.getInt(PREF_DB_COUNT, 0);

		if (!dbFileExists()) {
			password = generatePassword();
			prefs.put(PREF_DB_DATE, (dbDate = df.format(today)));
			prefs.putInt(PREF_DB_COUNT, ++dbCount);
		}

		Ramolos.getInstance().getWindow().getStatsTab().updateFooter(dbDate, dbCount);

        try {
            String url = "jdbc:sqlite:" + filePath;
            conn = DriverManager.getConnection(url, "", password);
			setupTables();
			readStats();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

	public void disconnect() {
		if (conn == null) return;
		try { conn.close(); }
		catch (SQLException ex) { ex.printStackTrace(); }
	}

	private String createPath() {
		return Ramolos.getInstance().getSettings().getMcDir() + File.separator + FILE_NAME;
	}
	
	private boolean dbFileExists() {
		File f = new File(filePath);
		return f.exists();
	}

	private String generatePassword() {
		SecureRandom random = new SecureRandom();
		String password = new String(random.generateSeed(16));
		prefs.put(PREF_DB_PASSWORD, password);
		return password;
	}

	private void setupTables() {
		Statement stmt = null;
		ResultSet rs = null;
		boolean tablesExists = false;
		ArrayList<StatisticType> existingTypes = new ArrayList<>();

		// Try to read existing tables
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("PRAGMA table_info(stats_alltime);");
			if (rs.next()) tablesExists = true;
			rs.close();
		} catch (SQLException ex) { 
			ex.printStackTrace(); 
			return;
		}

		// Create tables if nonexistent
		if (!tablesExists) {
			String defaultStats = "";
			for (StatisticType type : StatisticManager.defaultStats) {
				defaultStats += type.name() + " INT, ";
			}
			if (defaultStats.endsWith(", ")) 
				defaultStats = defaultStats.substring(0, defaultStats.length() - 2);

			try {
				Statement cstmt = conn.createStatement();
				conn.setAutoCommit(false);
				cstmt.addBatch("CREATE TABLE IF NOT EXISTS stats_alltime (" + defaultStats + ");");
				cstmt.addBatch("CREATE TABLE IF NOT EXISTS stats (date DATE, " + defaultStats + ");");
				cstmt.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				cstmt.close();
			} catch (SQLException ex) { ex.printStackTrace(); }
		}

		// Add new columns to existing table
		try {
			rs = stmt.executeQuery("PRAGMA table_info(stats_alltime);");
			Set<StatisticType> allTypes = new HashSet<>(Ramolos.getInstance().getStatisticManager().getStatistics().keySet());
			while(rs.next()) {
				StatisticType type = StatisticType.valueOf(rs.getString("name"));
				if (type == null) continue;
				existingTypes.add(type);
			}
			rs.close();
			allTypes.removeAll(existingTypes);

			if (allTypes.size() == 0) return;

			conn.setAutoCommit(false);
			for (StatisticType type : allTypes) {
				stmt.addBatch("ALTER TABLE stats_alltime ADD " + type.name() + " INT;");
				stmt.addBatch("ALTER TABLE stats ADD " + type.name() + " INT;");
			}
			stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			stmt.close();
		} catch (SQLException ex) { ex.printStackTrace(); }
	}

	public void readStats() {
		StatisticManager sm = Ramolos.getInstance().getStatisticManager();
		Collection<Statistic> stats = sm.getStatistics().values();
		dailyValuesLoaded = false;
		alltimeValuesLoaded = false;
		// Reset stats values
		for (Statistic stat : stats) stat.setRawValues(new int[] { 0, 0, 0 });
		
		final String[] queries = new String[] {
			"SELECT * FROM stats_alltime;",
			"SELECT * FROM stats where julianday() - julianday(date) <= 30;",
			"SELECT * FROM stats where date = '" + Utils.getDateString() + "';"
		};
		// query alltime, 30d, today
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < queries.length; i++) {
				boolean hasResult = stmt.execute(queries[i]);
				if (hasResult) readResultSet(stmt, i);
			}
			stmt.close();
			if (dailyValuesLoaded) dailyDate = Utils.getDateString();
		} catch (SQLException ex) { ex.printStackTrace(); }
		for (Statistic stat : stats) stat.update(true, true);
	}

	private void readResultSet(Statement stmt, int interval) throws SQLException {
		ResultSet rs = stmt.getResultSet();
		StatisticManager sm = Ramolos.getInstance().getStatisticManager();
		Collection<Statistic> stats = sm.getStatistics().values();
		while(rs.next()) {
			if (interval == Statistic.INTERVAL_TODAY) dailyValuesLoaded = true;
			else if (interval == Statistic.INTERVAL_ALLTIME) alltimeValuesLoaded = true;
			for (Statistic stat : stats) {
				try { stat.addRawValue(interval, rs.getInt(stat.getType().name()));	}
				catch (SQLException ex) { ex.printStackTrace(); }
			}
		}
	}

	public void checkTime() {
		String newDate = Utils.getDateString();
		if (newDate.equals(dailyDate)) return;
		writeStats();
		readStats();
	}

	public void writeStats() {
		String dateString = Utils.getDateString();
		StatisticManager sm = Ramolos.getInstance().getStatisticManager();
		Statistic[] stats = sm.getStatistics().values().toArray(new Statistic[sm.getStatistics().size()]);

		for (int interval = 0; interval < 3; interval += 2) {
			String update = "";
			String insertColumns = "";
			String insertValues = "";

			for (int i = 0; i < stats.length; i++) {
				Statistic stat = stats[i];

				update += stat.getType().name() + " = " + stat.values[interval];
				insertColumns += stat.getType().name();
				insertValues += stat.values[interval];
				
				if (i != stats.length - 1) {
					update += ", ";
					insertColumns += ", ";
					insertValues += ", ";
				}
			}

			boolean alltime = interval == Statistic.INTERVAL_ALLTIME;
			String table = alltime ? "stats_alltime" : "stats";
			boolean insert = alltime ? !alltimeValuesLoaded : !(dailyValuesLoaded && dailyDate.equals(dateString));
			String selector = alltime ? "" : " WHERE date = '" + dateString + "'";
			
			if (!alltime) {
				insertColumns = "date, " + insertColumns;
				insertValues = "'" + dailyDate + "', " + insertValues;
			}

			String query = "";
			if (insert) query = String.format("INSERT INTO %s (%s) VALUES (%s);", table, insertColumns, insertValues);
			else query = String.format("UPDATE %s SET %s%s;", table, update, selector);

			try {
				Statement stmt = conn.createStatement();
				conn.setAutoCommit(false);
				stmt.addBatch(query);
				if (!alltime) stmt.addBatch("DELETE FROM stats WHERE julianday('" + dateString + "') - julianday(date) >= 30;");
				stmt.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				stmt.close();
			} catch (SQLException ex) { ex.printStackTrace(); }
		}
	}
}
