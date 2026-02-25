// ==================== ProfesseurDAO ====================
package com.essprjtjava.dao;

import com.essprjtjava.model.Professeur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesseurDAO {

    public List<Professeur> getAllProfesseurs() throws SQLException {
        List<Professeur> professeurs = new ArrayList<>();
        String query = "SELECT * FROM Professeur ORDER BY nom, prenom";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Professeur prof = new Professeur(
                        rs.getInt("idProf"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite")
                );
                professeurs.add(prof);
            }
        }
        return professeurs;
    }

    public Professeur getProfesseurById(int id) throws SQLException {
        String query = "SELECT * FROM Professeur WHERE idProf = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Professeur(
                        rs.getInt("idProf"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite")
                );
            }
        }
        return null;
    }
}