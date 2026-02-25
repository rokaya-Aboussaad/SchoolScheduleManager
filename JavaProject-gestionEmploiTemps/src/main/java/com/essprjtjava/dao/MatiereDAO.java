// ==================== MatiereDAO ====================
package com.essprjtjava.dao;

import com.essprjtjava.model.Matiere;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {

    public List<Matiere> getAllMatieres() throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT * FROM Matiere ORDER BY nomMatiere";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Matiere matiere = new Matiere(
                        rs.getInt("idMatiere"),
                        rs.getString("nomMatiere")
                );
                matieres.add(matiere);
            }
        }
        return matieres;
    }

    public Matiere getMatiereById(int id) throws SQLException {
        String query = "SELECT * FROM Matiere WHERE idMatiere = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Matiere(rs.getInt("idMatiere"), rs.getString("nomMatiere"));
            }
        }
        return null;
    }

    public Matiere getMatiereByNom(String nomMatiere) throws SQLException {
        String query = "SELECT * FROM Matiere WHERE nomMatiere = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nomMatiere);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Matiere(rs.getInt("idMatiere"), rs.getString("nomMatiere"));
            }
        }
        return null;
    }
}
