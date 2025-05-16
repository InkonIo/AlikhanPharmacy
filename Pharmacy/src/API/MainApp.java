package API;

import AppGUI.ContactForm;
import Database.DatabaseHelper;
import com.sun.net.httpserver.HttpServer;
import API.MedicinesHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MainApp {
    public static void main(String[] args) throws IOException {
        // 1. Инициализация базы данных
        DatabaseHelper.connect();
        DatabaseHelper.createTable();
        DatabaseHelper.insertSampleMedicines();

        // 2. Запуск HTTP-сервера
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/medicines", new MedicinesHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8000/medicines");

        // 3. Запуск GUI (отдельным потоком если нужно)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ContactForm();
        });
    }
}

