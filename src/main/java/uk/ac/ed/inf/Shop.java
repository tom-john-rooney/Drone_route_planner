package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Collections;

public class Shop {
    public final String name;
    public final String location;
    public final ArrayList<Item> menu;

    public Shop(String name, String location, ArrayList<Item> menu){
        this.name = name;
        this.location = location;
        this.menu = menu;
    }

    public static ArrayList<ArrayList<Shop.Item>> compileMenus(ArrayList<Shop> shops){
        ArrayList<ArrayList<Shop.Item>> menus = new ArrayList<ArrayList<Shop.Item>>();
        for(Shop shop : shops){
            menus.add(shop.menu);
        }
        return menus;
    }

    public static class Item{
        public final String item;
        public final int pence;

        public Item(String item, int pence){
            this.item = item;
            this.pence = pence;

        }

        public static void alphabeticalSort(ArrayList<Shop.Item> items){
            Collections.sort(items, (itemOne, itemTwo) -> itemOne.item.compareTo(itemTwo.item));
        }

        public int findItemCost(ArrayList<ArrayList<Shop.Item>> menus){
            for(ArrayList<Shop.Item> menu: menus){
                int binSearchRes = Collections.binarySearch(menu, this ,(itemOne, itemTwo) -> itemOne.item.compareTo(itemTwo.item));
                if(binSearchRes >= 0) {
                    return menu.get(binSearchRes).pence;
                }
            }
            return 0;
        }
    }
}
