package main.java;

import java.sql.*;
import java.util.*;

public class DatabaseHandler {
	private Connection dbconnection;
	private Scanner scanner;
	
	//Constructor which injects the database to the class
	public DatabaseHandler(Connection connection, Scanner injectedscanner) {
		dbconnection = connection;
		scanner = injectedscanner;
	}
	
	//Method for command 2: Adding a location
	public void addLocation() throws SQLException{
		System.out.println("Enter location name:");
		String name = scanner.nextLine();
		
		//Preparing a parametrised statement
		PreparedStatement pstatement = dbconnection.prepareStatement("INSERT INTO Location (name) VALUES(?)");
		pstatement.setString(1, name);
		
		//Executing statement and preparing for possible errors
		try {
			pstatement.executeUpdate();
			System.out.println("Location added");
		} 
		//Error message
		catch (SQLException e) {
			System.out.println("ERROR: Such an location already exists");
			e.printStackTrace();
		}
	}
	
	
	//Method 1: Creating database and the required empty tables
	//Database connection as a parameter for the method
	public void createDatabase() throws SQLException{		
		Statement s = dbconnection.createStatement();
		
		try {		
			//Creating the necessary tables 
			s.execute("CREATE TABLE Customer (id INTEGER PRIMARY KEY, name TEXT UNIQUE NOT NULL)");
			s.execute("CREATE TABLE Parcel (id INTEGER PRIMARY KEY, tracking_number INTEGER UNIQUE, customer_id INTEGER, "
					+ "FOREIGN KEY(customer_id) REFERENCES Customer(id))");
			s.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY, parcel_id INTEGER, location_id INTEGER, "
					+ "description TEXT, date DATE, time TIME, FOREIGN KEY(parcel_id) REFERENCES Parcel(id), "
					+ "FOREIGN KEY(location_id) REFERENCES Location(id))");
			s.execute("CREATE TABLE Location(id INTEGER, name TEXT UNIQUE)");
		} 
		//try-catch statement for when the user executes the command "1" more than once
		catch (SQLException e) {
			System.out.println("ERROR: Database already exists");		
			e.printStackTrace();
		}
	}
}
