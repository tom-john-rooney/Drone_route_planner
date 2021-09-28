package uk.ac.ed.inf;

/**
 * Represents a coordinate pair.
 *
 * These coordinates consist of 2 components; a longitude and a latitude. They
 * are of the form (longitude, latitude).
 */
public class LongLat {
    /** The longitude component of the coordinate */
    public double longitude;

    /** The latitude component of the coordinate */
    public double latitude;

    /**
     * Initialises a new LongLat instance.
     *
     * @param longitude the longitude component of the coordinate being initialised
     * @param latitude  the latitude component of the coordinate being initialised
     */
    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
