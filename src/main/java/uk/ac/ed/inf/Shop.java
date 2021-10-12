package uk.ac.ed.inf;

import java.util.ArrayList;

public class Shop {
    String name;
    String location;
    ArrayList<Item> menu;

    public Shop() {}

    public static class Item{
        String item;
        int pence;

        public Item() {}
    }
}
