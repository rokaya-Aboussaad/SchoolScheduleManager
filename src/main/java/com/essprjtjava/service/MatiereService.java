package com.essprjtjava.service;

import com.essprjtjava.dao.MatiereDAO;
import com.essprjtjava.model.Matiere;
import java.sql.SQLException;
import java.util.List;

/**
 * Service pour la gestion des matières
 */
public class MatiereService {

    private final MatiereDAO matiereDAO;

    public MatiereService() {
        this.matiereDAO = new MatiereDAO();
    }

    /**
     * Récupérer toutes les matières
     */
    public List<Matiere> getAllMatieres() throws SQLException {
        return matiereDAO.getAllMatieres();
    }

    /**
     * Récupérer une matière par son ID
     */
    public Matiere getMatiereById(int id) throws SQLException {
        return matiereDAO.getMatiereById(id);
    }

    /**
     * Récupérer les matières d'un professeur
     */
    public List<Matiere> getMatieresByProfesseur(int idProf) throws SQLException {
        return matiereDAO.getMatieresByProfesseur(idProf);
    }

    /**
     * Ajouter une nouvelle matière
     */
    public boolean ajouterMatiere(Matiere matiere) throws SQLException {
        // Validation métier si nécessaire
        if (matiere.getNomMatiere() == null || matiere.getNomMatiere().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la matière ne peut pas être vide");
        }

        if (matiere.getProfesseur() == null) {
            throw new IllegalArgumentException("Une matière doit être associée à un professeur");
        }

        return matiereDAO.ajouterMatiere(matiere);
    }

    /**
     * Modifier une matière
     */
    public boolean modifierMatiere(Matiere matiere) throws SQLException {
        // Validation métier
        if (matiere.getNomMatiere() == null || matiere.getNomMatiere().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la matière ne peut pas être vide");
        }

        return matiereDAO.modifierMatiere(matiere);
    }

    /**
     * Supprimer une matière
     */
    public boolean supprimerMatiere(int idMatiere) throws SQLException {
        return matiereDAO.supprimerMatiere(idMatiere);
    }
}