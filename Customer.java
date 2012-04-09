import java.util.Scanner;
import java.util.*;
import java.sql.*;
import java.lang.*;

public class Customer
{
    public static Scanner in = new Scanner(System.in);
    private static String username;

    public Customer()
    {}
    
    public Customer(String username)
    {
        this.username=username;
        boolean exit = false;
        int action;
        
        while(exit != true)
        {
            System.out.println("\n\nCustomer Options: \n");
            System.out.println("1.  Browse Mutual Funds");
            System.out.println("2.  Search");
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
    
    public static void browseFunds()
    {
        String category;
        System.out.println("\n\nPlease pick a action: ");
        System.out.println("1.  See all mutual funds");
        System.out.println("2.  See only one category");
        System.out.println("3.  Exit");
        System.out.print("\nAnswer: ");
        int ans = in.nextInt();
        
        if(ans == 1)
        {
            //Insert Sql Code 
        }
        else if(ans ==2)
        {
            System.out.print("\nWhat category would you like to see?: \n");
            category = in.next();
            
            //Insert Sql Code
            
            //Need to figure out a way to loop around if a invalid category is given
            //System.out.println("That is not an available category!");
        }      
    }
    
    public static void searchFunds()
    {
        String keyword2 = "";
        System.out.print("\n\nPlease enter a keyword for the search: ");
        String keyword1 = in.next();
        System.out.print("Would you like to use another keyword?(y/n): ");
        String ans = in.next();
        
        if(ans.equals("y"))
        {
            System.out.print("Please enter a second keyword for the search: ");
            keyword2 = in.next();
            //System.out.println("System will search on keywords: " + keyword1 + ", " + keyword2); 
            
            //Insert SQL Code
        }
        else
        {
            //Insert SQL Code
           //System.out.println("System will search on keywords: " + keyword1);        
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
            System.out.print("\n\nWhich mutual fund would you like to alter?: ");
            String fund = in.next();
            System.out.print("What new precentage would you like to apply to this fund?: ");
            double precent = in.nextDouble();
            
            
            System.out.print("Would you like to change anymore precentages?(y/n): ");
            String res = in.next();
            if(res.equals("n")) { done = true; }
        }
    }
    
    public static void portfolio()
    {
        //SQL code
    }
    
}