package uk.ac.ed.inf;

import java.util.HashMap;
/**
 * All functionality relating to the words directory of the web server
 * is implemented in this class.
 */
public class Words {
    private static final String WORDS_DIR = "/words";
    private static final String DETAILS_FILE_NAME = "details.json";
    private HashMap<String, What3WordsLoc> wordsMap = new HashMap<>();

    public final String machine;
    public final String port;

    public Words(String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    public void getDetailsFromServer(String w3wAddress){
        if(wordsMap.get(w3wAddress) == null) {
            String detailsFileDir = getDetailsDir(w3wAddress);
            String URL = WebServer.buildURL(this.machine, this.port, detailsFileDir);
            String detailsJson = WebServer.getFrom(URL);
            What3WordsLoc w3wLoc = JsonParsing.parseWordsDetails(detailsJson);
            wordsMap.put(w3wAddress, w3wLoc);
        }
    }

    public void getWordsMapSize(){
        System.out.println(wordsMap.size());
    }

    private static String getDetailsDir(String w3wAddress){
        String extendedInputLoc = "." + w3wAddress + ".";
        String dirSubString = extendedInputLoc.replace(".","/");
        return WORDS_DIR + dirSubString + DETAILS_FILE_NAME;
    }
}
