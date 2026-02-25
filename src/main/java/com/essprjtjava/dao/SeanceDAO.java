package com.essprjtjava.dao;

import com.essprjtjava.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    private GroupeDAO groupeDAO = new GroupeDAO();
    private MatiereDAO matiereDAO = new MatiereDAO();
    private SalleDAO salleDAO = new SalleDAO();

    /**
     * Ajouter une nouvelle séance
     */
    public boolean ajouterSeance(Seance seance) throws SQLException {
        String query = "INSERT INTO Seance (jour, heureDebut, heureFin, idGroupe, idMatiere, idSalle) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, seance.getJour());
            pstmt.setTime(2, seance.getHeureDebut());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setInt(4, seance.getGroupe().getIdGroupe());
            pstmt.setInt(5, seance.getMatiere().getIdMatiere());
            pstmt.setInt(6, seance.getSalle().getIdSalle());

            return pstmt.executeUpdate() > 0;
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
                Seance seance = buildSeanceFromResultSet(rs);
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
                Seance seance = buildSeanceFromResultSet(rs);
                seances.add(seance);
            }
        }
        return seances;
    }

    /**
     * Récupérer les séances d'un professeur (via les matières)
     */
    public List<Seance> getSeancesByProfesseur(int idProf) throws SQLException {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT s.* FROM Seance s " +
                "JOIN Matiere m ON s.idMatiere = m.idMatiere " +
                "WHERE m.idProf = ? " +
                "ORDER BY FIELD(s.jour, 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'), s.heureDebut";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idProf);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Seance seance = buildSeanceFromResultSet(rs);
                seances.add(seance);
            }
        }
        return seances;
    }

    /**
     * Modifier une séance
     */
    public boolean modifierSeance(Seance seance) throws SQLException {
        String query = "UPDATE Seance SET jour = ?, heureDebut = ?, heureFin = ?, " +
                "idGroupe = ?, idMatiere = ?, idSalle = ? WHERE idSeance = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, seance.getJour());
            pstmt.setTime(2, seance.getHeureDebut());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setInt(4, seance.getGroupe().getIdGroupe());
            pstmt.setInt(5, seance.getMatiere().getIdMatiere());
            pstmt.setInt(6, seance.getSalle().getIdSalle());
            pstmt.setInt(7, seance.getIdSeance());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer une séance
     */
    public boolean supprimerSeance(int idSeance) throws SQLException {
        String query = "DELETE FROM Seance WHERE idSeance = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idSeance);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Construire une séance à partir d'un ResultSet
     */
    private Seance buildSeanceFromResultSet(ResultSet rs) throws SQLException {
        Seance seance = new Seance();
        seance.setIdSeance(rs.getInt("idSeance"));
        seance.setJour(rs.getString("jour"));
        seance.setHeureDebut(rs.getTime("heureDebut"));
        seance.setHeureFin(rs.getTime("heureFin"));
        seance.setGroupe(groupeDAO.getGroupeById(rs.getInt("idGroupe")));
        seance.setMatiere(matiereDAO.getMatiereById(rs.getInt("idMatiere")));
        seance.setSalle(salleDAO.getSalleById(rs.getInt("idSalle")));
        return seance;
    }

    // AJOUTEZ ces méthodes à la classe SeanceDAO (partie 2)

    /**
     * Vérifier conflit de salle (vérifie TOUS les groupes)
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
     * Vérifier conflit de professeur (via matière)
     */
    public boolean verifierConflitProfesseur(Seance seance) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance s " +
                "JOIN Matiere m ON s.idMatiere = m.idMatiere " +
                "WHERE m.idProf = ? AND s.jour = ? " +
                "AND ((s.heureDebut < ? AND s.heureFin > ?) OR (s.heureDebut < ? AND s.heureFin > ?) " +
                "OR (s.heureDebut >= ? AND s.heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getMatiere().getProfesseur().getIdProf());
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
     * Vérifier conflit de groupe
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
     * Vérifications avec exclusion (pour modification)
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

    public boolean verifierConflitProfesseurExcept(Seance seance, int idSeanceExclue) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance s " +
                "JOIN Matiere m ON s.idMatiere = m.idMatiere " +
                "WHERE m.idProf = ? AND s.jour = ? AND s.idSeance != ? " +
                "AND ((s.heureDebut < ? AND s.heureFin > ?) OR (s.heureDebut < ? AND s.heureFin > ?) " +
                "OR (s.heureDebut >= ? AND s.heureDebut < ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getMatiere().getProfesseur().getIdProf());
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

    /**
     * Obtenir détails des conflits
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
                return buildSeanceFromResultSet(rs);
            }
        }
        return null;
    }

    public Seance getConflitProfesseur(Seance seance) throws SQLException {
        String query = "SELECT s.* FROM Seance s " +
                "JOIN Matiere m ON s.idMatiere = m.idMatiere " +
                "WHERE m.idProf = ? AND s.jour = ? " +
                "AND ((s.heureDebut < ? AND s.heureFin > ?) OR (s.heureDebut < ? AND s.heureFin > ?) " +
                "OR (s.heureDebut >= ? AND s.heureDebut < ?)) LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, seance.getMatiere().getProfesseur().getIdProf());
            pstmt.setString(2, seance.getJour());
            pstmt.setTime(3, seance.getHeureFin());
            pstmt.setTime(4, seance.getHeureDebut());
            pstmt.setTime(5, seance.getHeureFin());
            pstmt.setTime(6, seance.getHeureFin());
            pstmt.setTime(7, seance.getHeureDebut());
            pstmt.setTime(8, seance.getHeureFin());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildSeanceFromResultSet(rs);
            }
        }
        return null;
    }
}
