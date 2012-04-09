
import java.util.Scanner;
import java.util.*;
import java.sql.*;
import java.lang.*;

public class TRB 
{
    /*
    private Connection connection; //used to hold the jdbc connection to the DB
    private Statement statement; //used to create an instance of the connection
    private ResultSet resultSet; //used to hold the result of your query
    private String username, password, query;
    */
    public static Scanner in = new Scanner(System.in);
    
    public static void main(String args[]) 
    {
        String username, password, query;
        Connection connection; //used to hold the jdbc connection to the DB

        username = ""; //This is your username in oracle
        password = ""; //This is your password in oracle
        try{
            // Register the oracle driver.  
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
            
            //This is the location of the database.  This is the database in oracle
            //provided to the class
            String url = "jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass"; 
            
            //create a connection to DB on db10.cs.pitt.edu
            connection = DriverManager.getConnection(url, username, password); 

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
                    query = "SELECT name FROM customer WHERE login=? AND password=?";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, userN);
                    ps.setString(2, passW);
                    ResultSet rs2 = ps.executeQuery();
                    if(rs2.next())
                    {
                        Customer cust = new Customer(rs2.getString("login"));
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
                        //Customer cust = new Customer(rs2.getString("login"));
                        System.out.println("Before");
                        Customer cust = new Customer();
                        System.out.println("After");
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



class Administrator
{
    public static Scanner in = new Scanner(System.in);
     
    public Administrator()
    {
        boolean exit = false;
        int action;
        
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
    
    public static void custRegistration()
    {
        System.out.print("\n\nPlease enter your name: ");
        String name = in.next();
        System.out.print("\n\nPlease enter your address: ");
        String adress = in.next();
        System.out.print("\n\nPlease enter your email: ");
        String email = in.next();
        System.out.print("\n\nPlease enter your preferred login name: ");
        String logName = in.next();
        System.out.print("\n\nPlease enter your preferred password: ");
        String password = in.next();
        
        //Insert SQL code
        
        System.out.println("This password is taken already.");
        System.out.println("This username is taken already.");
    }
    
    public static void updateShares()
    {
        //Not Sure what to put here?
    }
    
    public static void addMFunds()
    {
        boolean done = false;
        while(done == false)
        {
            System.out.print("\n\nWhat is the name of the new mutual fund you would like to add: ");
            String addMF = in.next();
        
            //Insert SQL code
            
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