package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;

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


        ArrayList<Order> orders = Database.readOrders(input_str);
        ArrayList<Shop> shops = menus.getShopsWithMenus();

        for(Order o: orders){
            w3w.getDetailsFromServer(o.deliveryLoc);
            System.out.println(o.deliveryLoc);
        }

        System.out.println("\norders done");
        for(Shop s: shops){
            w3w.getDetailsFromServer(s.location);
            System.out.println(s.location);
        }

        System.out.println("\nShops done");

    }
}
