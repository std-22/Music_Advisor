package io.github.studio22.advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Server {

    private static String AUTH_SERVER_PATH = "https://accounts.spotify.com";
    private static String API_PATH = "https://api.spotify.com/";
    private static final String REDIRECT_URL = "http://localhost:8080";
    private static final String CLIENT_ID = "2294e9f9c8014a5eb339287fb04cd7d5";
    private static final String GET_ACCESS_TOKEN_FROM_URL = "https://accounts.spotify.com/api/token";
    private static final String CLIENT_SECRET = "1962202a1a424874bf1b889131ce2b44";
    private static final String REDIRECT_URL_IN_QUERY_STRING = "http%3A%2F%2Flocalhost%3A8080";
    private static String authCode = "";
    private static final String releasesURL = API_PATH + "v1/browse/new-releases";
    private static final String featuredURL = API_PATH + "v1/browse/featured-playlists";

    static String accessToken = "";

    public static void printAuthCode() throws IOException, InterruptedException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.start();

        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code\n", AUTH_SERVER_PATH, CLIENT_ID, REDIRECT_URL);
        System.out.println("waiting for code...");

        server.createContext("/",
                exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    String result;
                    if (query != null && query.contains("code")) {
                        authCode = query.substring(5);
                        result = "Success, return back to your program.";
                        System.out.println("Auth code: " + authCode);
                    } else {
                        result = "Not found authorization code. Try again.";
                    }
                    exchange.sendResponseHeaders(300, result.length());
                    exchange.getResponseBody().write(result.getBytes());
                    exchange.getResponseBody().close();
                    System.out.println(result);
                }
        );
        while (authCode.equals("")) {
            Thread.sleep(10);
        }
        server.stop(10);
    }

    public static String getRequestedData(String requestedFeatureURL) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(requestedFeatureURL))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static void getAccessToken() throws IOException, InterruptedException {
        HttpRequest requestAccessToken = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(
                        "client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&grant_type=" + "authorization_code"
                                + "&code=" + authCode
                                + "&redirect_uri=" + REDIRECT_URL_IN_QUERY_STRING))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(GET_ACCESS_TOKEN_FROM_URL))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> responseWithAccessToken = client.send(requestAccessToken, HttpResponse.BodyHandlers.ofString());
        accessToken = JsonParser.parseString(responseWithAccessToken.body()).getAsJsonObject().get("access_token").getAsString();
    }

    public static JsonObject getNewReleases() throws IOException, InterruptedException {
        String verboseJson = getRequestedData(releasesURL);
        return JsonParser.parseString(verboseJson).getAsJsonObject().get("albums").getAsJsonObject();
    }

    public static JsonObject getFeatures() throws IOException, InterruptedException {
        var features = getRequestedData(featuredURL);
        return JsonParser.parseString(features).getAsJsonObject().get("playlists").getAsJsonObject();
    }
}

