package project;

import java.util.Scanner;
import java.sql.Connection;
public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection conn = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DBConnection.getConnection();
            System.out.println("Connected successfully: " + conn);
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }

        RideFunctions rf = new RideFunctions(conn);
 Scanner cin = new Scanner(System.in);
        int choice;

        do {
           
            System.out.println("1. Book Ride");
            System.out.println("2. Serve Ride (FIFO)");
            System.out.println("3. Serve Highest Priority Ride (Heap)");
            System.out.println("4. Undo Last Served Ride");
            System.out.println("5. Show All Rides");
            System.out.println("6. Count Rides");
            System.out.println("7. Search Ride");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = cin.nextInt();

            switch (choice) {
                case 1: 
                	rf.book();
                	break;
                case 2:
                	rf.serve();
                	break;
                case 3:
                	rf.serveHighestPriority(); 
                	break;
                case 4:
                	rf.Undo();
                	break;
                case 5:
                	rf.showAll();
                	break;
                case 6:
                	rf.countCars();
                	break;
                case 7: 
                	rf.search();
                	break;
                case 0: 
                	System.out.println("Exit"); 
                break;
                default:
                	System.out.println("Invalid choice!");
            }

        } while (choice != 0);

        cin.close();
    }


	}


