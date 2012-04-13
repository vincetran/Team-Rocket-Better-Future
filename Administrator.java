import java.util.*;
import java.sql.*;
import java.lang.*;
import java.io.*;

public class Administrator
{
	public static Scanner in = new Scanner(System.in);
	private Connection connection;
	private BufferedReader br;
	 
	public Administrator()
	{
		boolean exit = false;
		int action;

		br = new BufferedReader(new InputStreamReader(System.in));

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
				case 2: updateShare();
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
		Boolean repeat = true;
		Boolean looper=true;
		Boolean isCustomer=true;
		String custOrAdmin, fullname, address, email, login, password;
		while(repeat)
		{
			while(looper)
			{
				System.out.print("\n\nIs the new user a CUSTOMER or ADMINISTRATOR? ");
				custOrAdmin = in.next();
				custOrAdmin=custOrAdmin.toLowerCase();
				if(custOrAdmin.compareTo("customer")==0)
					looper=false;
				else if(custOrAdmin.compareTo("administrator")==0)
				{
					isCustomer=false;
					looper=false;
				}
				else
					System.out.println("Sorry, your input was not recognized...");
			}

			looper = true;
			System.out.print("\n\nPlease enter user preferred login name: ");
			login = in.next();

			while(looper)
			{
				try{
					PreparedStatement ps = connection.prepareStatement("SELECT * FROM customer WHERE login=?");
					ps.setString(1, login);
					ResultSet rs = ps.executeQuery();
					if(rs.next())
					{
						System.out.println("\nSorry, that login name has already been chosen.");
						System.out.print("\nPlease enter another login name: ");
						login = in.next();
					}
					else
						looper=false;
				}catch(Exception e){ e.printStackTrace(); }
			}

			System.out.print("\n\nPlease enter user's full name: ");
			fullname = in.next();
			System.out.print("\n\nPlease enter user address: ");
			address = in.next();
			System.out.print("\n\nPlease enter user email: ");
			email = in.next();
			System.out.print("\n\nPlease enter user preferred password: ");
			password = in.next();

			try{
				PreparedStatement ps;
				if(isCustomer)
					ps = connection.prepareStatement("INSERT INTO customer VALUES(?,?,?,?,?,0.0)");
				else
					ps = connection.prepareStatement("INSERT INTO administrator VALUES(?,?,?,?,?)");
				ps.setString(1, login);
				ps.setString(2, fullname);
				ps.setString(3, email);
				ps.setString(4, address);
				ps.setString(5, password);
				ResultSet rs = ps.executeQuery();
				System.out.println("\nUser account created!");
				repeat = false;
			}catch(Exception e){ e.printStackTrace(); }
		}
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
	
	public void addMFunds()
	{
		boolean done = true;;
		while(done)
		{
			String symbol, desc, category;

			System.out.print("\n\nWhat is the name of the new mutual fund you would like to add: ");
			in.nextLine();
			String addMF = in.nextLine();
			
			boolean check = true;
			while(check)
			{
				System.out.print("\nWhat is this mutual fund's symbol? ");
				symbol = in.next();
				symbol = symbol.toUpperCase();

				try{
					PreparedStatement ps = connection.prepareStatement("SELECT * FROM mutualfund WHERE symbol=?");
					ps.setString(1, symbol);
					ResultSet rs = ps.executeQuery();
					if(rs.next())//if there is a row with the same name
					{
						System.out.println("Mutual fund symbol of the same name already exists");
						System.out.println("Please try another symbol...");
					}
					else
					{
						System.out.print("\nWhat is this mutual fund's description? ");
						in.nextLine();
						desc = in.nextLine();

						System.out.print("\nWhat is this mutual fund's category? ");
						category = in.next();
						in.nextLine();
						PreparedStatement ps1 = connection.prepareStatement("INSERT into mutualfund VALUES(?, ?, ?, ?, to_date(sysdate))");
						ps1.setString(1, symbol);
						ps1.setString(2, addMF);
						ps1.setString(3, desc);
						ps1.setString(4, category);
						ps1.executeQuery();

						done = false;
						check = false;
						System.out.println("\n\nPress 'ENTER' to continue...");
						String input = br.readLine();
					}
				}catch(Exception e) { e.printStackTrace(); }
			}
		}
	}
	
	public void updateTD()
	{
		try{
			PreparedStatement ps = connection.prepareStatement("SELECT to_char(sysdate) as today FROM dual");
			ResultSet rs = ps.executeQuery();
			rs.next();
			System.out.println("\n\nToday's date is: "+rs.getString("today"));
			System.out.print("\nWould you like to set/update the system date \nto today's date (1 for Yes, 0 for No)?  ");
			int ans = in.nextInt();
			Boolean repeat=true;
			while(repeat)
			{
				if(ans==1)
				{
					ps = connection.prepareStatement("DELETE FROM mutualdate");
					ps.executeQuery();
					ps = connection.prepareStatement("INSERT INTO mutualdate VALUES(to_date(sysdate))");
					ps.executeQuery();
					repeat=false;
				}
				else if(ans==0)
				{
					repeat=false;
				}
			}
		}catch(Exception e) { e.printStackTrace(); }
		
	}
	
	public void statistics()
	{
		//SQL code
	}
}