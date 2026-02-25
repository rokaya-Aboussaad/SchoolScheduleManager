package com.essprjtjava.dao;

import com.essprjtjava.model.Salle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    /**
     * Récupérer toutes les salles
     */
    public List<Salle> getAllSalles() throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM Salle ORDER BY nomSalle";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Salle salle = new Salle(
                        rs.getInt("idSalle"),
                        rs.getString("nomSalle"),
                        rs.getString("typeSalle"),
                        rs.getInt("capacite")
                );
                salles.add(salle);
            }
        }
        return salles;
    }

    /**
     * Récupérer une salle par son ID
     */
    public Salle getSalleById(int id) throws SQLException {
        String query = "SELECT * FROM Salle WHERE idSalle = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Salle(
                        rs.getInt("idSalle"),
                        rs.getString("nomSalle"),
                        rs.getString("typeSalle"),
                        rs.getInt("capacite")
                );
            }
        }
        return null;
    }

    /**
     * Récupérer les salles par type
     */
    public List<Salle> getSallesByType(String typeSalle) throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM Salle WHERE typeSalle = ? ORDER BY nomSalle";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, typeSalle);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Salle salle = new Salle(
                        rs.getInt("idSalle"),
                        rs.getString("nomSalle"),
                        rs.getString("typeSalle"),
                        rs.getInt("capacite")
                );
                salles.add(salle);
            }
        }
        return salles;
    }

    /**
     * Ajouter une nouvelle salle
     */
    public boolean ajouterSalle(Salle salle) throws SQLException {
        String query = "INSERT INTO Salle (nomSalle, typeSalle, capacite) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, salle.getNomSalle());
            pstmt.setString(2, salle.getTypeSalle());
            pstmt.setInt(3, salle.getCapacite());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Modifier une salle
     */
    public boolean modifierSalle(Salle salle) throws SQLException {
        String query = "UPDATE Salle SET nomSalle = ?, typeSalle = ?, capacite = ? WHERE idSalle = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, salle.getNomSalle());
            pstmt.setString(2, salle.getTypeSalle());
            pstmt.setInt(3, salle.getCapacite());
            pstmt.setInt(4, salle.getIdSalle());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer une salle
     */
    public boolean supprimerSalle(int idSalle) throws SQLException {
        String query = "DELETE FROM Salle WHERE idSalle = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idSalle);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Obtenir les types de salles disponibles
     */
    public List<String> getTypesSalles() {
        List<String> types = new ArrayList<>();
        types.add("Cours");
        types.add("TD");
        types.add("TP");
        types.add("Amphi");
        types.add("Labo");
        return types;
    }
}