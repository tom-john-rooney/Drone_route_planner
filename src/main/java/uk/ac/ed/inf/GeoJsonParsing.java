package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import java.util.ArrayList;

/**
 * The methods in the class handle different kinds of GeoJSON parsing using Mapbox's
 * GeoJSON library.
 */
public class GeoJsonParsing {

    private final static String LOCATION_PROPERTY = "location";

    /** Default constructor to prevent instantiation */
    private GeoJsonParsing(){}

    /**
     * Takes an input GeoJSON string whose FeatureCollection should only contain Features whose
     * Geometry type is Polygon. This string is parsed to an ArrayList of NoFlyZone objects.
     *
     * @param geoJsonNoFlyStr the string to be parsed
     * @return an ArrayList of NoFlyZone objects
     */
    public static ArrayList<NoFlyZone> parseNoFlyZones(String geoJsonNoFlyStr){
        FeatureCollection fc = FeatureCollection.fromJson(geoJsonNoFlyStr);
        ArrayList<NoFlyZone> noFlyLst = new ArrayList<>();
        for(Feature f : fc.features()){
            Geometry g = f.geometry();
            if(g instanceof Polygon){
                Polygon p = (Polygon) g;
                noFlyLst.add(new NoFlyZone(p));
            }else{
                System.err.println("Fatal error in parsePolygons: File contained a non-polygon Feature.");
                System.exit(1);
            }
        }
        return noFlyLst;
    }

    /**
     * Takes an input GeoJSON string whose FeatureCollection should only contain Features whose
     * properties include a field called 'location'. This string is parsed to an ArrayList of
     * what3words address strings.
     *
     * @param geoJsonPointStr the string to be parsed
     * @return an ArrayList of what3words address strings
     */
    public static ArrayList<String> parsePointsToW3w(String geoJsonPointStr){
        FeatureCollection fc = FeatureCollection.fromJson(geoJsonPointStr);
        ArrayList<String> w3wAddressLst = new ArrayList<>();
        for(Feature f : fc.features()){
            if(f.properties().has(LOCATION_PROPERTY)){
                w3wAddressLst.add(f.getStringProperty(LOCATION_PROPERTY));
            }
            else{
                System.err.println("Fatal error in GeoJsonParsing.parsePointsToW3w: Feature did not contain a property named " + LOCATION_PROPERTY);
                System.exit(1);
            }
        }
        return w3wAddressLst;
    }
}
