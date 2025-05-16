package API;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleApiServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/medicines", new API.MedicinesHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("Server started on http://localhost:8000/medicines");
    }
}



