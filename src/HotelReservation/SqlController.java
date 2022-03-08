package HotelReservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlController {
    private static final String USERNAME= "admin";
    private static final String PASSWORD= "Brentc.12";
    private static final String CONN_STRING= "jdbc:mysql://database-1.c0bp6zqdlav4.eu-west-1.rds.amazonaws.com:3306/Hotel";

    public Connection openConnection() {
        Connection conn = null;
        try {
            //load and register JDBC driver for MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD);
            System.out.println("Connected to Database!");
        }catch (SQLException se){
            System.out.println(se);
        }catch (Exception e) {
            System.out.println(e); }
        return conn;

    }

    public void closeConnection() {
        try {
            //load and register JDBC driver for MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD).close();
            System.out.println("Database Connection Closed");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }
}
