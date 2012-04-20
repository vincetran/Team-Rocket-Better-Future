
import java.util.Scanner;
import java.util.*;
import java.sql.*;
import java.lang.*;

public class TRB 
{
	public static Scanner in = new Scanner(System.in);
	
	public static void main(String args[]) 
	{
		String username, password, query;
		Connection connection; //used to hold the jdbc connection to the DB

		username = "vtt2"; //This is your username in oracle
		password = "password"; //This is your password in oracle
		try{
			// Register the oracle driver.  
			DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
			
			//This is the location of the database.  This is the database in oracle
			//provided to the class
			String url = "jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass"; 
			
			//create a connection to DB on db10.cs.pitt.edu
			connection = DriverManager.getConnection(url, username, password); 
			//comment
			if(adminOrCust())
			{
				//If the user is a customer
				String userN, passW;

				System.out.println("Please log in below:");
				System.out.println("-------------------------\n");
				System.out.print("Username: ");
				userN = in.next();
				System.out.print("\nPassword: ");
				passW = in.next();
				
				query = "SELECT name FROM customer WHERE login=?";
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, userN);
				ResultSet rs = ps.executeQuery();
				if(rs.next())
				{
					PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM customer WHERE login=? AND password=?");
					ps2.setString(1, userN);
					ps2.setString(2, passW);
					ResultSet rs2 = ps2.executeQuery();
					if(rs2.next())
					{
						Customer cust = new Customer(rs2);
					}
					else
					{
						System.out.println("Incorrect Password");
						System.exit(0);
					}
					
				}
				else
				{
					System.out.println("Supplied username does not exist.\n");
					System.exit(0);
				}
			}
			else
			{
				//If the user is an administrator
				String userN, passW;

				System.out.println("Admin, please log in below:");
				System.out.println("-------------------------\n");
				System.out.print("Username: ");
				userN = in.next();
				System.out.print("\nPassword: ");
				passW = in.next();
				
				query = "SELECT name FROM administrator WHERE login=?";
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, userN);
				ResultSet rs = ps.executeQuery();
				if(rs.next())
				{
					query = "SELECT login FROM administrator WHERE login=? AND password=?";
					ps = connection.prepareStatement(query);
					ps.setString(1, userN);
					ps.setString(2, passW);
					ResultSet rs2 = ps.executeQuery();
					if(rs2.next())
					{
						Administrator admin = new Administrator();
					}
					else
					{
						System.out.println("Incorrect Password");
						System.exit(0);
					}

				}
				else
				{
					System.out.println("Supplied username does not exist.");
					System.exit(0);
				}
			}
		}
		catch(Exception Ex)  {
			System.out.println("Machine Error: " +
					   Ex.toString());
			System.exit(0);
		}
	}

	public static boolean adminOrCust()
	{
		System.out.println("\nWelcome to Team Rocket Finances!");
		System.out.println("Where we help you take over the World!\n\n");
		System.out.println("Login Options:");
		System.out.println("1.  Customer");
		System.out.println("2.  Administrator");
		System.out.println("3.  Exit");
		System.out.println("-------------------------");
		System.out.print("Please choose a option(1-3): ");
		String answer = in.next();
		if(answer.compareTo("1") == 0)
			return true;
		else if(answer.compareTo("2") == 0)
			return false;
		else if(answer.compareTo("3") == 0)
			System.exit(0);
		else
			System.out.println("Improper answer. Exitting now.\n");
		System.exit(0);

		return false;
	}
}
