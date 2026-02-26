package com.essprjtjava.dao;

import com.essprjtjava.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    // Mapper un ResultSet vers un objet Seance
    private Seance mapSeance(ResultSet rs) throws SQLException {
        Professeur prof = new Professeur(
                rs.getInt("idProf"),
                rs.getString("nomProf"),
                rs.getString("prenomProf"),
                rs.getString("specialite")
        );

        Matiere matiere = new Matiere(
                rs.getInt("idMatiere"),
                rs.getString("nomMatiere"),
                prof
        );

        Salle salle = new Salle(
                rs.getInt("idSalle"),
                rs.getString("nomSalle")
        );

        Groupe groupe = new Groupe(
                rs.getInt("idGroupe"),
                rs.getString("nomGroupe")
        );

        Seance seance = new Seance();
        seance.setIdSeance(rs.getInt("idSeance"));
        seance.setJour(rs.getString("jour"));
        seance.setHeureDebut(rs.getTime("heureDebut"));
        seance.setHeureFin(rs.getTime("heureFin"));
        seance.setGroupe(groupe);
        seance.setMatiere(matiere);
        seance.setSalle(salle);
        return seance;
    }

    private String getJoinQuery() {
        return "SELECT s.*, " +
                "p.nom as nomProf, p.prenom as prenomProf, p.specialite, " +
                "m.nomMatiere, " +
                "sa.nomSalle, " +
                "g.nomGroupe " +
                "FROM Seance s " +
                "JOIN Professeur p ON s.idProf = p.idProf " +
                "JOIN Matiere m ON s.idMatiere = m.idMatiere " +
                "JOIN Salle sa ON s.idSalle = sa.idSalle " +
                "JOIN Groupe g ON s.idGroupe = g.idGroupe";
    }

    public List<Seance> getAllSeances() throws SQLException {
        List<Seance> seances = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(getJoinQuery() + " ORDER BY s.jour, s.heureDebut")) {
            while (rs.next()) seances.add(mapSeance(rs));
        }
        return seances;
    }

    public List<Seance> getSeancesByGroupe(int idGroupe) throws SQLException {
        List<Seance> seances = new ArrayList<>();
        String query = getJoinQuery() + " WHERE s.idGroupe = ? ORDER BY s.jour, s.heureDebut";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idGroupe);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) seances.add(mapSeance(rs));
        }
        return seances;
    }

    public List<Seance> getSeancesByProfesseur(int idProf) throws SQLException {
        List<Seance> seances = new ArrayList<>();
        String query = getJoinQuery() + " WHERE s.idProf = ? ORDER BY s.jour, s.heureDebut";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idProf);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) seances.add(mapSeance(rs));
        }
        return seances;
    }

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
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean modifierSeance(Seance seance) throws SQLException {
        String query = "UPDATE Seance SET jour=?, heureDebut=?, heureFin=?, idProf=?, idGroupe=?, idMatiere=?, idSalle=? " +
                "WHERE idSeance=?";
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

    public boolean supprimerSeance(int idSeance) throws SQLException {
        String query = "DELETE FROM Seance WHERE idSeance = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idSeance);
            return pstmt.executeUpdate() > 0;
        }
    }

    // ===== Vérifications de conflits =====

    public boolean verifierConflitSalle(Seance s) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idSalle=? AND jour=? " +
                "AND heureDebut < ? AND heureFin > ?";
        return executeConflitQuery(query,
                s.getSalle().getIdSalle(), s.getJour(), s.getHeureFin(), s.getHeureDebut());
    }

    public boolean verifierConflitSalleExcept(Seance s, int idExclue) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idSalle=? AND jour=? " +
                "AND heureDebut < ? AND heureFin > ? AND idSeance != ?";
        return executeConflitQueryExcept(query,
                s.getSalle().getIdSalle(), s.getJour(), s.getHeureFin(), s.getHeureDebut(), idExclue);
    }

    public boolean verifierConflitProfesseur(Seance s) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idProf=? AND jour=? " +
                "AND heureDebut < ? AND heureFin > ?";
        return executeConflitQuery(query,
                s.getProfesseur().getIdProf(), s.getJour(), s.getHeureFin(), s.getHeureDebut());
    }

    public boolean verifierConflitProfesseurExcept(Seance s, int idExclue) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idProf=? AND jour=? " +
                "AND heureDebut < ? AND heureFin > ? AND idSeance != ?";
        return executeConflitQueryExcept(query,
                s.getProfesseur().getIdProf(), s.getJour(), s.getHeureFin(), s.getHeureDebut(), idExclue);
    }

    public boolean verifierConflitGroupe(Seance s) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idGroupe=? AND jour=? " +
                "AND heureDebut < ? AND heureFin > ?";
        return executeConflitQuery(query,
                s.getGroupe().getIdGroupe(), s.getJour(), s.getHeureFin(), s.getHeureDebut());
    }

    public boolean verifierConflitGroupeExcept(Seance s, int idExclue) throws SQLException {
        String query = "SELECT COUNT(*) FROM Seance WHERE idGroupe=? AND jour=? " +
                "AND heureDebut < ? AND heureFin > ? AND idSeance != ?";
        return executeConflitQueryExcept(query,
                s.getGroupe().getIdGroupe(), s.getJour(), s.getHeureFin(), s.getHeureDebut(), idExclue);
    }

    public Seance getConflitSalle(Seance s) throws SQLException {
        String query = getJoinQuery() + " WHERE s.idSalle=? AND s.jour=? " +
                "AND s.heureDebut < ? AND s.heureFin > ? LIMIT 1";
        return getConflitSeance(query,
                s.getSalle().getIdSalle(), s.getJour(), s.getHeureFin(), s.getHeureDebut());
    }

    public Seance getConflitProfesseur(Seance s) throws SQLException {
        String query = getJoinQuery() + " WHERE s.idProf=? AND s.jour=? " +
                "AND s.heureDebut < ? AND s.heureFin > ? LIMIT 1";
        return getConflitSeance(query,
                s.getProfesseur().getIdProf(), s.getJour(), s.getHeureFin(), s.getHeureDebut());
    }

    // ===== Méthodes utilitaires privées =====

    private boolean executeConflitQuery(String query, int id, String jour,
                                        Time heureFin, Time heureDebut) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, jour);
            pstmt.setTime(3, heureFin);
            pstmt.setTime(4, heureDebut);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean executeConflitQueryExcept(String query, int id, String jour,
                                              Time heureFin, Time heureDebut,
                                              int idExclue) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, jour);
            pstmt.setTime(3, heureFin);
            pstmt.setTime(4, heureDebut);
            pstmt.setInt(5, idExclue);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private Seance getConflitSeance(String query, int id, String jour,
                                    Time heureFin, Time heureDebut) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, jour);
            pstmt.setTime(3, heureFin);
            pstmt.setTime(4, heureDebut);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapSeance(rs);
        }
        return null;
    }
}