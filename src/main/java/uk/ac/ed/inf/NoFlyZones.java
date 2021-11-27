package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;

import java.util.ArrayList;

/**
 * Represents the no-fly-zones stored on the web server.
 */
public class NoFlyZones {
    /** The directory on the web server at which the details of the no-fly-zones are stored */
    private final String ZONES_DIR = "/buildings/no-fly-zones.geojson";

    /** One geojson.Polygon object to represent each no-fly-zone */
    private ArrayList<Polygon> zones = new ArrayList<>();
    /** The machine on which the web server is hosted */
    public final String machine;
    /** The on the web server to which a connection needs to be made */
    public final String port;

    /**
     * Constructor to initialise a new NoFlyZones instance.
     *
     * @param machine the machine on which the web server is hosted
     * @param port the port on the server to which a connection needs to be made
     */
    public NoFlyZones(String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    /**
     * Gets the no-fly-zones from the web server and stores them in the zones field.
     */
    public void getZones(){
        if(this.zones.isEmpty()) {
            String url = WebServer.buildURL(this.machine, this.port, this.ZONES_DIR);
            String geoJsonStr = WebServer.getFrom(url);
            this.zones = GeoJsonParsing.parseNoFlyZones(geoJsonStr);
        }
    }

    /**
     * Checks if a What3Words.LongLat object lies in any of the no-fly-zones.
     *
     * @param point the point to be checked
     * @return true if loc lies in any of the no-fly-zones stored, false otherwise
     */
    public boolean pointInZones(What3WordsLoc.LongLat point){
        for(Polygon zone : this.zones){
            if(pointInPolygon(point, zone)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a What3Words.LongLat point lies in a particular no-fly-zone
     * @param point the point to be checked
     * @param zone the no-fly-zone in which loc may or may not lie
     * @return true if loc is inside zone, false otherwise
     */
    private static boolean pointInPolygon(What3WordsLoc.LongLat point, Polygon zone){
        Point p = Point.fromLngLat(point.lng, point.lat);
        return TurfJoins.inside(p, zone);
    }
}
