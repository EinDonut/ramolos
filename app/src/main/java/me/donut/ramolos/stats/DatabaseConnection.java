package me.donut.ramolos.stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	private Connection conn = null;

	public DatabaseConnection() {
		connect();
	}

	public void connect() {
        try {
            String url = "jdbc:sqlite:C:/Users/DerDonut/Videos/test.db";
            conn = DriverManager.getConnection(url);            
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

	public void disconnect() {
		if (conn == null) return;
		try { conn.close(); }
		catch (SQLException ex) { ex.printStackTrace(); }
	}

}
