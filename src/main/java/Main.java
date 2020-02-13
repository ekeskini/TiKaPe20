package main.java;

import java.sql.*;
import java.util.*;
import main.java.DatabaseHandler;

public class Main {

	public static void main(String[] args) throws SQLException {
		//Setting up required objects and classes
		Connection parceldb = DriverManager.getConnection("jdbc:sqlite:parceldatabase.db");
		Scanner scanner = new Scanner(System.in);
		DatabaseHandler dbhandler = new DatabaseHandler(parceldb, scanner);
		
		//A variable for checking if the user has finished
		boolean finished = false;
		System.out.println("Welcome to the parcel database! Type 'Help' for instructions.");
		//Loop functionality 
		while (finished == false) {
			System.out.println("Enter command:");
			String input = scanner.nextLine();
			if (dbhandler.checkIfValidInt(input) == false) {
				if (input.trim().toLowerCase().equals("help")) {
					System.out.println(printHelp());
					continue;
				}
				System.out.println("Not a valid command. Enter a number between 0-9, -1 to end the program.\n'Help' lists the functions of the commands.");
				continue;
			}
			Integer i = Integer.valueOf(input);
			switch(i) {
				case 1:
					dbhandler.createDatabase();
					break;
				case 2:
					dbhandler.addLocation();
					break;
				case 3:
					dbhandler.addCustomer();
					break;
				case 4:
					dbhandler.addParcel();
					break;
				case 5:
					dbhandler.addEvent();
					break;
				case 6:
					dbhandler.getEvents();
					break;
				case 7:
					dbhandler.getParcels();
					break;
				case 8:
					dbhandler.getEventsInLocation();
					break;
				case 9:
					//Opening a new connection to another database for testing (as not to affect the "proper" database)
					parceldb.close();
					
					//Test database without indexes
					System.out.println("Test without indexes:");
					Connection testdb = DriverManager.getConnection("jdbc:sqlite:testdatabase.db");
					DatabaseHandler testdbhandler = new DatabaseHandler(testdb, scanner);
					testdbhandler.createDatabase();
					testdbhandler.efficiencytest();
					testdbhandler.clearDatabase();
					testdb.close();
					
					//Test database with indexes
					System.out.println("Test with indexes:");
					Connection testdbindex = DriverManager.getConnection("jdbc:sqlite:testdatabasewindex.db");
					DatabaseHandler testdbhandlerindex = new DatabaseHandler(testdbindex, scanner);
					testdbhandlerindex.createDatabaseWIndexes();
					testdbhandlerindex.efficiencytest();
					testdbhandlerindex.clearDatabase();
					testdbindex.close();
					
					//Opening the connection the the original "proper" database
					parceldb = DriverManager.getConnection("jdbc:sqlite:parceldatabase.db");
					dbhandler = new DatabaseHandler(parceldb, scanner);
					break;
				case -1:					
					finished = true;
					break;
			}
		}
		System.out.println("Thank you for using the parcel database!");
	}
	public static String printHelp() {
		return "List of commands:"
				+ "\n1: Create database with appropriate tables if it does not exist"
				+ "\n2: Add a location"
				+ "\n3: Add a customer"
				+ "\n4: Add a parcel"
				+ "\n5: Add an event for a parcel"
				+ "\n6: List the events concerning a parcel"
				+ "\n7: List all parcels belonging to a certain customer"
				+ "\n8: List all events in a certain location"
				+ "\n9: Execute an efficiency test for the database"
				+ "\n-1: Close the database."
				+ "\nNote that when opening the database manager for the first time, \n"
				+ "the command 1 has to be run.";
	}
}
