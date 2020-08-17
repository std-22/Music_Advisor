package io.github.studio22.advisor;

import java.util.Scanner;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println( "Hi, i'm a music advisor and can tell you about new releases of this week.\n" +
                "But first, enter the command -  auth  - in the console");
        Scanner scanner = new Scanner(System.in);
        boolean auth = false;
        boolean flag = true;
        while (flag) {
            String command = scanner.nextLine();
            switch (command) {
                case "new":
                    if (auth) {
                        Print.printNewReleases();
                    } else {
                        System.out.println("Please, provide access for application.\n");
                    }
                    break;
                case "featured":
                    if (auth) {
                        Print.printFeatures();
                    } else {
                        System.out.println("Please, provide access for application.\n");
                    }
                    break;
                case "auth":
                    Server.printAuthCode();
                    Server.getAccessToken();
                    System.out.println(Server.accessToken);
                    System.out.println("SUCCESS!");
                    auth = true;
                    break;
                case "exit":
                    System.out.println("GOODBYE!");
                    flag = false;
                    break;
                default:
                    if (auth) {
                        System.out.println("Oops, wrong command, try -  new  - or -  featured  -\n");
                    } else {
                        System.out.println("Please, provide access for application.\n");
                    }
                    break;
            }
        }
    }
}