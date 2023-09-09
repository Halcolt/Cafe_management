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
        String location = null; // this is not use yet but this is for to know where the account is login

        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {
                        case "db.hostname":
                            hostname = value;
                            break;
                        case "db.port":
                            port = Integer.parseInt(value);
                            break;
                        case "db.database":
                            database = value;
                            break;
                        case "db.username":
                            username = value;
                            break;
                        case "db.password":
                            password = value;
                            break;
                        default:
                            // Unknown configuration key, ignore or handle as needed
                            break;
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

            // Connection successful
            System.out.println("Connected to the database successfully!");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
