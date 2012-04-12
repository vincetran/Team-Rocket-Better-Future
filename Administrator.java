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