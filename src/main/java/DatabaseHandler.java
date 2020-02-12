package main.java;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.util.*;

public class DatabaseHandler {
	private Connection dbconnection;
	private Scanner scanner;
	
	//Constructor which injects the database and the scanner to the class
	public DatabaseHandler(Connection connection, Scanner injectedscanner) {
		dbconnection = connection;
		scanner = injectedscanner;
	}
	
	//Procedure for command 2: Adding a location
	public void addLocation() throws SQLException{
		System.out.println("Enter location name:");
		String name = scanner.nextLine();
		
		//Preparing a parametrised statement
		PreparedStatement pstatement = dbconnection.prepareStatement("INSERT INTO Location(name) VALUES (?);");
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
	
	//Procedure for command 3: adding a customer
	public void addCustomer() throws SQLException{
		System.out.println("Enter customer name:");
		String name = scanner.nextLine();
		
		//Preparing a parametrised statement
		PreparedStatement pstatement = dbconnection.prepareStatement("INSERT INTO Customer(name) Values (?)");
		pstatement.setString(1, name);
		
		try {
			pstatement.executeUpdate();
			System.out.println("Customer added");
		}
		catch (SQLException e) {
			System.out.println("ERROR: Such an customer already exists");
		}
	}
	
	//Procedure for command 4: adding a parcel
	public void addParcel() throws SQLException {
		
		//Inputs by user
		System.out.println("Enter parcel tracking number:");
		String tn = scanner.nextLine();
		//Checking if the input is valid
		if (checkIfValidIntWMessage(tn) == false) {
			return;
		}
		Integer tracking_number = Integer.valueOf(tn);
		System.out.println("Enter customer name for parcel:");
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
			System.out.println("Such an parcel already exists");
		}
	}
	
	//Procedure for command 5: adding an event
	public void addEvent() throws SQLException{
		System.out.println("Enter parcel tracking number:");
		String tn = scanner.nextLine();
		if (checkIfValidIntWMessage(tn) == false) {
			return;
		}
		Integer tracking_number = Integer.valueOf(tn);
		System.out.println("Enter location:");
		String location = scanner.nextLine();
		System.out.println("Enter description of event:");
		String description = scanner.nextLine();
		
		//Preparing variable for later use (the actual value is extracted from a query later)
		Integer location_id = -1;
		
		//Preparing queries for checking the database for existing location and tracking number
		PreparedStatement controlstatement1 = dbconnection.prepareStatement("SELECT id, COUNT(*) as count FROM Location WHERE name = (?)");
		PreparedStatement controlstatement2 = dbconnection.prepareStatement("SELECT tracking_number, COUNT(*) as count FROM Parcel WHERE tracking_number = (?)");
		
		//Executing queries
		controlstatement1.setString(1, location);
		controlstatement2.setInt(1, tracking_number);
		
		//Comparing queries to expected return values
		//Gives an error message and returns to the main menu if the parcel/location does not exist
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
		Date date = Date.valueOf(java.time.LocalDate.now());
		Time time = Time.valueOf(java.time.LocalTime.now());
		
		//Preparing statement for adding a parcel
		PreparedStatement pstatement = dbconnection.prepareStatement("INSERT INTO Event (tracking_number, location_id, description, date, time) VALUES (?, ?, ?, ?, ?)");
		pstatement.setInt(1, tracking_number);
		pstatement.setInt(2, location_id);
		pstatement.setString(3, description);
		pstatement.setDate(4, date);
		pstatement.setTime(5, time);
		
		try {
			pstatement.executeUpdate();
			System.out.println("Event added");
		} catch (SQLException e) {
			System.out.println(genericErrorMessage());
		}
	}
	
	//Procedure for command 6: listing events of a certain parcel
	public void getEvents() throws SQLException{
		//User input
		System.out.println("Enter parcel tracking number:");
		String tn = scanner.nextLine();
		if (checkIfValidIntWMessage(tn) == false) {
			return;
		}
		Integer tracking_number = Integer.valueOf(tn);
		
		//Preparing a statement for the query
		PreparedStatement pstatement = dbconnection.prepareStatement("SELECT Event.description as eventdescription, Location.name as locationname, "
				+ "Event.date AS d, Event.time AS t FROM Event LEFT JOIN Location "
				+ "ON Event.location_id = Location.id WHERE Event.tracking_number = ?");
		pstatement.setInt(1, tracking_number);
		
		try {
			//Executing the query
			ResultSet rs = pstatement.executeQuery();
			
			System.out.println("List of events for parcel " + tracking_number + ":");
			//Listing results
			while (rs.next()) {
				
				System.out.println("Time: " + rs.getDate("d") + " " + rs.getTime("t") + ", location: " + rs.getString("locationname") + ", description: " + rs.getString("eventdescription"));
			}
		} catch (SQLException e) {
			System.out.println(genericErrorMessage());
			e.printStackTrace();
		}
		System.out.println("No more events to list.");
	}
	
