package com.essprjtjava.dao;

import com.essprjtjava.model.Matiere;
import com.essprjtjava.model.Professeur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {

    private ProfesseurDAO professeurDAO = new ProfesseurDAO();

    /**
     * Récupérer toutes les matières avec leurs professeurs
     */
    public List<Matiere> getAllMatieres() throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT m.*, p.nom, p.prenom, p.specialite " +
                "FROM Matiere m " +
                "JOIN Professeur p ON m.idProf = p.idProf " +
                "ORDER BY m.nomMatiere";

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

                Matiere matiere = new Matiere(
                        rs.getInt("idMatiere"),
                        rs.getString("nomMatiere"),
                        prof
                );
                matieres.add(matiere);
            }
        }
        return matieres;
    }

    /**
     * Récupérer une matière par son ID
     */
    public Matiere getMatiereById(int id) throws SQLException {
        String query = "SELECT m.*, p.nom, p.prenom, p.specialite " +
                "FROM Matiere m " +
                "JOIN Professeur p ON m.idProf = p.idProf " +
                "WHERE m.idMatiere = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Professeur prof = new Professeur(
                        rs.getInt("idProf"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite")
                );

                return new Matiere(
                        rs.getInt("idMatiere"),
                        rs.getString("nomMatiere"),
                        prof
                );
            }
        }
        return null;
    }

    /**
     * Récupérer les matières d'un professeur
     */
    public List<Matiere> getMatieresByProfesseur(int idProf) throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT m.*, p.nom, p.prenom, p.specialite " +
                "FROM Matiere m " +
                "JOIN Professeur p ON m.idProf = p.idProf " +
                "WHERE m.idProf = ? " +
                "ORDER BY m.nomMatiere";

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

                Matiere matiere = new Matiere(
                        rs.getInt("idMatiere"),
                        rs.getString("nomMatiere"),
                        prof
                );
                matieres.add(matiere);
            }
        }
        return matieres;
    }

    /**
     * Ajouter une nouvelle matière
     */
    public boolean ajouterMatiere(Matiere matiere) throws SQLException {
        String query = "INSERT INTO Matiere (nomMatiere, idProf) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, matiere.getNomMatiere());
            pstmt.setInt(2, matiere.getProfesseur().getIdProf());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Modifier une matière
     */
    public boolean modifierMatiere(Matiere matiere) throws SQLException {
        String query = "UPDATE Matiere SET nomMatiere = ?, idProf = ? WHERE idMatiere = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, matiere.getNomMatiere());
            pstmt.setInt(2, matiere.getProfesseur().getIdProf());
            pstmt.setInt(3, matiere.getIdMatiere());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer une matière
     */
    public boolean supprimerMatiere(int idMatiere) throws SQLException {
        String query = "DELETE FROM Matiere WHERE idMatiere = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idMatiere);
            return pstmt.executeUpdate() > 0;
        }
    }
}