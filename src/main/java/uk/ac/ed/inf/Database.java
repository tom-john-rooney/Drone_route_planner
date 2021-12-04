package uk.ac.ed.inf;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Methods in this class handle all functionality relating to database IO.
 */
public class Database {
    public static final String MACHINE_NAME = "localhost";
    /** Query to select all the orders from the orders table on a given date */
    private static final String READ_ORDERS_QUERY_STR = "select * from orders where deliveryDate =(?)";
    /** Query to select the contents of an order from the orderDetails table */
    private static final String READ_ORDER_CONTENTS_QUERY_STR = "select * from orderDetails where orderNo =(?)";
    /** Query to insert a record into the deliveries table */
    private static final String INSERT_DELIVERY_QUERY = "insert into deliveries values (?, ?, ?)";
    /** Query to insert a record into the flightpath table */
    private static final String INSERT_MOVE_QUERY = "insert into flightpath values (?, ?, ?, ?, ?, ?)";
    /** Query to create the deliveries table */
    private static final String CREATE_DELIVERIES_QUERY_STR = "create table deliveries("+
            "orderNo char(8),"+
            "deliveredTo varchar(19),"+
            "costInPence int)";
    /** Query to create the flight path table */
    private static final String CREATE_FLIGHTPATH_QUERY_STR = "create table flightpath("+
            "orderNo char(8),"+
            "fromLongitude double,"+
            "fromLatitude double,"+
            "angle integer,"+
            "toLongitude double,"+
            "toLatitude double)";
    /** The name of the deliveries table */
    public static final String DELIVERIES = "deliveries";
    /** The name of the flighpath table */
    public static final String FLIGHTPATH = "flightpath";
    /** Prefix of address at which database is hosted */
    private static final String JDBC_PREFIX = "jdbc:derby://";
    /** Suffix address at which database is hosted */
    private static final String JDBC_SUFFIX = "/derbyDB";
    /** The address at which the database is hosted */
    private static String jdbcString = "";

    /**
     * Default constructor to prevent instantiation
     */
    private Database() {
    }

    /**
     * Sets the address at which the database is hosted.
     *
     * @param port the port to which a connection must be made
     */
    public static void setJdcbString(String port){
        isJdbcStrSet();
        jdbcString = JDBC_PREFIX + MACHINE_NAME + ":" + port + JDBC_SUFFIX;
    }

    private static void isJdbcStrSet(){
        if(!(jdbcString.equals(""))){
            System.err.println("Fatal error in Database.isJdbcStrSet: setJdbcString must be called before using any Database class methods.");
            System.exit(1);
        }
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
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
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

    /**
     * Creates a table in the database with a specified name.
     *
     * If the table already exists, it is dropped before being created.
     *
     * @param name the name of the table to be created
     */
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

    /**
     * Deletes a database table, if it already exists.
     *
     * @param tableName the table to be deleted
     */
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

    /**
     * Inserts an ArrayList of orders into the deliveries table, one by one.
     *
     * @param ordersDelivered the orders to be inserted into the deliveries table as
     *                        records
     */
    public static void insertDeliveries(ArrayList<Delivery> ordersDelivered){
        try {
            createTable(DELIVERIES);
            for (Delivery d : ordersDelivered) {
                buildInsertDeliveryQuery(d.orderDelivered).execute();
            }
        } catch (SQLException e) {
            System.err.println("Fatal error in Database.insertDeliveries: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Inserts the moves made by the drone in delivering all orders into the flightpath table.
     *
     * Each move in 2D space has its own record inserted into the table, with records for hover moves
     * also added when the drone reaches a location it must visit to complete an order.
     *
     * @param flightPath an ArrayList of ArrayLists of What3WordsLoc.LongLat objects. Each ArrayList represents the
     *                  moves made by the drone through 2D space to complete each order it delivered
     * @param deliveriesMade the orders delivered by the drone, whose moves are specified in positions
     */
    public static void insertFlightPaths(ArrayList<ArrayList<What3WordsLoc.LongLat>> flightPath, ArrayList<Delivery> deliveriesMade){
        try{
            if(flightPath.size() != deliveriesMade.size()){
                System.err.println("Fatal error in Database.insertFlightPaths: size of positions and delivered must match.");
                System.exit(1);
            }

            createTable(FLIGHTPATH);
            System.out.println(String.format("No of position arrays: %d", flightPath.size()));
            System.out.println(String.format("No of deliveries: %d", deliveriesMade.size()));

            for(int i = 0; i < deliveriesMade.size(); i++){
                Delivery d = deliveriesMade.get(i);
                ArrayList<What3WordsLoc.LongLat> oPositions = flightPath.get(i);

                for(int j = 0; j < oPositions.size()-1; j++){
                    What3WordsLoc.LongLat start = oPositions.get(j);
                    What3WordsLoc.LongLat end = oPositions.get(j+1);
                    int bearing = start.getBearingTo(end);
                    System.out.println(start.toString());
                    System.out.println(end.toString());
                    System.out.println(bearing);
                    System.out.println("\n");

                    buildInsertMoveQuery(start, end, bearing, d.orderDelivered.id).execute();
                }
                // This code adds a hover move at the end of each order's move.
                // We don't need to hover upon returning to Appleton tower hence the if statement.
                if(!(i == deliveriesMade.size()-1)) {
                    What3WordsLoc.LongLat oFinalPositon = oPositions.get(oPositions.size() - 1);
                    buildInsertMoveQuery(oFinalPositon, oFinalPositon, What3WordsLoc.LongLat.JUNK_ANGLE, d.orderDelivered.id).execute();
                    System.out.println(oFinalPositon.toString());
                    System.out.println(oFinalPositon.toString());
                    System.out.println(What3WordsLoc.LongLat.JUNK_ANGLE);

                }
            }
        } catch (SQLException e) {
            System.err.println("Fatal error in Database.insertFlightPath: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Builds a PreparedStatement object that will be used to insert the details of a delivery made
     * into the deliveries table.
     *
     * @param o the order that has been delivered
     * @return a PreparedStatement object, initialised with the relevant details of the order
     */
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

    /**
     * Builds a PreparedStatement object that will be used to insert the details of a move made by the drone into the
     * flightpath table.
     *
     * @param start a What3Words.LongLat object, representing the point at which the drone started the move
     * @param end a What3Words.LongLat object, representing the point at which the drone ended the move
     * @param bearing the bearing from start to end
     * @param orderId the ID of the order being processed at the time of the move
     * @return a PreparedStatement object, initialised with the relevant details of the move made and the order it was
     *         made for
     */
    private static PreparedStatement buildInsertMoveQuery(What3WordsLoc.LongLat start, What3WordsLoc.LongLat end, int bearing, String orderId){
        try{
            Connection conn = makeConnection();
            PreparedStatement psInsertMoveQuery = conn.prepareStatement(INSERT_MOVE_QUERY);
            psInsertMoveQuery.setString(1, orderId);
            psInsertMoveQuery.setDouble(2, start.lng);
            psInsertMoveQuery.setDouble(3, start.lat);
            psInsertMoveQuery.setInt(4, bearing);
            psInsertMoveQuery.setDouble(5, end.lng);
            psInsertMoveQuery.setDouble(6, end.lat);
            return psInsertMoveQuery;
        } catch (SQLException e) {
            System.err.println("Fatal error in Database.buildInsertMoveQuery: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }
}





