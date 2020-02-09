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
			Integer input = Integer.valueOf(scanner.nextLine());
			switch(input) {
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
