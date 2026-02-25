package com.essprjtjava.service;

import com.essprjtjava.dao.SalleDAO;
import com.essprjtjava.model.Salle;
import java.sql.SQLException;
import java.util.List;

/**
 * Service pour la gestion des salles
 */
public class SalleService {

    private final SalleDAO salleDAO;

    public SalleService() {
        this.salleDAO = new SalleDAO();
    }

    /**
     * Récupérer toutes les salles
     */
    public List<Salle> getAllSalles() throws SQLException {
        return salleDAO.getAllSalles();
    }

    /**
     * Récupérer une salle par son ID
     */
    public Salle getSalleById(int id) throws SQLException {
        return salleDAO.getSalleById(id);
    }

    /**
     * Récupérer les salles par type
     */
    public List<Salle> getSallesByType(String typeSalle) throws SQLException {
        return salleDAO.getSallesByType(typeSalle);
    }

    /**
     * Obtenir les types de salles disponibles
     */
    public List<String> getTypesSalles() {
        return salleDAO.getTypesSalles();
    }

    /**
     * Ajouter une nouvelle salle
     */
    public boolean ajouterSalle(Salle salle) throws SQLException {
        // Validation métier
        if (salle.getNomSalle() == null || salle.getNomSalle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide");
        }

        if (salle.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être supérieure à 0");
        }

        return salleDAO.ajouterSalle(salle);
    }

    /**
     * Modifier une salle
     */
    public boolean modifierSalle(Salle salle) throws SQLException {
        // Validation métier
        if (salle.getNomSalle() == null || salle.getNomSalle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide");
        }

        if (salle.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être supérieure à 0");
        }

        return salleDAO.modifierSalle(salle);
    }

    /**
     * Supprimer une salle
     */
    public boolean supprimerSalle(int idSalle) throws SQLException {
        return salleDAO.supprimerSalle(idSalle);
    }
}