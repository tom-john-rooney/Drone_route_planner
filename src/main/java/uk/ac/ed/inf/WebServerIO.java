package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class WebServerIO {

    private static final HttpClient client = HttpClient.newHttpClient();
    public static final int OK_RESPONSE_CODE = 200;

    public WebServerIO(){}

    public HttpRequest buildRequest(String urlString){
        try {
            return HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        } catch(IllegalArgumentException | NullPointerException e){
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public String get(String urlString){
        try{
            HttpRequest request = buildRequest(urlString);
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if(isGoodCode(response.statusCode())){
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

    public boolean isGoodCode(int responseCode){
        return responseCode == OK_RESPONSE_CODE;
    }

}
