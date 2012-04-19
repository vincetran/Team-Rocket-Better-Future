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
			System.out.println("3.  Invest/Deposit");
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
				ps.close();
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
				ps.close();
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
				ps.close();
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
				ps.close();
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
				ps.close();
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
				ps.close();
				System.out.println("\n\nPress 'ENTER' to continue...");
				String check = br.readLine();
			}catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	public void investing()
	{
		boolean loop = true;
		PreparedStatement ps;
		try{
			ps = connection.prepareStatement("SELECT * FROM allocation WHERE login=? ORDER BY p_date DESC");
			ps.setString(1, login);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
			{
				while(loop){
					System.out.println("You have Allocation Preferences set up.\n"+
					"Would you like to use them when you INVEST or do you just want to DEPOSIT?"+
					"\n1. INVEST \n2. DEPOSIT \n3. CANCEL");
					System.out.println("-------------------------\n");
					System.out.print("Please choose an option (1-3): ");
					int ans = in.nextInt();
					if(ans == 1)
					{
						investPrompt();
						loop=false;
					}
					else if(ans == 2)
					{
						depositPrompt();
						loop=false;
					}
					else if(ans == 3)
					{
						loop=false;
					}
					else
						System.out.println("\nYour input was not recognized. Please try again...");
				}
			}
			else
			{
				depositPrompt();
			}
			ps.close();
		}catch(Exception e) { e.printStackTrace(); }
	}

	public void depositPrompt()
	{
		System.out.print("\n\nPlease enter the amount you would like to DEPOSIT (float value accepted): ");
		float val = in.nextFloat();
		PreparedStatement ps;

		try{
			ps = connection.prepareStatement("UPDATE customer SET balance=? WHERE login=?");
			ps.setFloat(1, balance+val);
			ps.setString(2, login);
			ps.executeUpdate();
			balance += val;
			ps.close();
			System.out.println("\nYou have deposited $"+val+" into your account.");
			System.out.println("\n\nPress 'ENTER' to continue...");
			String input = br.readLine();

		}catch(Exception e) { e.printStackTrace(); }
	}
	
	public void investPrompt()
	{
		System.out.println("Investing!");
	}

	public static void sellShares() //TO DO
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
	
	public void buyShares() //TO DO
	{    
		PreparedStatement ps;
		boolean loop = true;
		while(loop)
		{
			System.out.print("\nThese are the available stocks to purchuase: \n");
			ArrayList<String> symbols = new ArrayList<String>();
			ArrayList<String> names = new ArrayList<String>();
			try{
				ps = connection.prepareStatement("SELECT symbol, name FROM mutualfund");
				ResultSet rs = ps.executeQuery();
				while(rs.next())
				{
					symbols.add(rs.getString("symbol"));
					names.add(rs.getString("name"));
				}
				ps.close();
			}catch(Exception e){ e.printStackTrace(); }

			for(int i = 0; i < symbols.size(); i++)
				System.out.println((i+1)+".  "+symbols.get(i)+" | "+names.get(i));
			
			System.out.println("-------------------------\n");
			int selection;

			do{
				System.out.print("Please choose an appropriate symbol to purchase(1-"+symbols.size()+"): ");
				selection = in.nextInt();
			}while(selection > symbols.size() || selection < 1);

			float price=0;
			try{
				ps = connection.prepareStatement("SELECT price FROM closingprice WHERE symbol=? ORDER BY p_date DESC");
				ps.setString(1, symbols.get(selection-1));
				ResultSet rs = ps.executeQuery();
				rs.next();
				price = rs.getFloat("price");
				ps.close();
			}catch(Exception e){ 
				break;
			}

			boolean check=true;
			while(check)
			{
				System.out.println("\n\nThe cost of each share is: $"+price);
				System.out.println("You currently have $"+balance+" in your account balance.");
				System.out.print("How many shares would you like to purchase?: ");
				int shares = in.nextInt();

				if((shares*price) > balance)
					System.out.println("\n\nWhoa, you just tried to buy more shares than you have money for!"+
						"\nLet's try this again...");
				else{
					try{
						ps = connection.prepareStatement("INSERT INTO trxlog VALUES(?,?,?,to_date(sysdate),?,?,?,?)");
						ps.setInt(1, 1); //This will be overwritten
						ps.setString(2, login);
						ps.setString(3, symbols.get(selection-1));
						ps.setString(4, "buy");
						ps.setInt(5, shares);
						ps.setFloat(6, price);
						ps.setFloat(7, shares*price);
						ps.executeUpdate();
						System.out.println("\nYou have purchased "+shares+" shares of "+names.get(selection-1)+".");
						ps.close();
						System.out.println("\n\nPress 'ENTER' to continue...");
						String input = br.readLine();
						loop=false;
					}catch(Exception e){ e.printStackTrace(); }
					check=false;
				}
			}
		}
	}
	
	public void changePref()
	{
		PreparedStatement ps, ps2;
		ResultSet rs, rs2, rs3;
		boolean loop = true;
		while(loop)
		{
			ArrayList<String> symbols = new ArrayList<String>();
			ArrayList<Float> percentages = new ArrayList<Float>();
			ArrayList<String> names = new ArrayList<String>();
			try{
				ps = connection.prepareStatement("SELECT login, p_date FROM allocation WHERE login=? order by p_date");
				ps.setString(1, login);
				rs = ps.executeQuery();
				if(rs.next())
				{
					ps = connection.prepareStatement("SELECT count(*) FROM allocation WHERE login='vince' AND TO_CHAR(p_date, 'MM')=to_char((SELECT p_date FROM mutualdate), 'MM')");
					rs3 = ps.executeQuery();
					ps = connection.prepareStatement("SELECT symbol, percentage from prefers natural join(SELECT allocation_no FROM allocation WHERE p_date =(SELECT max(p_date) FROM allocation WHERE login=? GROUP BY login))");
					ps.setString(1, login);
					rs2 = ps.executeQuery();
					System.out.println("Your current allocation preferences are as follows:\n");
					while(rs2.next())
					{
						symbols.add(rs2.getString("symbol"));
						percentages.add(rs2.getFloat("percentage"));

						System.out.println("Symbol:  "+rs2.getString("symbol"));
						System.out.println("Percent: "+(Float.parseFloat(rs2.getString("percentage"))*100)+"%\n");
					}
					ps.close();

					if(rs3.next())
					{
						System.out.println("\n\nSorry, you have already updated your preferences this month.");
						System.out.println("You'll have to wait until next month to update your preferences.");
						System.out.println("\n\nPress 'ENTER' to continue...");
						String input = br.readLine();
						loop=false;
					}
					else
					{
						editPref();
						loop=false;
					}
				}
				else
				{
					System.out.println("\nYou have not set your preferences yet. Let's change that!");
					editPref();
					loop=false;
				}
			}catch(Exception e){ e.printStackTrace(); }
		}
	}

	public void editPref()
	{
		PreparedStatement ps;
		ResultSet rs, rs2;
		boolean loop = true;
		while(loop)
		{
			ArrayList<String> symbols = new ArrayList<String>();
			ArrayList<Float> percentages = new ArrayList<Float>();
			ArrayList<String> names = new ArrayList<String>();

			try
			{
				ps = connection.prepareStatement("SELECT symbol, name FROM mutualfund");
				rs = ps.executeQuery();
				System.out.println("\nFrom the list below, select all of the mutual funds you want SPACE DELIMITED:");
				while(rs.next())
				{
					symbols.add(rs.getString("symbol"));
					names.add(rs.getString("name"));
				}
				ps.close();
				for(int i = 0; i < symbols.size(); i++)
					System.out.println((i+1)+".  "+symbols.get(i)+" | "+names.get(i));
			
				System.out.println("-------------------------\n");

				System.out.print("\n\nSelect all of the mutual funds you want SPACE DELIMITED: ");
				in.nextLine();
				String input = in.nextLine();
				String[] keywords = input.split(" ");
				ArrayList<Integer> percentBreakdown = new ArrayList<Integer>();
				int totalPercent = 100;
				int selectedPercent = 0;
				boolean check;
				for(int i=0; i < keywords.length; i++)
				{
					check = true;
					while(check)
					{
						System.out.print("\n\nFor mutual fund '"+symbols.get(Integer.parseInt(keywords[i])-1)+"', how much of "+totalPercent+
							"% would you like your investments to go to: ");
						selectedPercent = in.nextInt();
						if((totalPercent-selectedPercent) < 0)
						{
							System.out.println("Hold up... you selected a percentage breakdown that would exceed 100%");
							System.out.println("Let's try this again...");
						}
						else
						{
							percentBreakdown.add(selectedPercent);
							totalPercent -= selectedPercent;
							check = false;
						}

					}
				}

				System.out.println("To confirm, this is what your allocation preferences will be...");
				System.out.println("Remember, you can only change this once a month!\n");

				for(int i=0; i < keywords.length; i++)
					System.out.println(symbols.get(Integer.parseInt(keywords[i])-1)+" | "+percentBreakdown.get(i)+"%");

				System.out.print("Is this correct (1 for YES, 2 to CANCEL entire operation)? ");
				int ans = in.nextInt();
				if(ans==1)
				{
					ps = connection.prepareStatement("INSERT INTO allocation VALUES(1, ?, to_date(sysdate))");
					ps.setString(1, login);
					ps.executeUpdate();
					ps.close();

					ps = connection.prepareStatement("SELECT allocation_no FROM allocation ORDER BY allocation_no DESC");
					rs2 = ps.executeQuery();
					ps.close();
					for(int i=0; i<keywords.length; i++)
					{
						ps = connection.prepareStatement("INSERT INTO prefers VALUES(?, ?, ?)");
						ps.setInt(1, rs2.getInt("allocation_no"));
						ps.setString(2, symbols.get(Integer.parseInt(keywords[i])-1));
						ps.setFloat(3, percentBreakdown.get(i)/100);
						ps.executeUpdate();
					}
					loop=false;
				}
				else
				{
					System.out.println("\nEnding entire operation...");
					loop=false;
				}
			}catch(Exception e){ e.printStackTrace(); }
		}
	}
	
	public void portfolio() //TO DO
	{
	}
}