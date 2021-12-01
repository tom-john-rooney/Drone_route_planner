package uk.ac.ed.inf;


import com.mapbox.geojson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println("Please enter the date in format dd/mm/yyyy: ");
        Scanner in = new Scanner(System.in);
        String input_str = in.nextLine();
        Menus menus = new Menus("localhost", "9898");
        Words w3w = new Words("localhost","9898");
        NoFlyZones zones = new NoFlyZones("localhost", "9898");
        Landmarks landmarks = new Landmarks("localhost", "9898");

        ArrayList<Order> orders1 = Database.readOrders(input_str, menus);
        Order.sortByValue(orders1);
        ArrayList<Shop> shops = menus.getShopsWithMenus();
        ArrayList<String> landmarkAddresses = landmarks.getLandmarksAddresses();

        w3w.getDetailsFromServer(Drone.AT_W3W_ADDR);

        for(Order o: orders1){
            w3w.getDetailsFromServer(o.deliveryLoc);
            System.out.println(o.deliveryLoc);
        }
        System.out.println("orders done\n");

        for(Shop s: shops){
            w3w.getDetailsFromServer(s.location);
            System.out.println(s.location);
        }
        System.out.println("shops done\n");

        for(String lstr: landmarkAddresses){
            w3w.getDetailsFromServer(lstr);
            System.out.println(lstr);
        }
        System.out.println("landmarks done\n");

        zones.getZones();
        w3w.buildGraphFromWords(zones);
        LocationGraph lg = new LocationGraph(w3w);
        Drone d = new Drone(lg, w3w, zones);

        String startLoc = "nests.takes.print";
        ArrayList<String> locs = new ArrayList<>();
        locs.add("sketch.spill.puzzle");
        locs.add("pest.round.peanut");
        String endLoc = "linked.pads.cigar";

        ArrayList<ArrayList<What3WordsLoc.LongLat>> allLocs = new ArrayList<>();
        ArrayList<Order> delivered = new ArrayList<>();


        for(Order o : orders1){
            ArrayList<What3WordsLoc.LongLat> deliveryPathLocs =  d.makeDelivery(menus.getShopLocns(o.contents), o.deliveryLoc);
            if(!(deliveryPathLocs.isEmpty())) {
                allLocs.add(deliveryPathLocs);
                delivered.add(o);
            }
        }
        allLocs.add(d.returnToBase());
        Database.insertDeliveries(delivered);

        // need to add a dud order for the final ArrayList in allLocs of moves back to base.
        delivered.add(new Order());
        Database.insertFlightPaths(allLocs, delivered);

        ArrayList<What3WordsLoc.LongLat> allLocsAsOne = new ArrayList<>();
        for(ArrayList<What3WordsLoc.LongLat> orderPositons : allLocs){
            allLocsAsOne.addAll(orderPositons);
        }
        ArrayList<Feature> fs = new ArrayList<>();

        ArrayList<Point> points = new ArrayList<>();
        for(What3WordsLoc.LongLat loc : allLocsAsOne){
            Point pointAsPoint = Point.fromLngLat(loc.lng, loc.lat);
            points.add(pointAsPoint);
        }
        LineString line = LineString.fromLngLats(points);
        Geometry g = (Geometry) line;
        Feature f = Feature.fromGeometry(g);
        fs.add(f);
        FeatureCollection fc = FeatureCollection.fromFeatures(fs);
        String geoJsonStr = fc.toJson();
        try {
            FileWriter fileWriter = new FileWriter("graph-test.json");
            fileWriter.write(geoJsonStr);
            fileWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }




}
