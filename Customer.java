import java.util.*;
import java.sql.*;
import java.lang.*;
import java.io.*;

public class Customer
{
	public static Scanner in = new Scanner(System.in);
	private static String login, name, email, address;
	private static float balance;
	private Connection connection;
	private BufferedReader br;

	public Customer()
	{}
	
	public Customer(ResultSet rs)
	{
		getUserInfo(rs);
		boolean exit = false;
		int action;

		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());   
			String url = "jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass"; 
			connection = DriverManager.getConnection(url, "vtt2", "password"); 
		}catch(SQLException e) { e.printStackTrace(); }

		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Welcome, "+name+"!");
		
		while(exit != true)
		{
			System.out.println("\n\nCustomer Options: \n");
			System.out.println("1.  Browse Mutual Funds");
			System.out.println("2.  Search Mutual Funds by Keyword");
			System.out.println("3.  Invest");
			System.out.println("4.  Sell Shares");
			System.out.println("5.  Buy Shares");
			System.out.println("6.  Change Allocation Preferences");
			System.out.println("7.  Customer Portfolio");
			System.out.println("8.  Exit");
			
			System.out.println("-------------------------\n");
			
			System.out.print("Please choose a option(1-8): ");
			action = in.nextInt();
			
			switch(action)
			{
				case 1: browseFunds();
						break;
				case 2: searchFunds();
						break;
				case 3: investing();
						break;
				case 4: sellShares();
						break;
				case 5: buyShares();
						break;
				case 6: changePref();
						break;
				case 7: portfolio();
						break;
				default: exit = true;
						 break;
			}
		}
	}

	public void getUserInfo(ResultSet rs)
	{
		try{
			this.login = rs.getString("login");
			this.name = rs.getString("name");
			this.email = rs.getString("email");
			this.address = rs.getString("address");
			this.balance = rs.getFloat("balance");
		}catch(SQLException e)
		{ e.printStackTrace(); }
	}
	
	public void browseFunds()
	{
		System.out.println("\n\nPlease pick a action: ");
		System.out.println("1.  Browse all mutual funds");
		System.out.println("2.  Browse by category");
		System.out.println("3.  Browse alphabetically");
		System.out.println("4.  Browse by price on date");
		System.out.println("5.  Exit");
		System.out.println("-------------------------\n");
		System.out.print("Please choose a option(1-5): ");
		int ans = in.nextInt();
		
		if(ans == 1)
		{
			try{
				PreparedStatement ps = connection.prepareStatement("SELECT * FROM mutualfund");
				ResultSet rs = ps.executeQuery();
				System.out.println("Symbol\tName\t\t\tDescription\t\t\tCategory");
				System.out.println("-------------------------------------------------------------------------");
				while(rs.next())
				{
					System.out.println(rs.getString("symbol")+"\t"+rs.getString("name")+"\t\t"+rs.getString("description")+"\t\t\t"+rs.getString("category"));
				}
				System.out.println("\n\nPress 'ENTER' to continue...");
				String input = br.readLine();
			}catch(Exception e) { e.printStackTrace(); }
		}
		else if(ans ==2)
		{
			System.out.print("\nWhat category would you like to see?: \n");
			ArrayList<String> categories = new ArrayList<String>();
			try{
				PreparedStatement ps = connection.prepareStatement("SELECT distinct(category) as cat FROM mutualfund");
				ResultSet rs = ps.executeQuery();
				while(rs.next())
				{
					categories.add(rs.getString("cat"));
				}
			}catch(Exception e){ e.printStackTrace(); }

			for(int i = 0; i < categories.size(); i++)
				System.out.println((i+1)+".  "+categories.get(i));
			
			System.out.println("-------------------------\n");
			int selection;

			do{
				System.out.print("Please choose a option(1-"+categories.size()+"): ");
				selection = in.nextInt();
			}while(selection > categories.size() || selection < 1);

			try{
				PreparedStatement ps = connection.prepareStatement("SELECT * FROM mutualfund where category=?");
				ps.setString(1, categories.get(selection-1));
				ResultSet rs = ps.executeQuery();
				System.out.println("Symbol\tName\t\t\tDescription\t\t\tCategory");
				System.out.println("-------------------------------------------------------------------------");
				while(rs.next())
				{
					System.out.println(rs.getString("symbol")+"\t"+rs.getString("name")+"\t\t"+rs.getString("description")+"\t\t\t"+rs.getString("category"));
				}
				System.out.println("\n\nPress 'ENTER' to continue...");
				String input = br.readLine();
			}catch(Exception e){ e.printStackTrace(); }
		}
		else if(ans==3)
		{
			try{
				PreparedStatement ps = connection.prepareStatement("SELECT * FROM mutualfund ORDER BY name ASC");
				ResultSet rs = ps.executeQuery();
				System.out.println("Symbol\tName\t\t\tDescription\t\t\tCategory");
				System.out.println("-------------------------------------------------------------------------");
				while(rs.next())
				{
					System.out.println(rs.getString("symbol")+"\t"+rs.getString("name")+"\t\t"+rs.getString("description")+"\t\t\t"+rs.getString("category"));
				}
				System.out.println("\n\nPress 'ENTER' to continue...");
				String input = br.readLine();
			}catch(Exception e) { e.printStackTrace(); }
		}
		else if(ans==4)
		{
			/* 
			* TO DO
			*/
		}
	}
	
	public void searchFunds()
	{
		System.out.print("\n\nPlease enter up to TWO keywords for the search (SPACE DELIMITED): ");
		in.nextLine();
		String input = in.nextLine();
		String[] keywords = input.split(" ");
		
		if(keywords.length ==1)
		{
			try{
				PreparedStatement ps = connection.prepareStatement("SELECT * FROM mutualfund WHERE description LIKE '%' || ? || '%'");
				ps.setString(1, keywords[0]);
				ResultSet rs = ps.executeQuery();
				System.out.println("Symbol\tName\t\t\tDescription\t\t\tCategory");
				System.out.println("-------------------------------------------------------------------------");
				while(rs.next())
				{
					System.out.println(rs.getString("symbol")+"\t"+rs.getString("name")+"\t\t"+rs.getString("description")+"\t\t\t"+rs.getString("category"));
				}
				System.out.println("\n\nPress 'ENTER' to continue...");
				String check = br.readLine();
			}catch(Exception e) { e.printStackTrace(); }
		}
		else
		{
			try{
				PreparedStatement ps = connection.prepareStatement("SELECT * FROM mutualfund WHERE description LIKE ? OR description LIKE ?");
				ps.setString(1, '%'+keywords[0]+'%'+keywords[1]+'%');
				ps.setString(2, '%'+keywords[1]+'%'+keywords[0]+'%');
				ResultSet rs = ps.executeQuery();
				System.out.println("Symbol\tName\t\t\tDescription\t\t\tCategory");
				System.out.println("-------------------------------------------------------------------------");
				while(rs.next())
				{
					System.out.println(rs.getString("symbol")+"\t"+rs.getString("name")+"\t\t"+rs.getString("description")+"\t\t\t"+rs.getString("category"));
				}
				System.out.println("\n\nPress 'ENTER' to continue...");
				String check = br.readLine();
			}catch(Exception e) { e.printStackTrace(); }
		}

	}
	
	public void investing()
	{
		System.out.print("How much would you like to deposit?: ");
		double amount = in.nextDouble();

		//Insert SQL Code
	}
	
	public static void sellShares()
	{
		boolean done = false;
		while(done == false)
		{
			System.out.print("\n\nWhat mutual fund would you like to sell your shares?: ");
			String fund = in.next();
			System.out.print("How many shares would you like to sell?: ");
			int shares = in.nextInt();

			//Insert SQL Code
		
			System.out.print("Would you like to sell more shares?(y/n): ");
			String res = in.next();
			if(res.equals("n")) { done = true; }
		}
		
	}
	
	public static void buyShares()
	{    
		boolean done = false;
		while(done == false)
		{
			System.out.print("\n\nWould you like to buy or trade shares? (buy/trade): ");
			String ans = in.next();

			if(ans.equals("buy"))
			{
				System.out.print("What mutual fund would you like to buy your shares from?: ");
				String fund = in.next();
				System.out.print("How many shares would you like to buy?: ");
				int shares = in.nextInt();
				
				//Insert SQL Code
			}
			else
			{
				//Not sure how to do trade?!?!
			}
			
			System.out.print("Would you like to buy more shares?(y/n): ");
			String res = in.next();
			if(res.equals("n")) { done = true; }
		}
		
	}
	
	public static void changePref()
	{
		boolean done = false;
		while(done == false)
		{
			//SQL check if user has any preferences.
			//If yes
			System.out.print("\n\nWhich mutual fund would you like to alter?: ");
			String fund = in.next();
			System.out.print("What new precentage would you like to apply to this fund?: ");
			double precent = in.nextDouble();
			
			
			System.out.print("Would you like to change anymore precentages?(y/n): ");
			String res = in.next();
			if(res.equals("n")) { done = true; }
			//If no
			//Show list of stocks. Allow user to select which stocks using a x y z (space delimited) to choose
			//Then prompt users (using a for loop to go through each selection) for percentages
			//Insert into Allocation and Prefers
		}
	}
	
	public static void portfolio()
	{
		//SQL code
	}
	
}