package io.github.studio22.advisor;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

public class Print {

    public static void printNewReleases() throws IOException, InterruptedException {
        JsonObject albums = Server.getNewReleases();
        albums.get("items").getAsJsonArray().forEach(item -> {
            var album = item.getAsJsonObject();
            var name = album.get("name").getAsString();
            var url = album.get("external_urls").getAsJsonObject().get("spotify").getAsString();
            var artists = new ArrayList<>();
            album.get("artists").getAsJsonArray().forEach(artist -> {
                artists.add(artist.getAsJsonObject().get("name").getAsString());
            });
            System.out.println(name);
            System.out.println(artists);
            System.out.println(url);
            System.out.println();
        });
        System.out.println("if you want to close the program, enter -  exit  -");
    }

    public static void printFeatures() throws IOException, InterruptedException {
        var features = Server.getFeatures();
        var items = features.get("items").getAsJsonArray();
        items.forEach(item -> {
            var bar = item.getAsJsonObject();
            System.out.println(bar.get("name").getAsString());
            System.out.println(bar.get("owner").getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString() + "\n");
        });
        System.out.println("if you want to close the program, enter -  exit  -");
    }
}
