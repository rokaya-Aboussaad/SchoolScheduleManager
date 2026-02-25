package com.essprjtjava.service;

import com.essprjtjava.model.*;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

/**
 * Interface définissant les services métier pour la gestion des séances
 */
public interface ISeanceService {

    /**
     * Créer une nouvelle séance avec vérifications de conflits
     * @return true si la séance a été créée, false sinon
     * @throws ConflitException si un conflit est détecté
     */
    boolean creerSeance(Seance seance) throws SQLException, ConflitException;

    /**
     * Modifier une séance existante
     */
    boolean modifierSeance(Seance seance) throws SQLException, ConflitException;

    /**
     * Supprimer une séance
     */
    boolean supprimerSeance(int idSeance) throws SQLException;

    /**
     * Récupérer toutes les séances
     */
    List<Seance> getAllSeances() throws SQLException;

    /**
     * Récupérer les séances d'un groupe
     */
    List<Seance> getSeancesByGroupe(int idGroupe) throws SQLException;

    /**
     * Récupérer les séances d'un professeur
     */
    List<Seance> getSeancesByProfesseur(int idProf) throws SQLException;

    /**
     * Vérifier la disponibilité d'une salle
     */
    boolean isSalleDisponible(int idSalle, String jour, Time heureDebut, Time heureFin, Integer idSeanceExclue) throws SQLException;

    /**
     * Vérifier la disponibilité d'un professeur (via matière)
     */
    boolean isProfesseurDisponible(int idProf, String jour, Time heureDebut, Time heureFin, Integer idSeanceExclue) throws SQLException;

    /**
     * Vérifier la disponibilité d'un groupe
     */
    boolean isGroupeDisponible(int idGroupe, String jour, Time heureDebut, Time heureFin, Integer idSeanceExclue) throws SQLException;

    /**
     * Obtenir les salles disponibles pour un créneau
     */
    List<Salle> getSallesDisponibles(String jour, Time heureDebut, Time heureFin) throws SQLException;

    /**
     * Obtenir les matières disponibles pour un créneau (professeurs libres)
     */
    List<Matiere> getMatieresDisponibles(String jour, Time heureDebut, Time heureFin) throws SQLException;
}