	//Procedure for command 7: listing parcels and the count of events per parcel for given customer
	public void getParcels() throws SQLException{
		System.out.println("Enter customer name:");
		String customername = scanner.nextLine();
		
		//Preparing variable for later use (correct value extracted in query later)
		Integer customerid = -1;
		
		//Preparing statement for checking if named customer exists
		PreparedStatement controlstatement = dbconnection.prepareStatement("SELECT id, COUNT(*) AS count FROM Customer WHERE name = (?)");
		controlstatement.setString(1, customername);
		
		//Checking for the existence of named customer
		try {
			ResultSet rs = controlstatement.executeQuery();
			
			//If there is no customer or there (for some reason) are more customers than 1 with the same name
			if (rs.getInt("count") != 1) {
				System.out.println("No customer with the given name exists. Please check your input and try again");
				return;
			}
			//Extracting customer id
			customerid = rs.getInt("id");
		} catch (SQLException e){
			System.out.println(genericErrorMessage());
		}
		
		//Preparing statement for wanted query
		PreparedStatement pstatement = dbconnection.prepareStatement("SELECT Parcel.tracking_number AS tn, COALESCE(COUNT(Event.id), 0) AS count FROM Parcel "
				+ "LEFT JOIN Event ON Event.tracking_number = Parcel.tracking_number "
				+ "WHERE Parcel.customer_id = (?) "
				+ "GROUP BY tn "
				+ "ORDER BY tn");
		
		pstatement.setInt(1, customerid);
		
		//Executing query and listing parcels + amount of events concerning each parcel
		System.out.println("Parcels for customer " + customername + ":");
		try {
			ResultSet rs2 = pstatement.executeQuery();
			
			while(rs2.next()) {
				
				System.out.println(rs2.getInt("tn") + ", number of events: " + rs2.getInt("count"));
			}
		} catch (SQLException e) {
			System.out.println(genericErrorMessage());
		}
		System.out.println("No more parcels to list.");		
	}
	
	//Procedure for command 8: listing events in a specific location on a certain date
	public void getEventsInLocation() throws SQLException{
		System.out.println("Enter location name:");
		String locationname = scanner.nextLine();
		
		//Setting up a date-type variable
		Date d = Date.valueOf("0000-01-01");
		
		System.out.println("Enter date (format YYYY-MM-DD), with the current date as input 'today':");
		//Setting the date-type object to the wanted date
		while (true) {	
						
			String date = scanner.nextLine();
			
			if (date.equals("today")) {
				d = Date.valueOf(java.time.LocalDate.now());
				break;
			}
			//Checking if input is of correct format
			try {
				//If it is, change the value of the variable d to the wanted date
				d = Date.valueOf(date);
			} catch (Exception e) {
				System.out.println(genericErrorMessage());
				continue;
			}					
			break;
		}
		
		//Preparing variable for later use
		Integer locationid = -1;
		
		//Preparing statement for extracting the location id based on the location name
		PreparedStatement controlstatement = dbconnection.prepareStatement("SELECT id, COUNT(*) AS count FROM Location WHERE name = ?");
		controlstatement.setString(1, locationname);
		
		try {
			ResultSet rs = controlstatement.executeQuery();
			
			if(rs.getInt("count") != 1){
				System.out.println("No location with the given name exists. Please check your input and try again");
				return;
			}
			locationid = rs.getInt("id");
		} catch (SQLException e) {
			System.out.println(genericErrorMessage());
		}
		
		//Preparing the main query, using the location id and date as parameters
		PreparedStatement pstatement = dbconnection.prepareStatement("SELECT COUNT(*) AS count FROM Event WHERE location_id = ? AND date = ?");
		pstatement.setInt(1, locationid);
		pstatement.setDate(2, d);
		
		try {
			ResultSet rs = pstatement.executeQuery();
			while(rs.next()) {
				System.out.println("Events in location " + locationname + " on date " + d.toString() + ": " + rs.getInt("count"));
			}
		} catch (SQLException e) {
			System.out.println(genericErrorMessage());
		}
	}
	
	//Procedure 9: efficiency test
	
	
	//Procedure 1: Creating database and the required empty tables
	//Database connection as a parameter for the Procedure
	public void createDatabase() throws SQLException{		
		Statement s = dbconnection.createStatement();
		
		try {				
			//Creating the necessary tables 
			s.execute("CREATE TABLE Customer (id INTEGER PRIMARY KEY, name TEXT UNIQUE NOT NULL)");
			s.execute("CREATE TABLE Parcel (tracking_number INTEGER UNIQUE PRIMARY KEY, customer_id INTEGER, "
					+ "FOREIGN KEY(customer_id) REFERENCES Customer(id))");
			s.execute("CREATE TABLE Event (id INTEGER PRIMARY KEY, tracking_number INTEGER, location_id INTEGER, "
					+ "description TEXT, date DATE, time TIME, FOREIGN KEY(tracking_number) REFERENCES Parcel(tracking_number), "
					+ "FOREIGN KEY(location_id) REFERENCES Location(id))");
			s.execute("CREATE TABLE Location(id INTEGER PRIMARY KEY, name TEXT UNIQUE NOT NULL)");
		} 
		//catch statement for when the user executes the command "1" more than once
		catch (SQLException e) {
			System.out.println("ERROR: Database already exists");		
			
		}
	}
	//Checking if input is a valid integer (with an error message)
	public boolean checkIfValidIntWMessage(String i) {
		try {
			Integer.valueOf(i);
	
		} catch (Exception e) {
			System.out.println("Not an integer. Please check your input");
			return false;
		}
		return true;		
	}
	//Checking if input is a valid integer (without an error message)
	public boolean checkIfValidInt(String i) {
		try {
			Integer.valueOf(i);
	
		} catch (Exception e) {
			return false;
		}
		return true;		
	}
	
	//Generic error message for uncommon situations
	public String genericErrorMessage() {
		return "ERROR: An error occured. Please check your input and try again";
	}
	
}