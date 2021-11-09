package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the items for sale in participating shops
 *
 * Does not handle the parsing of the web server content once it has been
 * requested and fetched from the server, that is done by the JsonParsing.
 */
public class Menus {
    /** The URL 'suffix' specifying the location of the menus content on the web server. */
    private static final String MENUS_URL = "/menus/menus.json";
    /** The standard delivery fee charged to users for every delivery made by the drone. */
    private static final int STANDARD_DELIVERY_PENCE = 50;
    /** A HashMap storing merging all the menus of each shop in the system into one. */
    private HashMap<String, Integer> menusMap = new HashMap<>();

    /** The machine on which the web server is hosted */
    public final String machine;
    /** The port to which a connection needs to be made */
    public final String port;

    /**
     * Constructor to initialise a new menus instance.
     *
     * @param machine the machine hosting the web server on which the content relating to the
     *                new menus instance is stored
     * @param port the port to which a connection needs to be made for the new instance
     */
    public Menus(String machine, String port){
        this.machine = machine;
        this.port = port;
        this.getMenus();
    }

    /**
     * Calculates the total cost of a delivery.
     *
     * Takes a variable number of item names. The menu of each shop registered on the system is
     * searched for each item with their costs summed once found. The standard delivery fee is also
     * included in the total.
     *
     * @param orderItems the names of the items in the order
     * @return the total cost of ordering each item in 'orderItems' (including the delivery charge)
     */
    public int getDeliveryCost(String... orderItems){
        int deliveryCost = 0;
        for(String orderItem : orderItems){
            if(menusMap.get(orderItem) != null){
                deliveryCost += menusMap.get(orderItem);
            }
        }

        return deliveryCost + STANDARD_DELIVERY_PENCE;
    }

    /**
     * Fetches the details of each shop participating in the service from the web server.
     *
     * @return an ArrayList of Shop objects
     */
    public ArrayList<Shop> getShopsWithMenus(){
        String url = WebServer.buildURL(this.machine, this.port, MENUS_URL);
        // Contents of json file from web parsed to an ArrayList of shop objects
        ArrayList<Shop> shopsWithMenus = JsonParsing.parseShops(WebServer.getFrom(url));
        return shopsWithMenus;
    }

    /**
     * Fetches the menus of shops participating in the service from the web server.
     *
     * The HashMap menu of each shop is concatenated and are stored as a HashMap in the
     * combinedMenusMap field.
     */
    private void getMenus(){
        ArrayList<Shop> shops = this.getShopsWithMenus();
        for(Shop s : shops){
            HashMap<String,Integer> sMenuMap = s.hashMenu();
            menusMap.putAll(sMenuMap);
        }
    }


}
