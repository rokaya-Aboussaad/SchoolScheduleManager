package com.essprjtjava.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/gestion_emploi_temps?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "rokaya";

    // Obtenir une connexion
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Tester la connexion
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion réussie ✅");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion ❌");
            e.printStackTrace();
        }
    }
}
