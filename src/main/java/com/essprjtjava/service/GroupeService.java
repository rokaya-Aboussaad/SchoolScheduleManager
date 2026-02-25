package com.essprjtjava.service;

import com.essprjtjava.dao.GroupeDAO;
import com.essprjtjava.model.Groupe;
import java.sql.SQLException;
import java.util.List;

/**
 * Service pour la gestion des groupes
 */
public class GroupeService {

    private final GroupeDAO groupeDAO;

    public GroupeService() {
        this.groupeDAO = new GroupeDAO();
    }

    /**
     * Récupérer tous les groupes
     */
    public List<Groupe> getAllGroupes() throws SQLException {
        return groupeDAO.getAllGroupes();
    }

    /**
     * Récupérer un groupe par son ID
     */
    public Groupe getGroupeById(int id) throws SQLException {
        return groupeDAO.getGroupeById(id);
    }

    /**
     * Vérifier si un groupe existe
     */
    public boolean groupeExiste(int id) throws SQLException {
        return groupeDAO.getGroupeById(id) != null;
    }

    /**
     * Obtenir le nombre total de groupes
     */
    public int getNombreGroupes() throws SQLException {
        return groupeDAO.getAllGroupes().size();
    }
}