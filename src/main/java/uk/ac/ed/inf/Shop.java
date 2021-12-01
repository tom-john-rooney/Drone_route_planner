package uk.ac.ed.inf;

import java.util.*;

/**
 * A shop from which a user of the system can order lunch items.
 *
 * The functionality of this class pertains mostly to the manipulation of
 * the menus of shops.
 */
public class Shop {
    /** The name of the shop */
    public final String name;
    /** The location of the shop in the form of a what3words address. */
    public final String location;
    /** The menu of the shop containing all the items available for order from it. */
    private final ArrayList<Item> menu;

    /**
     * Constructor to initialise a new Shop instance.
     *
     * @param name the name of the shop being initialised
     * @param location the what3words address of the shop being initialised
     * @param menu the menu of the shop being initialised
     */
    public Shop(String name, String location, ArrayList<Item> menu){
        this.name = name;
        this.location = location;
        this.menu = menu;
    }

    /**
     * Converts a shop object's menu into a HashMap, with the keys being the items on the menu and the value,
     * their price.
     *
     * @return the HashMap version of the menu mapping items to prices
     */
    public HashMap<String, Integer> getPriceMap(){
        HashMap<String,Integer> priceMap = new HashMap<>();
        for(Item i: this.menu){
            priceMap.put(i.item, Integer.valueOf(i.pence));
        }
        return priceMap;
    }

    /**
     * Converts the Shop's menu into a HashMap, with the keys being the items on the menu and the value,
     * the Shop's location field.
     *
     * @return the HashMap version of the menu items to the location
     */
    public HashMap<String, String> getLocMap(){
        HashMap<String, String> locMap = new HashMap<>();
        for(Item i: this.menu){
            locMap.put(i.item, this.location);
        }
        return locMap;
    }

    /**
     * An item available for order by a user from a shop.
     *
     * The methods contained within this class work with ArrayLists of items and
     * focus on manipulating and searching them.
     */
    public static class Item{
        /** The name of the item */
        public final String item;
        /** The price of the item per unit in pence */
        public final int pence;

        /**
         * Constructor to initialise a new item instance
         *
         * @param item the name of the item to be initialised
         * @param pence the price per unit of the item in pence
         */
        public Item(String item, int pence){
            this.item = item;
            this.pence = pence;
        }
    }
}
