package main.java;

import java.sql.*;
import java.util.*;

public class DatabaseHandler {
	private Connection dbconnection;
	private Scanner scanner;
	
	//Constructor which injects the database and the scanner to the class
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
		}
	}
	
	//Method for command 3: adding a customer
	public void addCustomer() throws SQLException{
		System.out.println("Enter customer name:");
		String name = scanner.nextLine();
		
		//Preparing a parametrised statement
		PreparedStatement pstatement = dbconnection.prepareStatement("INSERT INTO Customer (name) Values(?)");
		pstatement.setString(1, name);
		
		try {
			pstatement.executeUpdate();
			System.out.println("Customer added");
		}
		catch (SQLException e) {
			System.out.println("ERROR: Such an customer already exists");
		}
	}
	
	//Method for command 4: adding a parcel
	public void addParcel() throws SQLException {
		
		//Inputs by user
		System.out.println("Enter parcel tracking number:");
		Integer tracking_number = Integer.valueOf(scanner.nextLine());
		System.out.println("Enter customer name:");
		String customer_name = scanner.nextLine();
		
		//Preparing a variable for later use (the customer id is extracted from a query later)
		Integer customer_id = -1;
		
		//Checking if customer exists
		//Preparing a parametrised query where the customer name given by the user is a parameter
		PreparedStatement controlstatement = dbconnection.prepareStatement("SELECT id, COUNT(*) as count FROM Customer WHERE Customer.name = (?)");
		controlstatement.setString(1, customer_name);
		
		try {
			ResultSet rs = controlstatement.executeQuery();
			if (rs.getInt("count") != 1) {
				System.out.println("No customer with the given name exists. Please create a new customer or enter an existing customer name");
				//Returning to the main menu in cases where the query returns more or less than one customer
				return;
			}
			//Selecting the customer id for adding the parcel
			customer_id = rs.getInt("id");
		} 
		catch (SQLException e) {
			System.out.println("Problems with adding the parcel. Please check your input and try again");
		}
		//Preparing the statement for adding a parcel
		PreparedStatement pstatement = dbconnection.prepareStatement("INSERT INTO Parcel (tracking_number, customer_id) VALUES (?,?)");
		pstatement.setInt(1, tracking_number);
		pstatement.setInt(2, customer_id);
		
		try {
			pstatement.executeUpdate();
			System.out.println("Parcel added");
		}
		catch (SQLException e) {
			System.out.println("ERROR: Such an parcel already exists");
		}
	}
	
	//Method for command 5: adding an event
	public void addEvent() throws SQLException{
		System.out.println("Enter parcel tracking number:");
		Integer tracking_number = Integer.valueOf(scanner.nextLine());
		System.out.println("Enter location:");
		String location = scanner.nextLine();
		System.out.println("Enter description of event:");
		String description = scanner.nextLine();
		
		//Preparing variable for later use (the actual value is extracted from a query later)
		Integer location_id = -1;
		
		//Preparing queries for checking the database for existing location and tracking number
		PreparedStatement controlstatement1 = dbconnection.prepareStatement("SELECT id, COUNT(*) as count FROM Location WHERE name = (?)");
		PreparedStatement controlstatement2 = dbconnection.prepareStatement("SELECT tracking_number, COUNT(*) as count FROM Location WHERE tracking_number = (?)");
		
		//Executing queries
		controlstatement1.setString(1, location);
		controlstatement2.setInt(1, tracking_number);
		
		//Comparing queries to expected return values
		try {
			ResultSet rs1 = controlstatement1.executeQuery();
			ResultSet rs2 = controlstatement2.executeQuery();
			if (rs1.getInt("count") != 1) {
				System.out.println("No location with the given name exists. Please create a new location or enter a valid location name");
				return;
			}
			if (rs2.getInt("count") != 1) {
				System.out.println("No parcel with the given tracking number exists. Please create a new location or enter a valid location name");
				return;
			}
			location_id = rs1.getInt("id");
		} catch (SQLException e){
			System.out.println("Problems with adding the event. Please check your input and try again");
		}
		
		//Preparing statement for adding a parcel
		PreparedStatement pstatement = dbconnection.prepareStatement("INSERT INTO Event (tracking_number, location_id, description) VALUES (?, ?, ?)");
		pstatement.setInt(1, tracking_number);
		pstatement.setInt(2, location_id);
		pstatement.setString(3, description);
	}
	
	
	//Method 1: Creating database and the required empty tables
	//Database connection as a parameter for the method
	public void createDatabase() throws SQLException{		
		Statement s = dbconnection.createStatement();
		
		try {		
			//Creating the necessary tables 
			s.execute("CREATE TABLE Customer (id INTEGER PRIMARY KEY, name TEXT UNIQUE NOT NULL)");
			s.execute("CREATE TABLE Parcel (tracking_number INTEGER UNIQUE PRIMARY KEY, customer_id INTEGER, "
					+ "FOREIGN KEY(customer_id) REFERENCES Customer(id))");
			s.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY, tracking_number INTEGER, location_id INTEGER, "
					+ "description TEXT, date DATE, time TIME, FOREIGN KEY(tracking_number) REFERENCES Parcel(id), "
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
