package com.essprjtjava.dao;

import com.essprjtjava.model.Matiere;
import com.essprjtjava.model.Professeur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {

    public List<Matiere> getAllMatieres() throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        // JOIN avec Professeur via la spécialité
        String query = "SELECT m.idMatiere, m.nomMatiere, p.idProf, p.nom, p.prenom, p.specialite " +
                "FROM Matiere m " +
                "LEFT JOIN Professeur p ON m.nomMatiere = p.specialite " +
                "ORDER BY m.nomMatiere";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Professeur prof = null;
                if (rs.getInt("idProf") != 0) {
                    prof = new Professeur(
                            rs.getInt("idProf"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("specialite")
                    );
                }
                matieres.add(new Matiere(
                        rs.getInt("idMatiere"),
                        rs.getString("nomMatiere"),
                        prof
                ));
            }
        }
        return matieres;
    }

    public Matiere getMatiereById(int id) throws SQLException {
        String query = "SELECT m.idMatiere, m.nomMatiere, p.idProf, p.nom, p.prenom, p.specialite " +
                "FROM Matiere m " +
                "LEFT JOIN Professeur p ON m.nomMatiere = p.specialite " +
                "WHERE m.idMatiere = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Professeur prof = null;
                if (rs.getInt("idProf") != 0) {
                    prof = new Professeur(
                            rs.getInt("idProf"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("specialite")
                    );
                }
                return new Matiere(rs.getInt("idMatiere"), rs.getString("nomMatiere"), prof);
            }
        }
        return null;
    }

    public List<Matiere> getMatieresByProfesseur(int idProf) throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT m.idMatiere, m.nomMatiere, p.idProf, p.nom, p.prenom, p.specialite " +
                "FROM Matiere m " +
                "JOIN Professeur p ON m.nomMatiere = p.specialite " +
                "WHERE p.idProf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idProf);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Professeur prof = new Professeur(
                        rs.getInt("idProf"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite")
                );
                matieres.add(new Matiere(rs.getInt("idMatiere"), rs.getString("nomMatiere"), prof));
            }
        }
        return matieres;
    }

    public boolean ajouterMatiere(Matiere matiere) throws SQLException {
        String query = "INSERT INTO Matiere (nomMatiere) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, matiere.getNomMatiere());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean modifierMatiere(Matiere matiere) throws SQLException {
        String query = "UPDATE Matiere SET nomMatiere = ? WHERE idMatiere = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, matiere.getNomMatiere());
            pstmt.setInt(2, matiere.getIdMatiere());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean supprimerMatiere(int idMatiere) throws SQLException {
        String query = "DELETE FROM Matiere WHERE idMatiere = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idMatiere);
            return pstmt.executeUpdate() > 0;
        }
    }
}