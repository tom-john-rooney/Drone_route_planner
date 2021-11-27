package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.ArrayList;

public class Landmarks {
    /** The directory on the web server at which the details of the no-fly-zones are stored */
    private final String LANDMARKS_DIR = "/buildings/landmarks.geojson";

    /** One geojson.Point object to represent each landmark */
    private ArrayList<Point> landmarks = new ArrayList<>();
    /** The machine on which the web server is hosted */
    public final String machine;
    /** The on the web server to which a connection needs to be made */
    public final String port;

    /**
     * Constructor to initialise a new Landmarks instance.
     *
     * @param machine the machine on which the web server is hosted
     * @param port the port on the server to which a connection needs to be made
     */
    public Landmarks(String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    public ArrayList<String> getLandmarksAddresses(){
        String url = WebServer.buildURL(this.machine, this.port, this.LANDMARKS_DIR);
        String landmarksStr = WebServer.getFrom(url);
        return GeoJsonParsing.parsePointsToW3w(landmarksStr);
    }
}
