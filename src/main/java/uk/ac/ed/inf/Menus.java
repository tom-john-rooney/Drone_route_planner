package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Handles functionality relating to the menus section of the web server.
 *
 * Does not handle the parsing of the web server content once it has been
 * requested and fetched from the server, that is done by the JsonParser.
 */
public class Menus {
    /** The URL 'suffix' specifying the location of the menus content on the web server. */
    public static final String MENUS_URL = "/menus/menus.json";
    /** The standard delivery fee charged to users for every delivery made by the drone. */
    public static final int STANDARD_DELIVERY_PENCE = 50;

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
        ArrayList<ArrayList<Shop.Item>> menus = getMenus();
        for(ArrayList<Shop.Item> menu : menus){
            // Required as a binary search is used
            Shop.Item.alphabeticalSort(menu);
        }

        int deliveryCost = 0;
        for(String orderItem : orderItems){
            Shop.Item searchItem = new Shop.Item(orderItem, Shop.Item.DUMMY_PENCE);
            deliveryCost += searchItem.findItemByName(menus).pence;
        }

        return deliveryCost + STANDARD_DELIVERY_PENCE;
    }

    /**
     * Fetches the relevant web server content.
     *
     * A json file containing details of shops and their menus is retrieved from the port of the
     * web server specified by the Menus instance's fields. The parsing of this file is handled by
     * JsonParser. After parsing, the menu of each shop is extrapolated and compiled.
     *
     * @return an ArrayList of ArrayLists of Item objects i.e, an ArrayList of menus
     */
    private ArrayList<ArrayList<Shop.Item>> getMenus(){
        WebServer server = new WebServer();
        String url = server.buildURL(this.machine, this.port, MENUS_URL);
        // Contents of json file parsed to an ArrayList of shop objects
        ArrayList<Shop> shops = (ArrayList<Shop>) JsonParser.parseJsonList(server.getFrom(url));
        return Shop.compileMenus(shops);
    }


}
