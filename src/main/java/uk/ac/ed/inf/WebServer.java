package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class WebServer {

    private static final HttpClient client = HttpClient.newHttpClient();
    public static final int OK_RESPONSE_CODE = 200;
    public static final String URL_PREFIX = "http://";

    private static WebServer instance;

    private WebServer(){}

    public static WebServer getInstance(){
        if(instance == null){
            instance = new WebServer();
        }
        return instance;
    }

    public HttpRequest buildRequest(String urlString){
        try {
            return HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        } catch(IllegalArgumentException | NullPointerException e){
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public String getFrom(String urlString){
        try{
            HttpRequest request = buildRequest(urlString);
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if(isGoodResponse(response.statusCode())){
                return response.body();
            }else{
                System.err.println("Fatal error: Bad HTTP response code.");
                System.exit(1);
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public boolean isGoodResponse(int responseCode){
        return responseCode == OK_RESPONSE_CODE;
    }

    public String buildURL(String machine, String port, String suffix){
        return URL_PREFIX + machine + ":" + port + suffix;
    }

}
