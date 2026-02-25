package com.essprjtjava.service;

import com.essprjtjava.dao.*;
import com.essprjtjava.model.*;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation des services métier pour la gestion des séances
 * Cette classe contient toute la logique métier et les règles de gestion
 */
public class SeanceServiceImpl implements ISeanceService {

    private final SeanceDAO seanceDAO;
    private final SalleDAO salleDAO;
    private final MatiereDAO matiereDAO;

    public SeanceServiceImpl() {
        this.seanceDAO = new SeanceDAO();
        this.salleDAO = new SalleDAO();
        this.matiereDAO = new MatiereDAO();
    }

    @Override
    public boolean creerSeance(Seance seance) throws SQLException, ConflitException {
        // Vérifications des conflits
        verifierConflits(seance, null);

        // Si pas de conflit, créer la séance
        return seanceDAO.ajouterSeance(seance);
    }

    @Override
    public boolean modifierSeance(Seance seance) throws SQLException, ConflitException {
        // Vérifications des conflits en excluant la séance modifiée
        verifierConflits(seance, seance.getIdSeance());

        // Si pas de conflit, modifier la séance
        return seanceDAO.modifierSeance(seance);
    }

    @Override
    public boolean supprimerSeance(int idSeance) throws SQLException {
        return seanceDAO.supprimerSeance(idSeance);
    }

    @Override
    public List<Seance> getAllSeances() throws SQLException {
        return seanceDAO.getAllSeances();
    }

    @Override
    public List<Seance> getSeancesByGroupe(int idGroupe) throws SQLException {
        return seanceDAO.getSeancesByGroupe(idGroupe);
    }

    @Override
    public List<Seance> getSeancesByProfesseur(int idProf) throws SQLException {
        return seanceDAO.getSeancesByProfesseur(idProf);
    }

    @Override
    public boolean isSalleDisponible(int idSalle, String jour, Time heureDebut, Time heureFin, Integer idSeanceExclue) throws SQLException {
        Seance seanceTest = new Seance();
        Salle salle = salleDAO.getSalleById(idSalle);
        seanceTest.setSalle(salle);
        seanceTest.setJour(jour);
        seanceTest.setHeureDebut(heureDebut);
        seanceTest.setHeureFin(heureFin);

        if (idSeanceExclue != null) {
            return !seanceDAO.verifierConflitSalleExcept(seanceTest, idSeanceExclue);
        } else {
            return !seanceDAO.verifierConflitSalle(seanceTest);
        }
    }

    @Override
    public boolean isProfesseurDisponible(int idProf, String jour, Time heureDebut, Time heureFin, Integer idSeanceExclue) throws SQLException {
        // Récupérer une matière de ce professeur pour le test
        List<Matiere> matieres = matiereDAO.getMatieresByProfesseur(idProf);
        if (matieres.isEmpty()) {
            return true; // Pas de matière = professeur libre
        }

        Seance seanceTest = new Seance();
        seanceTest.setMatiere(matieres.get(0)); // Utiliser n'importe quelle matière du prof
        seanceTest.setJour(jour);
        seanceTest.setHeureDebut(heureDebut);
        seanceTest.setHeureFin(heureFin);

        if (idSeanceExclue != null) {
            return !seanceDAO.verifierConflitProfesseurExcept(seanceTest, idSeanceExclue);
        } else {
            return !seanceDAO.verifierConflitProfesseur(seanceTest);
        }
    }

    @Override
    public boolean isGroupeDisponible(int idGroupe, String jour, Time heureDebut, Time heureFin, Integer idSeanceExclue) throws SQLException {
        Seance seanceTest = new Seance();
        Groupe groupe = new Groupe();
        groupe.setIdGroupe(idGroupe);
        seanceTest.setGroupe(groupe);
        seanceTest.setJour(jour);
        seanceTest.setHeureDebut(heureDebut);
        seanceTest.setHeureFin(heureFin);

        if (idSeanceExclue != null) {
            return !seanceDAO.verifierConflitGroupeExcept(seanceTest, idSeanceExclue);
        } else {
            return !seanceDAO.verifierConflitGroupe(seanceTest);
        }
    }

    @Override
    public List<Salle> getSallesDisponibles(String jour, Time heureDebut, Time heureFin) throws SQLException {
        List<Salle> salles = salleDAO.getAllSalles();
        List<Salle> sallesDisponibles = new ArrayList<>();

        for (Salle salle : salles) {
            if (isSalleDisponible(salle.getIdSalle(), jour, heureDebut, heureFin, null)) {
                sallesDisponibles.add(salle);
            }
        }

        return sallesDisponibles;
    }

    @Override
    public List<Matiere> getMatieresDisponibles(String jour, Time heureDebut, Time heureFin) throws SQLException {
        List<Matiere> matieres = matiereDAO.getAllMatieres();
        List<Matiere> matieresDisponibles = new ArrayList<>();

        for (Matiere matiere : matieres) {
            if (isProfesseurDisponible(matiere.getProfesseur().getIdProf(), jour, heureDebut, heureFin, null)) {
                matieresDisponibles.add(matiere);
            }
        }

        return matieresDisponibles;
    }

    /**
     * Méthode privée pour vérifier tous les conflits
     */
    private void verifierConflits(Seance seance, Integer idSeanceExclue) throws SQLException, ConflitException {
        // Vérifier conflit de salle
        boolean conflitSalle = (idSeanceExclue != null)
                ? seanceDAO.verifierConflitSalleExcept(seance, idSeanceExclue)
                : seanceDAO.verifierConflitSalle(seance);

        if (conflitSalle) {
            Seance conflictuelle = seanceDAO.getConflitSalle(seance);
            String message = String.format(
                    "La salle %s est déjà occupée le %s de %s à %s",
                    seance.getSalle().getNomSalle(),
                    seance.getJour(),
                    seance.getHeureDebut().toString().substring(0, 5),
                    seance.getHeureFin().toString().substring(0, 5)
            );
            throw new ConflitException(ConflitException.TypeConflit.SALLE, message, conflictuelle);
        }

        // Vérifier conflit de professeur
        boolean conflitProf = (idSeanceExclue != null)
                ? seanceDAO.verifierConflitProfesseurExcept(seance, idSeanceExclue)
                : seanceDAO.verifierConflitProfesseur(seance);

        if (conflitProf) {
            Seance conflictuelle = seanceDAO.getConflitProfesseur(seance);
            String message = String.format(
                    "Le professeur %s %s a déjà cours le %s de %s à %s",
                    seance.getProfesseur().getNom(),
                    seance.getProfesseur().getPrenom(),
                    seance.getJour(),
                    seance.getHeureDebut().toString().substring(0, 5),
                    seance.getHeureFin().toString().substring(0, 5)
            );
            throw new ConflitException(ConflitException.TypeConflit.PROFESSEUR, message, conflictuelle);
        }

        // Vérifier conflit de groupe
        boolean conflitGroupe = (idSeanceExclue != null)
                ? seanceDAO.verifierConflitGroupeExcept(seance, idSeanceExclue)
                : seanceDAO.verifierConflitGroupe(seance);

        if (conflitGroupe) {
            String message = String.format(
                    "Le groupe %s a déjà une séance le %s de %s à %s",
                    seance.getGroupe().getNomGroupe(),
                    seance.getJour(),
                    seance.getHeureDebut().toString().substring(0, 5),
                    seance.getHeureFin().toString().substring(0, 5)
            );
            throw new ConflitException(ConflitException.TypeConflit.GROUPE, message);
        }
    }
}