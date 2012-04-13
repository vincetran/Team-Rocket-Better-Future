import java.util.Scanner;
import java.util.*;
import java.sql.*;
import java.lang.*;


class Administrator
{
	public static Scanner in = new Scanner(System.in);
	 
	public Administrator()
	{
		boolean exit = false;
		int action;

		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());   
			String url = "jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass"; 
			connection = DriverManager.getConnection(url, "vtt2", "password"); 
		}catch(SQLException e) { e.printStackTrace(); }
		
		while(exit != true)
		{
			System.out.println("\n\nAdministrator Options: \n");
			System.out.println("1.  Customer Registration");
			System.out.println("2.  Update Share Price");
			System.out.println("3.  Add Mutual Fund");
			System.out.println("4.  Update Time and Date");
			System.out.println("5.  Statistics");
			System.out.println("6.  Exit");
			
			System.out.println("-------------------------\n");
			
			System.out.print("Please choose a option(1-6): ");
			action = in.nextInt();
			
			switch(action)
			{
				case 1: custRegistration();
						break;
				case 2: updateShares();
						break;
				case 3: addMFunds();
						break;
				case 4: updateTD();
						break;
				case 5: statistics();
						break;
				default: exit = true;
						 break;
			}
		}
	}
	
	public void custRegistration()
	{
		System.out.print("\n\nPlease enter user name: ");
		String name = in.next();
		System.out.print("\n\nPlease enter user address: ");
		String adress = in.next();
		System.out.print("\n\nPlease enter user email: ");
		String email = in.next();
		System.out.print("\n\nPlease enter user preferred login name: ");
		String logName = in.next();
		System.out.print("\n\nPlease enter user preferred password: ");
		String password = in.next();
		
		//Insert SQL code
		
		System.out.println("This username is taken already.");
	}
	
	public static void updateShare()
	{
		System.out.println("Which share would you like to update?");
		//SQL to print all of the shares in db
		//User selects a share
		//SQL check to see if it has been updated TODAY
		//If it has already been updated:
		System.out.println("Sorry, a share can only be updated once a day");
		//If not:
		System.out.println("What is this share's closing price?");
		//SQL INSERT into closingprice values(share, price, date);
	}
	
	public static void addMFunds()
	{
		boolean done = false;
		while(done == false)
		{
			String symbol, desc, category;

			System.out.print("\n\nWhat is the name of the new mutual fund you would like to add: ");
			String addMF = in.next();

			System.out.print("\nWhat is this mutual fund's symbol? ");
			symbol = in.next();
		
			try{
				PreparedStatement ps = connection.prepareStatement("SELECT * FROM mutualfund WHERE symbol=?");
				ps.setString(1, symbol);
				ResultSet rs = ps.executeQuery();
				if(rs.next())//if there is a row with the same name
				{
					System.out.println("Mutual fund symbol of the same name already exists");
				}
				else
				{
					System.out.print("\nWhat is this mutual fund's description? ");
					desc = in.next();

					System.out.print("\nWhat is this mutual fund's category? ");
					category = in.next();

					PreparedStatement ps1 = connection.prepareStatement("INSERT into mutualfund VALUES(?, ?, ?, ?, ?)");
					ps1.setString(1, symbol);
					ps1.setString(2, addMF);
					ps1.setString(3, desc);
					ps1.setString(4, category);
					ps1.setDate(5, new Date(Date.currentTimeMillis()));
					ResultSet rs = ps1.executeQuery();

				}


				System.out.println("\n\nPress 'ENTER' to continue...");
				String input = br.readLine();
			}catch(Exception e) { e.printStackTrace(); }
			
			System.out.print("Would you like to change anymore precentages?(y/n): ");
			String res = in.next();
			if(res.equals("n")) { done = true; }
		}
		
	}
	
	public static void updateTD()
	{
		//Date and Time?
	}
	
	public static void statistics()
	{
		//SQL code
	}
}