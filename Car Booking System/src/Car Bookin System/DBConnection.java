package project;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	   private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=car_booking;encrypt=false";
	    private static final String USER = "sa";
	    private static final String PASSWORD = "123";

	    public static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection(URL, USER, PASSWORD);
	    }
}
