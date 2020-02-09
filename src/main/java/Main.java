package main.java;

import java.sql.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws SQLException {
		Connection parceldb = DriverManager.getConnection("jdbc:sqlite:parceldatabase.db");
		createDatabase(parceldb);
		
	}
	
	public static void createDatabase(Connection connection){
		try {
			Statement s = connection.createStatement();
			s.execute("CREATE TABLE Asiakas (id INTEGER PRIMARY KEY, name TEXT UNIQUE)");
			
		} catch (SQLException e) {
			System.out.println("Database already exists");
		}
	}
}
