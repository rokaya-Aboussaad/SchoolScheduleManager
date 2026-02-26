package com.essprjtjava.dao;

import com.essprjtjava.model.Salle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    public List<Salle> getAllSalles() throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String query = "SELECT * FROM Salle ORDER BY nomSalle";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                salles.add(new Salle(
                        rs.getInt("idSalle"),
                        rs.getString("nomSalle")
                ));
            }
        }
        return salles;
    }

    public Salle getSalleById(int id) throws SQLException {
        String query = "SELECT * FROM Salle WHERE idSalle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Salle(rs.getInt("idSalle"), rs.getString("nomSalle"));
            }
        }
        return null;
    }

    public List<Salle> getSallesByType(String typeSalle) throws SQLException {
        return getAllSalles(); // pas de type dans la BDD
    }

    public boolean ajouterSalle(Salle salle) throws SQLException {
        String query = "INSERT INTO Salle (nomSalle) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, salle.getNomSalle());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean modifierSalle(Salle salle) throws SQLException {
        String query = "UPDATE Salle SET nomSalle = ? WHERE idSalle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, salle.getNomSalle());
            pstmt.setInt(2, salle.getIdSalle());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean supprimerSalle(int idSalle) throws SQLException {
        String query = "DELETE FROM Salle WHERE idSalle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idSalle);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<String> getTypesSalles() {
        List<String> types = new ArrayList<>();
        types.add("Cours");
        types.add("TD");
        types.add("TP");
        return types;
    }
}