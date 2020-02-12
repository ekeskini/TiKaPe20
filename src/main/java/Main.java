package main.java;

import java.sql.*;
import java.util.*;
import main.java.DatabaseHandler;

public class Main {

	public static void main(String[] args) throws SQLException {
		Connection parceldb = DriverManager.getConnection("jdbc:sqlite:parceldatabase.db");
		
		Scanner scanner = new Scanner(System.in);
		
		DatabaseHandler dbhandler = new DatabaseHandler(parceldb, scanner);
		
		boolean finished = false;
		
		while (true) {
			System.out.println("Enter command:");
			String input = scanner.nextLine();
			if (dbhandler.checkIfValidInt(input) == false) {
				System.out.println("Not a valid command. Enter a number between 0-9, -1 to end the program");
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
				case -1:					
					finished = true;
					break;
			}
			if (finished == true) {
				break;
			}
		}
		System.out.println("Thank you for using the parcel database!");
	}
}
