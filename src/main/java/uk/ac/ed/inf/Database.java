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
    private static final String READ_ORDERS_QUERY_STR = "select * from orders where deliveryDate =(?)";
    /** Query to select the contents of an order from the orderDetails table */
    private static final String READ_ORDER_CONTENTS_QUERY_STR = "select * from orderDetails where orderNo =(?)";
    private static final String INSERT_DELIVERY_QUERY = "insert into deliveries values (? ? ?)";
    private static final String CREATE_DELIVERIES_QUERY_STR = "create table deliveries("+
            "orderNo char(8),"+
            "deliveredTo varchar(19),"+
            "costInPence int)";
    private static final String CREATE_FLIGHTPATH_QUERY_STR = "create table flightpath("+
            "orderNo char(8),"+
            "fromLongitude double,"+
            "fromLatitude double,"+
            "angle integer,"+
            "toLongitude double,"+
            "toLatitude double)";
    public static final String DELIVERIES = "deliveries";
    public static final String FLIGHTPATH = "flightpath";

    /** Address at which the database is hosted */
    private static final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";

    /**
     * Default constructor to prevent instantiation
     */
    private Database() {
    }

    /**
     * Attempts to make a connection to the database server.
     *
     * If a connection to the database at the address specified by 'jdbcString' cannot be made,
     * the exception message is printed and the application exits.
     *
     * @return the connection to the database, if successfully made
     */
    private static Connection makeConnection() {
        try {
            return DriverManager.getConnection(jdbcString);
        } catch (SQLException e) {
            System.err.println("Fatal error in 'makeConnection': " + e.getMessage());
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
     * @return the PreparedStatement object, initialised with READ_ORDERS_QUERY_STR
     */
    public static PreparedStatement buildReadOrdersQuery(String date) {
        try {
            Connection conn = makeConnection();
            PreparedStatement psReadOrdersQuery = conn.prepareStatement(READ_ORDERS_QUERY_STR);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            /* may want to move this into a parser method with its own specific try-catch? Not sure. */
            java.util.Date parsedDate = formatter.parse(date);
            psReadOrdersQuery.setDate(1, new java.sql.Date(parsedDate.getTime()));
            return psReadOrdersQuery;
        } catch (SQLException | ParseException e) {
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
    public static ArrayList<Order> readOrders(String date, Menus m) {
        try {
            PreparedStatement psReadOrdersQuery = buildReadOrdersQuery(date);
            ArrayList<Order> orderList = new ArrayList<>();
            ResultSet rs = psReadOrdersQuery.executeQuery();
            while (rs.next()) {
                String orderId = rs.getString("orderNo");
                String customerId = rs.getString("customer");
                String deliveryLoc = rs.getString("deliverTo");
                ArrayList<String> contents = readOrderContents(orderId);
                int orderValue = m.getDeliveryCost(contents.toArray(new String[0]));
                orderList.add(new Order(orderId, customerId, deliveryLoc, contents, orderValue));
            }
            return orderList;
        } catch (SQLException e) {
            System.err.println("Fatal error in 'readOrders': " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler; never actually returns.
            return null;
        }
    }

    /**
     * Builds a PreparedStatement object that will be used to query the database.
     *
     * This query will seek to read all records from the orderDetails table whose orderNo
     * is equal to the one supplied as a parameter of this method. If the PreparedObject cannot be
     * instantiated then the exception message is printed and the application closes.
     *
     * @param orderNo the order whose contents are to be read
     * @return the PreparedStatement object, initialised with READ_ORDER_CONTENTS_QUERY_STR
     */
    public static PreparedStatement buildReadOrderContentsQuery(String orderNo) {
        try {
            Connection conn = makeConnection();
            PreparedStatement psReadOrderContentsQuery = conn.prepareStatement(READ_ORDER_CONTENTS_QUERY_STR);
            psReadOrderContentsQuery.setString(1, orderNo);
            return psReadOrderContentsQuery;
        } catch (SQLException e) {
            System.err.println("Fatal error in 'buildReadOrderContentsQuery': " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler, never actually returns.
            return null;
        }
    }

    /**
     * Reads the details of the order whose number is orderNo from the orderDetails table
     *
     * If this is unsuccessful, the exception message is printed and the application
     * exits.
     *
     * @param orderNo the number of the order whose details are to be read
     * @return an ArrayList of Strings, representing the contents of the order
     */
    public static ArrayList<String> readOrderContents(String orderNo) {
        try {
            PreparedStatement psReadOrderContentsQuery = buildReadOrderContentsQuery(orderNo);
            ArrayList<String> contents = new ArrayList<>();
            ResultSet rs = psReadOrderContentsQuery.executeQuery();
            while (rs.next()) {
                String item = rs.getString("item");
                contents.add(item);
            }
            return contents;
        } catch (SQLException e) {
            System.err.println("Fatal error in 'readOrderContents': " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler; never actually returns.
            return null;
        }
    }

    public static void createTable(String name){
        try{
            Connection conn = makeConnection();
            Statement statement = conn.createStatement();
            switch(name.toLowerCase()){
                case DELIVERIES: {
                    deleteIfExists(DELIVERIES);
                    statement.execute(CREATE_DELIVERIES_QUERY_STR);
                }break;
                case FLIGHTPATH: {
                    deleteIfExists(FLIGHTPATH);
                    statement.execute(CREATE_FLIGHTPATH_QUERY_STR);
                }
            }
        } catch (SQLException e) {
            System.err.println("Fatal error in Database.createTable: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void deleteIfExists(String tableName){
        String nameLower = tableName.toLowerCase();
        String nameUpper = tableName.toUpperCase();

        try{
            Connection conn = makeConnection();
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, nameUpper, null);
            if(resultSet.next()){
                statement.execute(String.format("drop table %s", nameLower));
            }
        } catch (SQLException e) {
            System.err.println("Fatal error in Database.checkExists: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void insertDeliveries(ArrayList<Order> ordersDelivered){
        try {
            createTable(DELIVERIES);
            for (Order o : ordersDelivered) {
                buildInsertDeliveryQuery(o).execute();
            }
        } catch (SQLException e) {
            System.err.println("Fatal error in Database.insertDeliveries: " + e.getMessage());
            System.exit(1);
        }
    }

    private static PreparedStatement buildInsertDeliveryQuery(Order o){
        try{
            Connection conn = makeConnection();
            PreparedStatement psInsertDeliveryQuery = conn.prepareStatement(INSERT_DELIVERY_QUERY);
            psInsertDeliveryQuery.setString(1, o.id);
            psInsertDeliveryQuery.setString(2, o.deliveryLoc);
            psInsertDeliveryQuery.setInt(3, o.value);
            return psInsertDeliveryQuery;
        } catch (SQLException e) {
            System.err.println("Fatal error in Database.buildInsertDeliveryQuery: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }
}





