package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * The methods in this class handle different kinds of JSON parsing using
 * Google's gson parser.
 */
public class JsonParsing {

    /** Default constructor to prevent instantiation */
    private JsonParsing() {}

    /**
     * Takes an input JSON list string and parses this to an ArrayList of objects
     * of an unspecified type.
     *
     * @param jsonShopLstStr the string containing the content to be parsed
     * @return an ArrayList of objects of an unspecified type
     */
    public static ArrayList<Shop> parseShops(String jsonShopLstStr) {
        Type listType = new TypeToken<ArrayList<Shop>>() {}.getType();
        ArrayList<Shop> parsedList = new Gson().fromJson(jsonShopLstStr, listType);
        return parsedList;
    }

    public static What3WordsLoc parseWordsDetails(String wordsDetailsStr){
        return new Gson().fromJson(wordsDetailsStr, What3WordsLoc.class);
    }
}
