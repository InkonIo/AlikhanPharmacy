package API;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MedicinesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        Map<String, Double> meds = Database.MedicineDatabase.getAllMedicines();

        StringBuilder sb = new StringBuilder();
        sb.append("{\"medicines\": [");
        boolean first = true;
        for (Map.Entry<String, Double> entry : meds.entrySet()) {
            if (!first) sb.append(",");
            sb.append("{\"name\":\"").append(entry.getKey())
                    .append("\", \"price\":").append(entry.getValue()).append("}");
            first = false;
        }
        sb.append("]}");

        byte[] responseBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
