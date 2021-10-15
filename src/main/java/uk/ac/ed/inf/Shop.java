package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A shop from which a user of the system can order lunch items.
 *
 * The functionality of this class pertains mostly to the manipulation of
 * the menus of shops.
 */
public class Shop {
    /** The name of the shop */
    public final String name;
    /** The location of the shop in the form of a what3words address. More information
     * on what3words can be found here: https://what3words.com/about/
     */
    public final String location;
    /** The menu of the shop containing all the items available for order from it. */
    public final ArrayList<Item> menu;

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
     * Takes a list of shops and compiles all their menus into one ArrayList.
     *
     * @param shops the shops whose menus are to be compiled
     * @return An ArrayList containing each menu of each shop in shops
     */
    public static ArrayList<ArrayList<Item>> compileMenus(ArrayList<Shop> shops){
        ArrayList<ArrayList<Item>> menus = new ArrayList<>();
        for(Shop shop : shops){
            menus.add(shop.menu);
        }
        return menus;
    }

    /**
     * An item available for order by a user from a shop.
     *
     * The methods contained within this class work with ArrayLists of items and
     * focus on manipulating and searching them.
     */
    public static class Item{
        /** Dummy item name value for search */
        public static final String DUMMY_ITEM = "";
        /** Dummy pence value for search */
        public static final int DUMMY_PENCE = 0;

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

        /**
         * Sorts an ArrayList of items into alphabetical order based on their names.
         *
         * @param items the list of items to be sorted
         */
        public static void alphabeticalSort(ArrayList<Item> items){
            Collections.sort(items, Comparator.comparing(itemObj -> itemObj.item));
        }

        /**
         * Takes an ArrayList of ArrayLists of items i.e, an ArrayList of shop menus,
         * searches them in turn using a binary search to see if they contain the calling
         * instance.
         *
         * @param menus an ArrayList of ArrayLists of items which is to be searched
         * @return the Item object which has the same name as the calling instance if found in
         *         any of the 'menus', a dummy item object otherwise
         */
        public Item findItemByName(ArrayList<ArrayList<Item>> menus){
            for(ArrayList<Item> menu: menus) {
                int binSearchResIndex = Collections.binarySearch(menu, this, Comparator.comparing(itemObj -> itemObj.item));
                // if found
                if (binSearchResIndex >= 0) {
                    return menu.get(binSearchResIndex);
                }
            }
            return new Item(DUMMY_ITEM,DUMMY_PENCE);
        }
    }
}
