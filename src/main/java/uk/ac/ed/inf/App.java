package uk.ac.ed.inf;


import com.mapbox.geojson.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents ties all the others together to plan the route
 * for the drone on a given day and saving the results of these calculations,
 * communicating with the web-server and database along the way.
 */
public class App
{
    /**
     * Executes the program.
     *
     * @param args the arguments needed to execute the program for a specific day in the
     *             following order; DAY MONTH YEAR WEB_SERVER_PORT DATABASE_PORT
     */
    public static void main( String[] args )
    {
        String dateStr = String.format("%s-%s-%s", args[0], args[1], args[2]);

        // Setting up objects needed.
        Menus menus = new Menus(WebServer.MACHINE, args[3]);
        Words words = new Words(WebServer.MACHINE,args[3]);
        NoFlyZones zones = new NoFlyZones(WebServer.MACHINE, args[3]);
        Landmarks landmarks = new Landmarks(WebServer.MACHINE, args[3]);
        Database.setJdcbString(args[4]);

        // Retrieving necessary data from web-server/database.
        ArrayList<Order> orders = getDaysOrders(dateStr, words, menus);
        getAllShops(menus, words);
        getAllLandmarks(landmarks, words);
        words.getDetailsFromServer(Drone.AT_W3W_ADDR);
        zones.getZones();

        // Real world locations modelled using a graph
        LocationGraph lg = new LocationGraph(words, zones);

        // Actual flight path calculations
        Drone d = new Drone(lg, words, zones);
        ArrayList<Delivery> deliveriesMade = getDeliveriesMade(orders, menus, d);
        ArrayList<ArrayList<What3WordsLoc.LongLat>> flightPath = getFlightPath(deliveriesMade, d);

        // Deliveries made and flightpath written to database.
        Database.insertDeliveries(deliveriesMade);
        // Dud delivery for the final sub-path in flight of moves back to base.
        deliveriesMade.add(new Delivery());
        Database.insertFlightPaths(flightPath, deliveriesMade);

        createGeoJsonOutput(LocationGraph.mergeSubPaths(flightPath), dateStr);
        calculateMetric(orders, deliveriesMade);
    }

    /**
     * Gets the orders stored in the database for a given day and locally stores the relevant details
     * of each order found in a Words object.
     *
     * @param date the date whose orders are to be fetched
     * @param words the words object to store the location details of the fetched orders
     * @param menus Menus object required to read orders from database
     * @return an ArrayList of Order objects representing the day's orders.
     */
    private static ArrayList<Order> getDaysOrders(String date, Words words, Menus menus){
        ArrayList<Order> orders = Database.readOrders(date, menus);
        Order.sortByValue(orders);
        for(Order o: orders){
            words.getDetailsFromServer(o.deliveryLoc);
            System.out.println(o.deliveryLoc);
        }
        System.out.println(String.format("READ %d ORDERS FROM DATABASE FOR DATE %s", orders.size(), date));
        return orders;
    }

    /**
     * Gets the details of the shops stored on the web server and stores their location details locally
     * using a Words object.
     *
     * @param menus the Menus object required to read the details of shops from the web server
     * @param words the Words object to store the location details of the fetched shops locally
     */
    private static void getAllShops(Menus menus, Words words){
        ArrayList<Shop> shops = menus.getShopsWithMenus();
        for(Shop s: shops){
            words.getDetailsFromServer(s.location);
            System.out.println(s.location);
        }
        System.out.println(String.format("READ %d SHOPS FROM WEBSERVER", shops.size()));
    }

    /**
     * Gets the details of all the landmarks stored on the web server and stores the details of their locations
     * locally using a Words object.
     *
     * @param landmarks the Landmarks object used to fetch the details of the landmarks stored on the web server
     * @param words the Words object used to store the location details of the fetched landmarks locally
     */
    private static void getAllLandmarks(Landmarks landmarks, Words words){
        ArrayList<String> landmarkAddresses = landmarks.getLandmarksAddresses();
        for(String landmarkAddress: landmarkAddresses){
            words.getDetailsFromServer(landmarkAddress);
            System.out.println(landmarkAddress);
        }
        System.out.println(String.format("READ %d LANDMARKS FROM WEBSERVER", landmarkAddresses.size()));
    }

