package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * Methods in this class handle all functionality relating to the web server.
 */
public class WebServer {
    /** The single HttpClient instance that is used in communication with the server. */
    private static final HttpClient client = HttpClient.newHttpClient();
    /** The response code received from the server when a request with no errors is made */
    public static final int OK_RESPONSE_CODE = 200;
    /** Every URL the server works with begins with this */
    public static final String URL_PREFIX = "http://";

    /** Default constructor to prevent instantiation */
    private WebServer(){}

    /**
     * Builds an HttpRequest for the client instance to send to the server.
     *
     * If any exceptions are thrown when the building of the request runs, these are
     * caught and the application closes as this is an unrecoverable error.
     * The user is informed of the reason for termination in this case.
     *
     * @param urlString the URL to which the request is to be made
     * @return the request to be sent by the client if building is successful.
     */
    public static HttpRequest buildRequest(String urlString){
        try {
            return HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        } catch(IllegalArgumentException | NullPointerException e){
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler, never actually returns
            return null;
        }
    }

    /**
     * Fetches content from the web server at a specified URL.
     *
     * If the request fails, the application terminates and the user is informed of the
     * status code of the response.
     * Similarly, if any exceptions are thrown when the client sends the request these are
     * caught and the application terminates as these are unrecoverable.
     * The user is informed of the reason for termination and the exception message.
     *
     * @param urlString the URL from which content is to be fetched
     * @return the content contained on the server at the specified URL.
     */
    public static String getFrom(String urlString){
        try{
            HttpRequest request = buildRequest(urlString);
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if(isGoodResponse(response.statusCode())){
                return response.body();
            }else{
                System.err.println("Fatal error: Bad HTTP response code (" + response.statusCode() + ").");
                System.exit(1);
                // Return statement required by compiler, never actually returns
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
            // Return statement required by compiler, never actually returns
            return null;
        }
    }

    /**
     * Checks that the server's response was acceptable when the client makes a request to it.
     *
     * @param responseCode the response code from the server when the client makes the request
     * @return true if the response code is acceptable, false otherwise
     */
    private static boolean isGoodResponse(int responseCode){
        return responseCode == OK_RESPONSE_CODE;
    }

    /**
     * Builds a URL from constituent components to which the client can send a request.
     *
     * @param machine the machine on which the server is running
     * @param port the port to which a connection is to be made
     * @param suffix the location on the server where the content the client wishes to
     *               request is stored
     * @return the fully assembled URL
     */
    public static String buildURL(String machine, String port, String suffix){
        return URL_PREFIX + machine + ":" + port + suffix;
    }

}
