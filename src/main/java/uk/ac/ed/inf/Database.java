package uk.ac.ed.inf;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Methods in this class handle all functionality relating to database IO.
 */
public class Database {

    private static final String readOrdersQueryString = "select * from orders where deliveryDate =(?)";
    private static final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";

    /** Default constructor to prevent instantiation */
    private Database(){}

    private static Connection makeConnection(){
        try{
            return DriverManager.getConnection(jdbcString);
        }catch(SQLException e){
            System.err.println("Fatal error in 'makeConnection': " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    private static Statement createStatement() {
        try {
            Connection conn = makeConnection();
            return conn.createStatement();
        }catch(SQLException e){
            System.err.println("Fatal error in 'createStatement': " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public static PreparedStatement buildReadOrdersQuery(String date){
        try{
            Connection conn = makeConnection();
            PreparedStatement psReadOrdersQuery = conn.prepareStatement(readOrdersQueryString);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date parsedDate = formatter.parse(date);
            psReadOrdersQuery.setDate(1, new java.sql.Date(parsedDate.getTime()));
            return psReadOrdersQuery;
        }catch(SQLException | ParseException e){
            System.err.println("Fatal error in 'buildReadOrdersQuery': " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

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
            return null;
        }
    }


}
