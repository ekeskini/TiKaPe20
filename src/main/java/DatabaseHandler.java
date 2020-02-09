package main.java;

import java.sql.*;
import java.util.*;

public class DatabaseHandler {
	private Connection dbconnection;
	
	//Constructor which injects the database to the class
	public DatabaseHandler(Connection connection) {
		dbconnection = connection;
	}
	
	//Method 2: Adding a location
	public void addLocation(String name) {
		dbconnection.prepareStatement("INSERT INTO Location, (");
		
		try {
			
		}
	}
	
	
	//Method 1: Creating database and the required empty tables
	//Database connection as a parameter for the method
	public void createDatabase() throws SQLException{		
		Statement s = dbconnection.createStatement();
		
		try {		
			//Creating the necessary tables 
			s.execute("CREATE TABLE Customer (id INTEGER PRIMARY KEY NOT NULL, name TEXT UNIQUE NOT NULL)");
			s.execute("CREATE TABLE Parcel (id INTEGER PRIMARY KEY NOT NULL, tracking_number INTEGER UNIQUE NOT NULL, customer_id INTEGER NOT NULL, "
					+ "FOREIGN KEY(customer_id) REFERENCES Customer(id))");
			s.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY NOT NULL, parcel_id INTEGER NOT NULL, location_id INTEGER NOT NULL, "
					+ "description TEXT, date DATE, time TIME, FOREIGN KEY(parcel_id) REFERENCES Parcel(id), "
					+ "FOREIGN KEY(location_id) REFERENCES Location(id))");
			s.execute("CREATE TABLE Location(id INTEGER NOT NULL, name TEXT UNIQUE)");
		} 
		//try-catch statement for when the user executes the command "1" more than once
		catch (SQLException e) {
			System.out.println("Database already exists.");			
		}
	}
}
