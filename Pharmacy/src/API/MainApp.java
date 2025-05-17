package API;

import AppGUI.ContactForm;
import Database.DatabaseHelper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MainApp {
    public static void main(String[] args) throws IOException {
        DatabaseHelper.connect();
        DatabaseHelper.createTable();
        DatabaseHelper.insertSampleMedicines();

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/medicines", new MedicinesHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8000/medicines");


        javax.swing.SwingUtilities.invokeLater(() -> {
            new ContactForm();
        });
    }
}

