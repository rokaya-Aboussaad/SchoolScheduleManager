package com.essprjtjava.dao;

import com.essprjtjava.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    private GroupeDAO groupeDAO = new GroupeDAO();
    private ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private MatiereDAO matiereDAO = new MatiereDAO();
    private SalleDAO salleDAO = new SalleDAO();

    /**
     * Ajouter une nouvelle séance
     */
    public boolean ajouterSeance(Seance seance) throws SQLException {
        String query = "INSERT INTO Seance (jour, heureDebut, heureFin, idProf, idGroupe, idMatiere, idSalle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, seance.getJour());
            pstmt.setTime(2, seance.getHeureDebut());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setInt(4, seance.getProfesseur().getIdProf());
            pstmt.setInt(5, seance.getGroupe().getIdGroupe());
            pstmt.setInt(6, seance.getMatiere().getIdMatiere());
            pstmt.setInt(7, seance.getSalle().getIdSalle());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Récupérer toutes les séances
     */
    public List<Seance> getAllSeances() throws SQLException {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM Seance ORDER BY " +
                "FIELD(jour, 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'), heureDebut";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Seance seance = new Seance();
                seance.setIdSeance(rs.getInt("idSeance"));
                seance.setJour(rs.getString("jour"));
                seance.setHeureDebut(rs.getTime("heureDebut"));
                seance.setHeureFin(rs.getTime("heureFin"));
                seance.setProfesseur(professeurDAO.getProfesseurById(rs.getInt("idProf")));
                seance.setGroupe(groupeDAO.getGroupeById(rs.getInt("idGroupe")));
                seance.setMatiere(matiereDAO.getMatiereById(rs.getInt("idMatiere")));
                seance.setSalle(salleDAO.getSalleById(rs.getInt("idSalle")));
                seances.add(seance);
            }
        }
        return seances;
    }

    /**
     * Récupérer les séances d'un groupe
     */
    public List<Seance> getSeancesByGroupe(int idGroupe) throws SQLException {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM Seance WHERE idGroupe = ? ORDER BY " +
                "FIELD(jour, 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'), heureDebut";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idGroupe);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Seance seance = new Seance();
                seance.setIdSeance(rs.getInt("idSeance"));
                seance.setJour(rs.getString("jour"));
                seance.setHeureDebut(rs.getTime("heureDebut"));
                seance.setHeureFin(rs.getTime("heureFin"));
                seance.setProfesseur(professeurDAO.getProfesseurById(rs.getInt("idProf")));
                seance.setGroupe(groupeDAO.getGroupeById(rs.getInt("idGroupe")));
                seance.setMatiere(matiereDAO.getMatiereById(rs.getInt("idMatiere")));
                seance.setSalle(salleDAO.getSalleById(rs.getInt("idSalle")));
                seances.add(seance);
            }
        }
        return seances;
    }

    /**
     * Récupérer les séances d'un professeur
     */
    public List<Seance> getSeancesByProfesseur(int idProf) throws SQLException {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM Seance WHERE idProf = ? ORDER BY " +
                "FIELD(jour, 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'), heureDebut";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idProf);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Seance seance = new Seance();
                seance.setIdSeance(rs.getInt("idSeance"));
                seance.setJour(rs.getString("jour"));
                seance.setHeureDebut(rs.getTime("heureDebut"));
                seance.setHeureFin(rs.getTime("heureFin"));
                seance.setProfesseur(professeurDAO.getProfesseurById(rs.getInt("idProf")));
                seance.setGroupe(groupeDAO.getGroupeById(rs.getInt("idGroupe")));
                seance.setMatiere(matiereDAO.getMatiereById(rs.getInt("idMatiere")));
                seance.setSalle(salleDAO.getSalleById(rs.getInt("idSalle")));
                seances.add(seance);
            }
        }
        return seances;
    }

    /**
     * Vérifier conflit de salle (vérifie TOUS les groupes)
     * Une salle ne peut pas être utilisée par 2 groupes en même temps
     */
    public boolean verifierConflitSalle(Seance seance) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idSalle = ? AND jour = ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getSalle().getIdSalle());
            pstmt.setString(2, seance.getJour());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setTime(4, seance.getHeureDebut());
            pstmt.setTime(5, seance.getHeureFin());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureDebut());
            pstmt.setTime(8, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Vérifier conflit de professeur (vérifie TOUS les groupes)
     * Un professeur ne peut pas enseigner à 2 groupes en même temps
     */
    public boolean verifierConflitProfesseur(Seance seance) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idProf = ? AND jour = ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getProfesseur().getIdProf());
            pstmt.setString(2, seance.getJour());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setTime(4, seance.getHeureDebut());
            pstmt.setTime(5, seance.getHeureFin());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureDebut());
            pstmt.setTime(8, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Vérifier conflit de groupe (vérifie uniquement le groupe concerné)
     * Un groupe ne peut pas avoir 2 séances en même temps
     */
    public boolean verifierConflitGroupe(Seance seance) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idGroupe = ? AND jour = ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getGroupe().getIdGroupe());
            pstmt.setString(2, seance.getJour());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setTime(4, seance.getHeureDebut());
            pstmt.setTime(5, seance.getHeureFin());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureDebut());
            pstmt.setTime(8, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Obtenir les professeurs disponibles pour un créneau donné
     */
    public List<Professeur> getProfesseursDisponibles(String jour, Time heureDebut, Time heureFin) throws SQLException {
        String query = "SELECT DISTINCT p.* FROM Professeur p " +
                "WHERE p.idProf NOT IN (" +
                "  SELECT s.idProf FROM Seance s " +
                "  WHERE s.jour = ? " +
                "  AND ((s.heureDebut < ? AND s.heureFin > ?) " +
                "  OR (s.heureDebut < ? AND s.heureFin > ?) " +
                "  OR (s.heureDebut >= ? AND s.heureDebut < ?))" +
                ") ORDER BY p.nom, p.prenom";

        List<Professeur> professeurs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, jour);
            pstmt.setTime(2, heureFin);
            pstmt.setTime(3, heureDebut);
            pstmt.setTime(4, heureFin);
            pstmt.setTime(5, heureFin);
            pstmt.setTime(6, heureDebut);
            pstmt.setTime(7, heureFin);

            ResultSet rs = pstmt.executeQuery();
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

    /**
     * Obtenir les salles disponibles pour un créneau donné
     */
    public List<Salle> getSallesDisponibles(String jour, Time heureDebut, Time heureFin) throws SQLException {
        String query = "SELECT DISTINCT s.* FROM Salle s " +
                "WHERE s.idSalle NOT IN (" +
                "  SELECT se.idSalle FROM Seance se " +
                "  WHERE se.jour = ? " +
                "  AND ((se.heureDebut < ? AND se.heureFin > ?) " +
                "  OR (se.heureDebut < ? AND se.heureFin > ?) " +
                "  OR (se.heureDebut >= ? AND se.heureDebut < ?))" +
                ") ORDER BY s.nomSalle";

        List<Salle> salles = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, jour);
            pstmt.setTime(2, heureFin);
            pstmt.setTime(3, heureDebut);
            pstmt.setTime(4, heureFin);
            pstmt.setTime(5, heureFin);
            pstmt.setTime(6, heureDebut);
            pstmt.setTime(7, heureFin);

            ResultSet rs = pstmt.executeQuery();
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

    /**
     * Obtenir les détails du conflit de salle
     */
    public Seance getConflitSalle(Seance seance) throws SQLException {
        String query = "SELECT * FROM Seance WHERE idSalle = ? AND jour = ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?)) LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getSalle().getIdSalle());
            pstmt.setString(2, seance.getJour());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setTime(4, seance.getHeureDebut());
            pstmt.setTime(5, seance.getHeureFin());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureDebut());
            pstmt.setTime(8, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Seance conflitSeance = new Seance();
                conflitSeance.setIdSeance(rs.getInt("idSeance"));
                conflitSeance.setJour(rs.getString("jour"));
                conflitSeance.setHeureDebut(rs.getTime("heureDebut"));
                conflitSeance.setHeureFin(rs.getTime("heureFin"));
                conflitSeance.setProfesseur(professeurDAO.getProfesseurById(rs.getInt("idProf")));
                conflitSeance.setGroupe(groupeDAO.getGroupeById(rs.getInt("idGroupe")));
                conflitSeance.setMatiere(matiereDAO.getMatiereById(rs.getInt("idMatiere")));
                conflitSeance.setSalle(salleDAO.getSalleById(rs.getInt("idSalle")));
                return conflitSeance;
            }
        }
        return null;
    }

    /**
     * Obtenir les détails du conflit de professeur
     */
    public Seance getConflitProfesseur(Seance seance) throws SQLException {
        String query = "SELECT * FROM Seance WHERE idProf = ? AND jour = ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?)) LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getProfesseur().getIdProf());
            pstmt.setString(2, seance.getJour());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setTime(4, seance.getHeureDebut());
            pstmt.setTime(5, seance.getHeureFin());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureDebut());
            pstmt.setTime(8, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Seance conflitSeance = new Seance();
                conflitSeance.setIdSeance(rs.getInt("idSeance"));
                conflitSeance.setJour(rs.getString("jour"));
                conflitSeance.setHeureDebut(rs.getTime("heureDebut"));
                conflitSeance.setHeureFin(rs.getTime("heureFin"));
                conflitSeance.setProfesseur(professeurDAO.getProfesseurById(rs.getInt("idProf")));
                conflitSeance.setGroupe(groupeDAO.getGroupeById(rs.getInt("idGroupe")));
                conflitSeance.setMatiere(matiereDAO.getMatiereById(rs.getInt("idMatiere")));
                conflitSeance.setSalle(salleDAO.getSalleById(rs.getInt("idSalle")));
                return conflitSeance;
            }
        }
        return null;
    }

    /**
     * Modifier une séance
     */
    public boolean modifierSeance(Seance seance) throws SQLException {
        String query = "UPDATE Seance SET jour = ?, heureDebut = ?, heureFin = ?, " +
                "idProf = ?, idGroupe = ?, idMatiere = ?, idSalle = ? " +
                "WHERE idSeance = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, seance.getJour());
            pstmt.setTime(2, seance.getHeureDebut());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setInt(4, seance.getProfesseur().getIdProf());
            pstmt.setInt(5, seance.getGroupe().getIdGroupe());
            pstmt.setInt(6, seance.getMatiere().getIdMatiere());
            pstmt.setInt(7, seance.getSalle().getIdSalle());
            pstmt.setInt(8, seance.getIdSeance());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Vérifier conflit de salle en excluant une séance
     */
    public boolean verifierConflitSalleExcept(Seance seance, int idSeanceExclue) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idSalle = ? AND jour = ? " +
                "AND idSeance != ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getSalle().getIdSalle());
            pstmt.setString(2, seance.getJour());
            pstmt.setInt(3, idSeanceExclue);
            pstmt.setTime(4, seance.getHeureFin());
            pstmt.setTime(5, seance.getHeureDebut());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureFin());
            pstmt.setTime(8, seance.getHeureDebut());
            pstmt.setTime(9, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Vérifier conflit de professeur en excluant une séance
     */
    public boolean verifierConflitProfesseurExcept(Seance seance, int idSeanceExclue) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idProf = ? AND jour = ? " +
                "AND idSeance != ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getProfesseur().getIdProf());
            pstmt.setString(2, seance.getJour());
            pstmt.setInt(3, idSeanceExclue);
            pstmt.setTime(4, seance.getHeureFin());
            pstmt.setTime(5, seance.getHeureDebut());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureFin());
            pstmt.setTime(8, seance.getHeureDebut());
            pstmt.setTime(9, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Vérifier conflit de groupe en excluant une séance
     */
    public boolean verifierConflitGroupeExcept(Seance seance, int idSeanceExclue) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idGroupe = ? AND jour = ? " +
                "AND idSeance != ? " +
                "AND ((heureDebut < ? AND heureFin > ?) OR (heureDebut < ? AND heureFin > ?) " +
                "OR (heureDebut >= ? AND heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getGroupe().getIdGroupe());
            pstmt.setString(2, seance.getJour());
            pstmt.setInt(3, idSeanceExclue);
            pstmt.setTime(4, seance.getHeureFin());
            pstmt.setTime(5, seance.getHeureDebut());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureFin());
            pstmt.setTime(8, seance.getHeureDebut());
            pstmt.setTime(9, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean supprimerSeance(int idSeance) throws SQLException {
        String query = "DELETE FROM Seance WHERE idSeance = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idSeance);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}