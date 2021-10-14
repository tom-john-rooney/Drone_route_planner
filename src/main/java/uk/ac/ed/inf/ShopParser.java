package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShopParser implements IParser {

    private static ShopParser instance;

    private ShopParser(){}

    public static ShopParser getInstance(){
        if(instance == null){
            instance = new ShopParser();
        }
        return instance;
    }

    @Override
    public ArrayList<?> parseJson(String jsonListString) {
        Type listType = new TypeToken<ArrayList<Shop>>() {}.getType();
        ArrayList<Shop> shopList = new Gson().fromJson(jsonListString, listType);
        return shopList;
    }
}
