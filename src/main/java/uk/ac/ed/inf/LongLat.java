package uk.ac.ed.inf;

/**
 * Represents a coordinate pair.
 *
 * These coordinates consist of 2 components; a longitude and a latitude.
 * They are of the form (longitude, latitude).
 */
public class LongLat {
    /** The value which all longitudes must be strictly less than */
    public static final double MAX_LONGITUDE = -3.184319;
    /** The value which all longitudes must be strictly greater than */
    public static final double MIN_LONGITUDE = -3.192473;
    /** The value which all latitudes must be strictly less than */
    public static final double MAX_LATITUDE = 55.946233;
    /** The value which all longitudes must be strictly greater than */
    public static final double MIN_LATITUDE = 55.942617;
    /** The maximum distance allowed in degrees between two points for them to be 'close to' one another */
    public static final double DISTANCE_TOLERANCE = 0.00015;
    /** The number which any drone move's direction in degrees must be a scalar multiple of */
    public static final int ANGLE_SCALE = 10;
    /** The largest bearing on which the drone can move */
    public static final int MAX_ANGLE = 350;
    /** The smallest bearing on which the drone can move */
    public static final int MIN_ANGLE = 0;
    /** The special 'junk' value to represent when the drone is hovering */
    public static final int JUNK_ANGLE = -999;
    /** The length of any move the drone makes in degrees */
    public static final double MOVE_DISTANCE = 0.00015;

    /** The longitude component of the coordinate */
    public final double lng;
    /** The latitude component of the coordinate */
    public final double lat;

    /**
     * Constructor to initialise a new LongLat instance.
     *
     * @param lng the longitude component of the coordinate being initialised
     * @param lat the latitude component of the coordinate being initialised
     */
    public LongLat(double lng, double lat){
        this.lng = lng;
        this.lat = lat;
    }

    @Override
    public String toString(){
        return "longitude: " + this.lng + "\n" + "latitude: " + this.lat;
    }

    /**
     * Checks if a LongLat instance lies within the confinement area.
     *
     * This means is lies in the area defined by MAX_LONGITUDE, MIN_LONGITUDE,
     * MAX_LATITUDE, and MIN_LATITUDE.
     * This area strict i.e, being on the boundary equates to being outside it.
     *
     * @return true if coordinate lies within the confinement area, false otherwise
     */
    public boolean isConfined(){
        boolean lngConfined = (lng < MAX_LONGITUDE) && (lng > MIN_LONGITUDE);
        boolean latConfined = (lat < MAX_LATITUDE) && (lat > MIN_LATITUDE);

        return lngConfined && latConfined;
    }

    /**
     * Calculates the Pythagorean distance between 2 LongLat instances.
     *
     * @param point the point to which the Pythagorean distance is calculated
     * @return the Pythagorean distance between the calling instance and point in degrees
     */
    public double distanceTo(LongLat point){
        isPointNull(point);
        return Math.hypot(this.lng - point.lng, this.lat - point.lat);
    }

    /**
     * Determines if one LongLat instance is 'close to' another.
     *
     * This means that the two instances are within DISTANCE_TOLERANCE degrees of each other.
     *
     * @param point the point which may or may not be 'close to' the calling instance
     * @return true if the instance calling the method is 'close to' point, false otherwise
     */
    public boolean closeTo(LongLat point){
        isPointNull(point);
        return this.distanceTo(point) < DISTANCE_TOLERANCE;
    }

    /**
     * Calculates the next position of the drone after a move.
     *
     * This method assumes that the two points lie in a 2D plane, not on a sphere.
     * It also adopts the convention that East = 0 degrees, North = 90 etc.
     *
     * @param angle the direction in which the move is to be made in degrees
     * @return a LongLat object representing the longitude and latitude of the drone
     *         after the move, this object is null if the angle is not a multiple of ANGLE_SCALE
     *         or lies outside the range [MIN_ANGLE,MAX_ANGLE]
     */
    public LongLat nextPosition(int angle){
        if(angle == JUNK_ANGLE){
            return new LongLat(this.lng, this.lat);
        }
        else if(!(angle % ANGLE_SCALE == 0) || angle > MAX_ANGLE || angle < MIN_ANGLE){
            return null;
        }
        else{
            double newlng = this.lng + MOVE_DISTANCE * Math.cos(Math.toRadians(angle));
            double newlat = this.lat + MOVE_DISTANCE * Math.sin(Math.toRadians(angle));
            return new LongLat(newlng, newlat);
        }
    }

    /**
     * Handles null coordinates.
     *
     * Does so by exiting the application and displaying an error message in the case that
     * the input coordinate is null.
     * This method is to be used as a helper by other methods within this class to help with
     * input validation.
     *
     * @param point the point being checked for null status
     */
    private void isPointNull(LongLat point){
        if(point == null){
            System.err.println("Fatal error: null coordinate");
            System.exit(1);
        }
    }
}
