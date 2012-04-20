import java.util.*;
import java.sql.*;
import java.lang.*;
import java.io.*;

/*
TO DO: CHANGE ALL SYSDATES
*/

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
	
	public void updateShare()
	{
		try{
			connection.setAutoCommit(false);
		}catch(Exception e) { e.printStackTrace(); }

		PreparedStatement ps;
		boolean loop = true;
		while(loop)
		{
			System.out.println("Which share would you like to update?\n");
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
			}catch(Exception e){ e.printStackTrace(); }

			for(int i = 0; i < symbols.size(); i++)
				System.out.println((i+1)+".  "+symbols.get(i)+" | "+names.get(i));
			
			System.out.println("-------------------------\n");
			int selection;

			do{
				System.out.print("Please choose an appropriate symbol to update price(1-"+symbols.size()+"): ");
				selection = in.nextInt();
			}while(selection > symbols.size() || selection < 1);

			try{
				ps = connection.prepareStatement("select * from closingprice where symbol=?");
				ps.setString(1, symbols.get(selection-1));
				ResultSet rs2 = ps.executeQuery();
				if(rs2.next())
				{
					try{
						ps = connection.prepareStatement("select symbol, P_DATE from closingprice where symbol=? AND p_date=to_date(sysdate, 'DD-MON-YY')");
						ps.setString(1, symbols.get(selection-1));
						ResultSet rs = ps.executeQuery();
						if(rs.next()) //If the share has already been updated today
						{
							System.out.println("\nIt looks like this share has already been updated today.");
							System.out.println("A share's price can only be updated once a day.");
							loop = false;
							System.out.println("\n\nPress 'ENTER' to continue...");
							String input = br.readLine();
						}
						else //If the share is able to be updated today
						{
							float price=0;
							try{ //Gets current price of share
								ps = connection.prepareStatement("SELECT price FROM closingprice WHERE symbol=? ORDER BY p_date DESC");
								ps.setString(1, symbols.get(selection-1));
								ResultSet rs3 = ps.executeQuery();
								rs3.next();
								price = rs3.getFloat("price");
							}catch(Exception e){ e.printStackTrace(); }

							float newPrice=0;
							boolean check=true;
							while(check)
							{
								System.out.println("The current cost of each share is: $"+price);
								System.out.print("What would you like to update the share price to (float value accepted)?: ");
								newPrice = in.nextFloat();
								if(newPrice > 0)
								{
									ps=connection.prepareStatement("INSERT INTO closingprice VALUES(?,?,to_date(sysdate))");
									ps.setString(1, symbols.get(selection-1));
									ps.setFloat(2, newPrice);
									ps.executeUpdate();
									System.out.println("\nUpdate Successful!\n");
									check=false;
									loop=false;
									connection.commit();
									System.out.println("\n\nPress 'ENTER' to continue...");
									String input = br.readLine();
								}
								else
									System.out.println("Sorry, the value must be a non-zero positive number...");
								
							}
						}
					}catch(Exception e){ e.printStackTrace(); }
				}
				else //If the selected symbol is NOT in closingprice
				{
					float newPrice=0;
					System.out.println("\nIt looks like the selected symbol's price hasn't been set yet.");
					boolean check=true;
					while(check)
					{
						System.out.print("What would you like to set the share price to (float value accepted)?: ");
						newPrice = in.nextFloat();
						if(newPrice > 0)
						{
							ps=connection.prepareStatement("INSERT INTO closingprice VALUES(?,?,to_date(sysdate))");
							ps.setString(1, symbols.get(selection-1));
							ps.setFloat(2, newPrice);
							ps.executeUpdate();
							System.out.println("\nUpdate Successful!\n");
							check=false;
							loop=false;
							connection.commit();
							System.out.println("\n\nPress 'ENTER' to continue...");
							String input = br.readLine();
						}
						else
							System.out.println("Sorry, the value must be a non-zero positive number...");
					}
				}
			}catch(Exception e){ e.printStackTrace(); }
		}
		try{
			connection.setAutoCommit(true);
		}catch(Exception e){ e.printStackTrace(); }
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
		String[] monthsList = {"JAN", "FEB","MAR", "APR", "MAY", "JUN", "JUL","AUG", "SEP", "OCT", "NOV","DEC"};
			
		Calendar cal = Calendar.getInstance(); 
		String currMonth = monthsList[cal.get(Calendar.MONTH)];
		int year=cal.get(Calendar.YEAR);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); 
		String DOM = Integer.toString(dayOfMonth);

		System.out.print("\nHow many months back would you like to go?: ");
		int months = in.nextInt();

		while(months < 0)
		{
		System.out.print("\nHow many months back would you like to go?: ");
		months = in.nextInt();
		}

		System.out.print("Please enter the number of top investors/ categories you would like to see: ");
		int top = in.nextInt();

		while(top < 0)
		{
		System.out.print("Please enter the number of top investors/ categories you would like to see: ");
		top = in.nextInt();
		}

		int i = 0;
		while(currMonth.compareTo(monthsList[i]) != 0)
		{
		i++;
		}
		int monthIndex = i - months;
		int count = 0;
		while(monthIndex < 0) 
		{
		monthIndex = 12 - (monthIndex*-1);
		count++;
		}  
		//System.out.println("Month: " + monthsList[monthIndex]);
		year = year - count;            
		String sYear = Integer.toString(year);
		sYear = sYear.substring(2);
		//System.out.println("new year: " + sYear);

		String date = DOM + "-" + monthsList[monthIndex] + "-" + sYear;

		try
		{
		PreparedStatement ps = connection.prepareStatement("SELECT symbol, sum(amount) FROM trxlog WHERE action <> 'deposit' AND trunc(t_date) BETWEEN to_date(?) AND to_date(sysdate) GROUP BY symbol ORDER BY sum(amount) DESC");
		ps.setString(1, date);
		ResultSet rs = ps.executeQuery();
		System.out.println("\n\nCategories");
		System.out.println("Symbol\t\tSum");
		System.out.println("---------------------");
		int counter = 0;
		while(rs.next())
		{
			System.out.println(rs.getString("symbol") + "\t\t" + rs.getString("sum(amount)"));
			counter++;
			if(counter == top){break;}
		}

		ps = connection.prepareStatement("SELECT login, sum(amount) FROM trxlog WHERE action <> 'deposit' AND action <> 'sell' AND trunc(t_date) BETWEEN to_date(?) AND to_date(sysdate) GROUP BY login ORDER BY sum(amount) DESC");
		ps.setString(1, date);
		rs = ps.executeQuery();
		System.out.println("\n\nInvestors");      
		System.out.println("Login\t\tSum");
		System.out.println("---------------------");
		counter = 0;
		while(rs.next())
		{
			System.out.println(rs.getString("login") + "\t\t" + rs.getString("sum(amount)"));
			counter++;
			if(counter == top){break;}
		} 

		}catch(Exception e) { e.printStackTrace(); }
	}
}