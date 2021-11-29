package uk.ac.ed.inf;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All functionality relating to the words directory of the web server
 * is implemented in this class.
 */
public class Words {
    private static final int W3W_ADDRESS_LEN = 3;
    /** The words directory on the web server */
    private static final String WORDS_DIR = "/words";
    /** The name of the file storing the details of a what3words address on the web server */
    private static final String DETAILS_FILE_NAME = "details.json";
    /** A regular expression used to detect strings of the format word1.word2.word3 i.e. what3words addresses */
    private static final String W3WREGEX =
            "^/*[^0-9`~!@#$%^&*()+\\-_=\\]\\[{\\}\\\\|'<,.>?/\";:£§º©®\\s]{1,}[.｡。･・︒។։။۔።।]" +
            "[^0-9`~!@#$%^&*()+\\-_=\\]\\[{\\}\\\\|'<,.>?/\";:£§º©®\\s]{1,}[.｡。･・︒។։။۔።।]" +
            "[^0-9`~!@#$%^&*()+\\-_=\\]\\[{\\}\\\\|'<,.>?/\";:£§º©®\\s]{1,}$";
    /** A HashMap which maps every what3words address on the web server to a What3WordsLoc object */
    private HashMap<String, What3WordsLoc> wordsMap = new HashMap<>();
    private HashMap<String, Integer> edgeMap = new HashMap<String, Integer>();

    /** The machine on which the web server is hosted */
    public final String machine;
    /** The port on the web server to which a connection needs to be made */
    public final String port;

    /**
     * Constructor to initialise a new words instance
     *
     * @param machine the machine on which the web server is hosted
     * @param port the port on the web server to which a connection needs to be made
     */
    public Words(String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    /**
     * Gets the details of a what3words address from the web server.
     *
     * The details of the address (its coordinates) are fetched from the server. These
     * coordinates are then added to the wordsMap with the address as the key.
     *
     * @param w3wAddress the what3words address whose details are to be fetched and
     *                   added to the wordsMap
     */
    public void getDetailsFromServer(String w3wAddress){
        checkIsWhatThreeWordsAddress(w3wAddress);
        if(wordsMap.get(w3wAddress) == null) {
            String detailsFileDir = getDetailsDir(w3wAddress);
            String URL = WebServer.buildURL(this.machine, this.port, detailsFileDir);
            String detailsJson = WebServer.getFrom(URL);
            What3WordsLoc w3wLoc = JsonParsing.parseWordsDetails(detailsJson);
            wordsMap.put(w3wAddress, w3wLoc);
        }
    }

    /**
     * Build the directory on the web server containing the details of a what3words address.
     *
     * @param w3wAddress the address whose directory is to be build
     * @return the directory of the address
     */
    private static String getDetailsDir(String w3wAddress){
        checkIsWhatThreeWordsAddress(w3wAddress);
        String extendedInputLoc = "." + w3wAddress + ".";
        String dirSubString = extendedInputLoc.replace(".","/");
        return WORDS_DIR + dirSubString + DETAILS_FILE_NAME;
    }

    /**
     * Checks if an input string is of what3words format i.e. WORD1.WORD2.WORD3
     *
     * @param inputStr the string whose format is to be checked
     * @return true if inputStr is a what3words address, false otherwise.
     */
    public static void checkIsWhatThreeWordsAddress(String inputStr) {
        Pattern pattern = Pattern.compile(W3WREGEX);
        Matcher matcher = pattern.matcher(inputStr);

        if (!(matcher.find())) {
            System.err.println("Fatal error in Words.checkIsWhatThreeWordsAddress: Address supplied is not of what3words format.");
            System.exit(1);
        }
    }

    /**
     * Populates the edgeMap with the paths between What3WordsLoc instances.
     *
     * Iterates over every pair of entries in the wordsMap and checks if a legal, straight
     * path between them exists. If it does, an entry is added to the edgeMap with key = start_address
     * + "." + end_address and value = an ArrayList of What3WordsLoc.LongLat instances, representing
     * points to which moves are made along the path.
     *
     * @param zones a NoFlyZones object whose zones field contains all the no-fly-zones stored on the web server.
     */
    public void buildGraphFromWords(NoFlyZones zones){
        for (HashMap.Entry<String, What3WordsLoc> from : this.wordsMap.entrySet()) {
            for(HashMap.Entry<String, What3WordsLoc> to : this.wordsMap.entrySet()) {
                if (!from.equals(to)) {
                    What3WordsLoc.LongLat fromPoint = from.getValue().coordinates;
                    What3WordsLoc.LongLat toPoint = to.getValue().coordinates;
                    ArrayList<What3WordsLoc.LongLat> edge = fromPoint.getPathTo(toPoint, zones);
                    // a legal path between the 2 locations exists.
                    if (!(edge.isEmpty())) {
                        String key = from.getKey() + "." + to.getKey();
                        edgeMap.put(key, Integer.valueOf(edge.size()));
                    }
                }
            }
        }
    }

    public HashMap<String, What3WordsLoc> getWordsMap() {
        return wordsMap;
    }

    public HashMap<String, Integer> getEdgeMap() {
        return edgeMap;
    }

    public What3WordsLoc getLocOfAddr(String w3wAddr){
        checkIsWhatThreeWordsAddress(w3wAddr);
        return wordsMap.get(w3wAddr);
    }

    public static ArrayList<String> splitCombinedKey(String combinedKey){
        String[] keyWords = combinedKey.split("\\.");
        if(keyWords.length != 2*W3W_ADDRESS_LEN){
            System.err.println(String.format("Fatal error in Words.splitCombinedKey: %s is an invalid combined key.", combinedKey));
            System.exit(1);
            return null;
        }
        String keyOne = String.format("%s.%s.%s", keyWords[0], keyWords[1], keyWords[2]);
        String keyTwo = String.format("%s.%s.%s", keyWords[3], keyWords[4], keyWords[5]);
        return new ArrayList<String>(Arrays.asList(keyOne, keyTwo));

    }

    // Probably going to delete before submission so doesn't need full documentation.

    // This method converts the edge map to a GeoJson file for testing.
    // A similar idea will be needed for the final drone path map so don't delete too hastily!
    /*
    public void edgesToGeoJson() {
        ArrayList<Feature> fs = new ArrayList<>();
        for(HashMap.Entry<String, ArrayList<What3WordsLoc.LongLat>> entry : this.edgeMap.entrySet()){
            ArrayList<Point> points = new ArrayList<>();
            for(What3WordsLoc.LongLat pointAsLongLat : entry.getValue()){
                Point pointAsPoint = Point.fromLngLat(pointAsLongLat.lng, pointAsLongLat.lat);
                points.add(pointAsPoint);
            }
            LineString line = LineString.fromLngLats(points);
            Geometry g = (Geometry) line;
            Feature f = Feature.fromGeometry(g);
            fs.add(f);
        }
        FeatureCollection fc = FeatureCollection.fromFeatures(fs);
        String geoJsonStr = fc.toJson();
        try {
            FileWriter fileWriter = new FileWriter("graph.json");
            fileWriter.write(geoJsonStr);
            fileWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
     */
}
