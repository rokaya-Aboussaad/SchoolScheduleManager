// ==================== SalleDAO ====================
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
                Salle salle = new Salle(
                        rs.getInt("idSalle"),
                        rs.getString("nomSalle")
                );
                salles.add(salle);
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
}