    /**
     * Gets the deliveries that the program instructs the drone to make.
     *
     * @param orders the list of orders that the drone wishes to make
     * @param menus the Menus object used to find the locations of shops associated to each order
     * @param d the Drone object making the deliveries
     * @return an ArrayList of Delivery objects, one for each order successfully delivered by the drone
     */
    private static ArrayList<Delivery> getDeliveriesMade(ArrayList<Order> orders, Menus menus, Drone d){
        ArrayList<Delivery> deliveriesMade = new ArrayList<>();
        for(Order o: orders){
            ArrayList<What3WordsLoc.LongLat> pointsVisitedDeliveringOrder = d.makeDelivery(menus.getShopLocns(o.contents), o.deliveryLoc);
            // if order has been delivered
            if(!(pointsVisitedDeliveringOrder.isEmpty())) {
                deliveriesMade.add(new Delivery(pointsVisitedDeliveringOrder, o));
            }
        }
        return deliveriesMade;
    }

    /**
     * Gets the flight path of the drone from a list of deliveries made by it.
     *
     * @param deliveriesMade the list of deliveries made by the drone
     * @param d the drone making deliveries
     * @return An ArrayList of ArrayLists of What3Words.LongLat objects. The ArrayList as a whole
     *         represents the flight path as a whole, with each nested ArrayList representing a sub-path
     *         within the flight path. Please see the class documentation of LocationGraph for clarity on
     *         path/sub-path terminology.
     */
    private static ArrayList<ArrayList<What3WordsLoc.LongLat>> getFlightPath(ArrayList<Delivery> deliveriesMade, Drone d){
        ArrayList<ArrayList<What3WordsLoc.LongLat>> flightPathPoints = new ArrayList<>();
        for(Delivery delivery: deliveriesMade){
            flightPathPoints.add(delivery.pointsVisited);
        }
        // After all deliveries are made, flight path must guide drone back to its base.
        flightPathPoints.add(d.returnToBase());
        return flightPathPoints;
    }

    /**
     * Creates a GeoJSON file containing a single Feature, to visualise the program's flight path for the drone.
     *
     * This file is placed at the top level of the project's file structure.
     *
     * @param mergedFlightPath the flight path of the drone as a single path, instead of an ArrayList of sub-paths.
     *                         See the class documentation of the LocationGraph class for clarity on path/sub-path
     *                         terminology.
     * @param dateStr the date on which the drone is operating, in the format DD-MM-YYYY
     */
    private static void createGeoJsonOutput(ArrayList<What3WordsLoc.LongLat> mergedFlightPath, String dateStr){
        ArrayList<Point> points = new ArrayList<>();
        for(What3WordsLoc.LongLat loc : mergedFlightPath){
            Point pointAsPoint = Point.fromLngLat(loc.lng, loc.lat);
            points.add(pointAsPoint);
        }
        LineString line = LineString.fromLngLats(points);
        Geometry g = (Geometry) line;
        Feature f = Feature.fromGeometry(g);
        FeatureCollection fc = FeatureCollection.fromFeature(f);
        String geoJsonStr = fc.toJson();
        try {
            FileWriter fileWriter = new FileWriter(String.format("drone-%s.geojson", dateStr));
            fileWriter.write(geoJsonStr);
            fileWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateMetric(ArrayList<Order> orders, ArrayList<Delivery> deliveries){
        int oTotal = 0;
        for(Order o : orders){
            //System.out.println(o.value);
            oTotal += o.value;
        }
        int dTotal = 0;
        System.out.println("\n");
        for(Delivery d: deliveries){
            //System.out.println(d.orderDelivered.value);
            dTotal += d.orderDelivered.value;
        }
        System.out.println(oTotal);
        System.out.println(dTotal);
    }
}
