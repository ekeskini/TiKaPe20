package main.java;

import java.sql.*;
import java.util.*;
import main.java.DatabaseHandler;

public class Main {

	public static void main(String[] args) throws SQLException {
		Connection parceldb = DriverManager.getConnection("jdbc:sqlite:parceldatabase.db");
		
		Scanner scanner = new Scanner(System.in);
		
		DatabaseHandler dbhandler = new DatabaseHandler(parceldb, scanner);
		
		dbhandler.createDatabase();
		dbhandler.addCustomer();
	}
}
