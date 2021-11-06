package uk.ac.ed.inf;

/**
 * Represents what3words addresses.
 *
 * More information on these addresses can be found here: https://what3words.com/about/
 */
public class WhatThreeWords {
    public static final String WORDS_DIR = "/words";
    public static final String DETAILS_FILE_NAME = "details.json";

    public final String machine;
    public final String port;

    public WhatThreeWords(String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    public LongLat getDetailsFromServer(String w3wLocation){
        String detailsFileDir = getDetailsDir(w3wLocation);
        String URL = WebServer.buildURL(this.machine, this.port, detailsFileDir);
        String detailsJson = WebServer.getFrom(URL);
        return null;
    }

    private static String getDetailsDir(String w3wLocation){
        String extendedInputLoc = "." + w3wLocation + ".";
        String dirSubString = extendedInputLoc.replace(".","/");
        return WORDS_DIR + dirSubString + DETAILS_FILE_NAME;
    }
}
