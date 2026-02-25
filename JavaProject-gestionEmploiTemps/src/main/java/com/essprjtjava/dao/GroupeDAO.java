package com.essprjtjava.dao;

import com.essprjtjava.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ==================== GroupeDAO ====================
public class GroupeDAO {

    public List<Groupe> getAllGroupes() throws SQLException {
        List<Groupe> groupes = new ArrayList<>();
        String query = "SELECT * FROM Groupe ORDER BY nomGroupe";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Groupe groupe = new Groupe(
                        rs.getInt("idGroupe"),
                        rs.getString("nomGroupe")
                );
                groupes.add(groupe);
            }
        }
        return groupes;
    }

    public Groupe getGroupeById(int id) throws SQLException {
        String query = "SELECT * FROM Groupe WHERE idGroupe = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Groupe(rs.getInt("idGroupe"), rs.getString("nomGroupe"));
            }
        }
        return null;
    }
}
