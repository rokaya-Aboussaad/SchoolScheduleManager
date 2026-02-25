package com.essprjtjava.service;

import com.essprjtjava.dao.ProfesseurDAO;
import com.essprjtjava.model.Professeur;
import java.sql.SQLException;
import java.util.List;

/**
 * Service pour la gestion des professeurs
 */
public class ProfesseurService {

    private final ProfesseurDAO professeurDAO;

    public ProfesseurService() {
        this.professeurDAO = new ProfesseurDAO();
    }

    /**
     * Récupérer tous les professeurs
     */
    public List<Professeur> getAllProfesseurs() throws SQLException {
        return professeurDAO.getAllProfesseurs();
    }

    /**
     * Récupérer un professeur par son ID
     */
    public Professeur getProfesseurById(int id) throws SQLException {
        return professeurDAO.getProfesseurById(id);
    }

    /**
     * Vérifier si un professeur existe
     */
    public boolean professeurExiste(int id) throws SQLException {
        return professeurDAO.getProfesseurById(id) != null;
    }
}