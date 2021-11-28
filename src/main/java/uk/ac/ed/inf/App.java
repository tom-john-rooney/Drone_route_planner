package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

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
        LocationGraph lg = new LocationGraph();
        Drone d = new Drone(What3WordsLoc.LongLat.AT_LOC);

        ArrayList<Order> orders = Database.readOrders(input_str);
        Order.sortByValue(orders);
        ArrayList<Shop> shops = menus.getShopsWithMenus();
        ArrayList<String> landmarkAddresses = landmarks.getLandmarksAddresses();


        for(Order o: orders){
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
        lg.buildGraph(w3w);

        //lg.getW3wPathFromGraph("nests.takes.print")



    }
}
