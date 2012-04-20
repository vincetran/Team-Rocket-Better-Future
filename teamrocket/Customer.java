import java.util.*;
import java.sql.*;
import java.lang.*;
import java.io.*;

/*
TO DO: CHANGE ALL SYSDATES
*/

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
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		}catch(SQLException e) { e.printStackTrace(); }

		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Welcome, "+name+"!");

		try{
			PreparedStatement ps = connection.prepareStatement("SELECT balance FROM customer WHERE login=?");
			ps.setString(1, login);
			ResultSet rs1 = ps.executeQuery();
			rs1.next();
			balance = rs1.getFloat("balance");
		}catch(Exception e){ e.printStackTrace(); }
		System.out.println("\nYour current account balance is $"+balance);
		
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
			System.out.print("Enter the date you wish to browse the funds for (DD-MON-YY format): ");
			String date = in.next();

			try{
				PreparedStatement ps = connection.prepareStatement("select mutualfund.symbol, name, description, category, price, p_date "+
					"from mutualfund JOIN closingprice on mutualfund.symbol=closingprice.symbol "+
					"where trunc(p_date)=? order by price DESC");
				ps.setString(1, date);
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
			connection.commit();
			ps.close();
			System.out.println("\nYou have deposited $"+val+" into your account.");
			System.out.println("\n\nPress 'ENTER' to continue...");
			String input = br.readLine();

		}catch(Exception e) { e.printStackTrace(); }
	}
	
	public void investPrompt()
	{
		PreparedStatement ps;
		ResultSet rs;
		boolean loop=true;
		while(loop)
		{
			try
			{
				ArrayList<String> symbols = new ArrayList<String>();
				ArrayList<Float> percentages = new ArrayList<Float>();
				ArrayList<String> names = new ArrayList<String>();
				ps = connection.prepareStatement("SELECT symbol, percentage from prefers natural join(SELECT allocation_no FROM allocation WHERE p_date =(SELECT max(p_date) FROM allocation WHERE login=? GROUP BY login))");
				ps.setString(1, login);
				rs = ps.executeQuery();
				System.out.println("Your current allocation preferences are as follows:\n");
				while(rs.next())
				{
					symbols.add(rs.getString("symbol"));
					percentages.add(rs.getFloat("percentage"));

					System.out.println("Symbol:  "+rs.getString("symbol"));
					System.out.println("Percent: "+(Float.parseFloat(rs.getString("percentage"))*100)+"%\n");
				}
				ps.close();

				boolean check = true;
				while(check)
				{
					System.out.print("\nHow much would you like to invest (float value accepted; 0 to cancel)? ");
					float investAmount = in.nextFloat();
					if(investAmount > 0)
					{
						/*
						ps = connection.prepareStatement("UPDATE customer SET balance=? WHERE login=?");
						ps.setFloat(1, balance+investAmount);
						ps.setString(2, login);
						ps.executeUpdate();
						balance += investAmount;
						connection.commit();
						*/

						ps = connection.prepareStatement("INSERT into trxlog values(0, ?, NULL, (SELECT c_date FROM mutualdate), 'deposit', NULL, NULL, ?)");
						ps.setString(1, login);
						ps.setDouble(2, investAmount);
						ps.executeUpdate();
						connection.commit();

						ps = connection.prepareStatement("SELECT balance FROM customer WHERE login=?");
						ps.setString(1, login);
						rs = ps.executeQuery();
						rs.next();
						balance = rs.getFloat("balance");
						ps.close();
						System.out.println("\n$"+investAmount+" has successfully been invested into your account."+
							"\nAny excess amount will be deposited to your account balance."+
							"\nYour current balance is: $"+balance);
						
						System.out.println("\n\nPress 'ENTER' to continue...");
						String input = br.readLine();
						check=false;
						loop=false;
					}
					else if(investAmount == 0)
					{
						check=false;
						loop=false;
					}
					else
					{
						System.out.println("\nYour value is negative. Please enter an appropriate amount...\n");
					}
				}
			}catch(Exception e){ e.printStackTrace(); }
		}
	}

	public void sellShares()
	{
		PreparedStatement ps;
		ResultSet rs;
		boolean loop=true, check=true;
		while(loop)
		{
			ArrayList<String> symbols = new ArrayList<String>();
			ArrayList<Integer> number = new ArrayList<Integer>();
			System.out.println("You have own the following shares: ");
			try{
				ps = connection.prepareStatement("SELECT symbol, shares FROM owns WHERE login=?");
				ps.setString(1, login);
				rs = ps.executeQuery();
				while(rs.next())
				{
					symbols.add(rs.getString("symbol"));
					number.add(rs.getInt("shares"));
					System.out.println("\nSymbol:   "+rs.getString("symbol"));
					System.out.println("Number of Shares:   "+rs.getInt("shares"));
				}
			}catch(Exception e) { e.printStackTrace(); }

			System.out.println("-----------------------------");

			for(int i = 0; i < symbols.size(); i++)
				System.out.println((i+1)+".  "+symbols.get(i));
			System.out.println("-----------------------------\n");

			int selection=0, shares=0;

			while(check)
			{
				System.out.print("\nWhich mutual fund would you like to sell?\n");
				System.out.print("Select from 1-"+symbols.size()+":  ");
				selection = in.nextInt();
				if(selection > symbols.size() || selection <= 0)
				{
					System.out.println("You chose an improper number...\n\n");
				}
				else
					check=false;
			}
			check=true;

			while(check)
			{
				System.out.print("\nHow many shares would you like to sell? ");
				System.out.print("\nSelect from 1-"+number.get(selection-1)+":  ");
				shares = in.nextInt();
				if(shares > number.get(selection-1))
				{
					System.out.println("You chose a number greater than the number of shares you own...\n\n");
				}
				else
					check=false;
			}
			check=true;

			try{
				ps = connection.prepareStatement("SELECT price FROM closingprice WHERE symbol=? ORDER BY p_date DESC");
				ps.setString(1, symbols.get(selection-1));
				rs = ps.executeQuery();
				rs.next();
				float price = rs.getFloat("price");
				ps = connection.prepareStatement("INSERT INTO trxlog values (0, ?, ?, (SELECT c_date FROM mutualdate), 'sell', ?, ?, ?)");
				ps.setString(1, login);
				ps.setString(2, symbols.get(selection-1));
				ps.setInt(3, shares);
				ps.setFloat(4, price);
				ps.setFloat(5, shares*price);
				ps.executeUpdate();
				connection.commit();
				ps.close();
				loop=false;
				System.out.println("\n\nPress 'ENTER' to continue...");
				String input = br.readLine();
			}catch(Exception e){ e.printStackTrace(); }
			
		}	
	}
	
	public void buyShares() 
	{    
		PreparedStatement ps;
		boolean loop = true;
		while(loop)
		{
			System.out.print("\nThese are the available stocks to purchuse: \n");
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

			int option = 0;
			boolean check=true;
			while(check)
			{
				System.out.println("How would like to purchase shares?");
				System.out.println("1. By number of shares\n2. By number of dollars");
				System.out.println("-------------------------\n");
				System.out.print("Please select an option (1-2):  ");
				option = in.nextInt();
				if(option > 2 || option < 1)
				{
					System.out.println("\nYou selected an improper choice. Please select again...");
				}
				else
					check=false;
			}

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

			check = true;
			if(option == 1)
			{
				while(check)
				{
					System.out.println("\n\nThe cost of each share is: $"+price);
					System.out.println("You currently have $"+balance+" in your account balance.");
					System.out.print("How many shares would you like to purchase?: ");
					int shares = in.nextInt();
					try{
						ps = connection.prepareStatement("SELECT balance FROM customer WHERE login=?");
						ps.setString(1, login);
						ResultSet rs1 = ps.executeQuery();
						rs1.next();
						balance = rs1.getFloat("balance");

						ps = connection.prepareStatement("SELECT price FROM closingprice WHERE symbol=? ORDER BY p_date DESC");
						ps.setString(1, symbols.get(selection-1));
						rs1 = ps.executeQuery();
						rs1.next();
						price = rs1.getFloat("price");
					}catch (Exception e) { e.printStackTrace(); }

					if((shares*price) > balance)
						System.out.println("\n\nWhoa, you just tried to buy more shares than you have money for!"+
							"\nLet's try this again...");
					else{
						try{
							ps = connection.prepareStatement("INSERT INTO trxlog VALUES(?,?,?,(SELECT c_date FROM mutualdate),?,?,?,?)");
							ps.setInt(1, 1); //This will be overwritten
							ps.setString(2, login);
							ps.setString(3, symbols.get(selection-1));
							ps.setString(4, "buy");
							ps.setInt(5, shares);
							ps.setFloat(6, price);
							ps.setFloat(7, shares*price);
							ps.executeUpdate();
							connection.commit();
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
			else
			{
				while(check)
				{
					System.out.println("\n\nThe cost of each share is: $"+price);
					System.out.println("You currently have $"+balance+" in your account balance.");
					System.out.println("How much would you like to spend to buy shares (float value accepted)? ");
					float spend = in.nextFloat();

					try{
						ps = connection.prepareStatement("SELECT balance FROM customer WHERE login=?");
						ps.setString(1, login);
						ResultSet rs1 = ps.executeQuery();
						rs1.next();
						balance = rs1.getFloat("balance");

						ps = connection.prepareStatement("SELECT price FROM closingprice WHERE symbol=? ORDER BY p_date DESC");
						ps.setString(1, symbols.get(selection-1));
						rs1 = ps.executeQuery();
						rs1.next();
						price = rs1.getFloat("price");
					}catch (Exception e) { e.printStackTrace(); }

					if(spend > balance)
					{
						System.out.println("\nWhoa, you're trying to spend more than you have. Please try that again...\n");
						continue;
					}
					else{
						try{
							double calc_shares = Math.floor(spend/price);
							int shares = (int) calc_shares;
							System.out.println("\nYou will be purchasing "+shares+" shares today at $"+price+" per share.");
							float leftover = spend-(shares*price);
							if(leftover>0)
								System.out.println("\nYou have $"+leftover+" leftover that will remain in your balance.\n");
							ps = connection.prepareStatement("INSERT INTO trxlog VALUES(?,?,?,(SELECT c_date FROM mutualdate),?,?,?,?)");
							ps.setInt(1, 1); //This will be overwritten
							ps.setString(2, login);
							ps.setString(3, symbols.get(selection-1));
							ps.setString(4, "buy");
							ps.setInt(5, shares);
							ps.setFloat(6, price);
							ps.setFloat(7, shares*price);
							ps.executeUpdate();
							connection.commit();
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
					ps = connection.prepareStatement("SELECT count(*) FROM allocation WHERE login=? AND TO_CHAR(p_date, 'MM')=to_char((SELECT p_date FROM mutualdate), 'MM')");
					ps.setString(1, login);
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
			}catch(Exception e){ 
				e.printStackTrace(); }
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
					ps = connection.prepareStatement("INSERT INTO allocation VALUES(1, ?, (SELECT c_date FROM mutualdate))");
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
				connection.commit();
			}catch(Exception e){ 
				e.printStackTrace(); }
		}
	}
	
	public void portfolio() //TO DO
	{
		ArrayList<String> symbolN = new ArrayList<String>();
		ArrayList<Integer> numShare = new ArrayList<Integer>();
		ArrayList<String> actionBS = new ArrayList<String>();
		ArrayList<String> fsymbolN = new ArrayList<String>();
		ArrayList<Integer> fnumShare = new ArrayList<Integer>();
		
		System.out.print("Please enter a date you would like to start your profile on (DD-MON-YY): ");
		String report_date = in.next();
		try
		{
			String sql = "SELECT symbol, sum(num_shares), action FROM trxlog WHERE LOGIN=?  AND trunc(t_date) <= ? GROUP BY symbol, action";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, login);
			ps.setString(2, report_date);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				symbolN.add(rs.getString("symbol"));
				numShare.add(rs.getInt("sum(num_shares)"));
				actionBS.add(rs.getString("action"));
			}
			
			for(int i = 0; i < actionBS.size();i++)
			{
				if(actionBS.get(i).compareTo("buy") == 0)
				{
					int index;
					if(fsymbolN.contains(symbolN.get(i)) == false) 
					{
						fsymbolN.add(symbolN.get(i)); 
						fnumShare.add(numShare.get(i));
					}
					else
					{
						index = fsymbolN.indexOf(symbolN.get(i));
						fnumShare.set(index, (numShare.get(i) + fnumShare.get(index)));
					}
				}
				else
				{
					int index;
					if(fsymbolN.contains(symbolN.get(i)) == false) 
					{
						fsymbolN.add(symbolN.get(i)); 
						fnumShare.add(numShare.get(i)* -1);
					}
					else
					{
						index = fsymbolN.indexOf(symbolN.get(i));
						fnumShare.set(index, (fnumShare.get(index) - numShare.get(i)));
					}
				}
			}
			
			System.out.println("\nSymbol\t\tNumber of Shares\tPrice\t\tCurrent Value\t\tCost Value\t\tYield");
			System.out.println("------------------------------------------------------------------------------------------------------------------------------");
			
			int total = 0;
			for(int i = 0; i < fnumShare.size();i++)
			{
				int symPrice = 0;
				int costValue = 0;
				int costValueSell = 0;

				if(fnumShare.get(i) != 0)
				{
					sql = "SELECT price FROM closingprice WHERE trunc(p_date) = ? AND symbol = ?";
					ps = connection.prepareStatement(sql);
					ps.setString(1, report_date);
					ps.setString(2, fsymbolN.get(i));
					rs = ps.executeQuery();
					while(rs.next())
					{
						symPrice = rs.getInt("price");
					}
					
					//Used to find costValue
					sql = "SELECT num_shares, price FROM trxlog WHERE symbol = ? AND trunc(t_date) <= ? AND action = 'buy'";
					ps = connection.prepareStatement(sql);
					ps.setString(1, fsymbolN.get(i));
					ps.setString(2, report_date);
					rs = ps.executeQuery();
					while(rs.next())
					{
						costValue += rs.getInt("num_shares")*rs.getInt("price");
					}
					
					
					//Used to find costValue for sells
					sql = "SELECT num_shares, price FROM trxlog WHERE symbol = ? AND trunc(t_date) <= ? AND action = 'sell'";
					ps = connection.prepareStatement(sql);
					ps.setString(1, fsymbolN.get(i));
					ps.setString(2, report_date);
					rs = ps.executeQuery();
					while(rs.next())
					{
						costValueSell += rs.getInt("num_shares")*rs.getInt("price");
					}
					System.out.println(fsymbolN.get(i) + "\t\t" + fnumShare.get(i) + "\t\t\t" + symPrice + "\t\t" + (symPrice*fnumShare.get(i))+ "\t\t\t" + costValue + "\t\t\t" + ((symPrice*fnumShare.get(i))-(costValue-costValueSell)));
				
					total += (symPrice*fnumShare.get(i));
				}
			}
			System.out.println("\nTotal Value of Portfolio: " + total);

			System.out.println("\n\nPress 'ENTER' to continue...");
			String input = br.readLine();
		}catch(Exception e){ e.printStackTrace(); }
	}
}