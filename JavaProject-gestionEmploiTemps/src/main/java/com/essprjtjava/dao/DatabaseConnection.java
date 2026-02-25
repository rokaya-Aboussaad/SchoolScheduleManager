package com.essprjtjava.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Paramètres de connexion pour MySQL 8.0.32
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_emploi_temps?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "rokaya"; // Mets ton mot de passe MySQL ici si tu en as un

    /**
     * Obtenir une connexion à la base de données
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // Charger le driver JDBC MySQL (pour MySQL 8.x)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données réussie!");
            System.out.println("   URL: " + URL);
            System.out.println("   User: " + USER);
            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trouvé!");
            System.err.println("   Vérifiez que mysql-connector-j est dans pom.xml");
            System.err.println("   Faites: Maven -> Reload Project");
            throw new SQLException("Driver MySQL non trouvé: " + e.getMessage(), e);

        } catch (SQLException e) {
            System.err.println("❌ ERREUR DE CONNEXION À LA BASE DE DONNÉES!");
            System.err.println("   URL: " + URL);
            System.err.println("   User: " + USER);
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Code erreur SQL: " + e.getErrorCode());
            System.err.println("");
            System.err.println("🔍 VÉRIFICATIONS À FAIRE:");
            System.err.println("   1. MySQL est-il démarré ? (Vérifiez dans Services Windows)");
            System.err.println("   2. La base 'gestion_emploi_temps' existe-t-elle ?");
            System.err.println("   3. Le mot de passe est-il correct ?");
            System.err.println("   4. Le port 3306 est-il correct ?");
            throw e;
        }
    }

    /**
     * Fermer la connexion
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion");
            e.printStackTrace();
        }
    }

    /**
     * Tester la connexion
     */
    public static boolean testConnection() {
        System.out.println("🔄 Test de connexion à MySQL...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Test de connexion RÉUSSI!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Test de connexion ÉCHOUÉ!");
            return false;
        }
        return false;
    }

    /**
     * Main pour tester la connexion
     */
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONNEXION MYSQL ===");
        testConnection();
    }
}