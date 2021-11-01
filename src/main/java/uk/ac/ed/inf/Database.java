package uk.ac.ed.inf;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Methods in this class handle all functionality relating to database IO.
 */
public class Database {

    /** Query to select all the orders from the orders table on a given date */
    private static final String readOrdersQueryString = "select * from orders where deliveryDate =(?)";
    /** Address at which the database is hosted */
    private static final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";

    /** Default constructor to prevent instantiation */
    private Database(){}

    /**
     * Attempts to make a connection to the database server.
     *
     * If a connection to the database at the address specified by 'jdbcString' cannot be made,
     * the exception message is printed and the application exits.
     *
     * @return the connection to the database, if successfully made
     */
    private static Connection makeConnection(){
        try{
            return DriverManager.getConnection(jdbcString);
        }catch(SQLException e){
            System.err.println("Fatal error in 'makeConnection': " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler; never actually returns.
            return null;
        }
    }

    /**
     *  Attempts to create a statement object.
     *
     *  This will be used to run various SQL statement commands against the database. If unsuccessful,
     *  the exception message is printed and the application exits.
     *
     * @return the Statement object, if successfully created
     */
    private static Statement createStatement() {
        try {
            Connection conn = makeConnection();
            return conn.createStatement();
        }catch(SQLException e){
            System.err.println("Fatal error in 'createStatement': " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler; never actually returns.
            return null;
        }
    }

    /**
     * Builds a PreparedStatement object that will be used to query the database.
     *
     * This query will seek to read all records from the orders table that were placed
     * on 'date'. If the PreparedObject cannot be instantiated then the exception message
     * is printed and the application closes.
     *
     * @param date the date on which the orders the query seeks to read were placed
     * @return the PreparedStatement object, initialised with readOrdersQueryString
     */
    public static PreparedStatement buildReadOrdersQuery(String date){
        try{
            Connection conn = makeConnection();
            PreparedStatement psReadOrdersQuery = conn.prepareStatement(readOrdersQueryString);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            /* may want to move this into a parser method with its own specific try-catch? Not sure. */
            java.util.Date parsedDate = formatter.parse(date);
            psReadOrdersQuery.setDate(1, new java.sql.Date(parsedDate.getTime()));
            return psReadOrdersQuery;
        }catch(SQLException | ParseException e){
            System.err.println("Fatal error in 'buildReadOrdersQuery': " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler, never actually returns.
            return null;
        }
    }

    /**
     * Reads the orders placed on 'date' from the orders table of the database.
     *
     * If this is unsuccessful, the exception message is printed and the application
     * exits.
     *
     * @param date the date on which the orders to be read from the orders table were placed
     * @return an ArrayList of Order objects, each representing a record read from the database
     */
    public static ArrayList<Order> readOrders(String date){
        try{
            PreparedStatement psReadOrdersQuery = buildReadOrdersQuery(date);
            ArrayList<Order> orderList = new ArrayList<>();
            ResultSet rs = psReadOrdersQuery.executeQuery();
            while(rs.next()){
                String orderId = rs.getString("orderNo");
                String customerId = rs.getString("customer");
                String deliveryLoc = rs.getString("deliverTo");
                orderList.add(new Order(orderId,customerId, deliveryLoc));
            }
            return orderList;
        } catch (SQLException e) {
            System.err.println("Fatal error in 'readOrders': " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler; never actually returns.
            return null;
        }
    }


}
