package com.example.cafe_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Connection connect() throws IOException {
        // Read database configuration from config.txt file
        String configFilePath = "config.txt";
        String hostname = null;
        int port = 0;
        String database = null;
        String username = null;
        String password = null;
        String location = null; // this is not used yet but this is for knowing where the account is logged in

        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {
                        case "db.hostname" -> hostname = value;
                        case "db.port" -> port = Integer.parseInt(value);
                        case "db.database" -> database = value;
                        case "db.username" -> username = value;
                        case "db.password" -> password = value;
                        default -> {
                        }
                        // Unknown configuration key, ignore or handle as needed
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Create a connection URL
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

        // Establish a connection
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully!");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void disconnect(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from the database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
