package com.essprjtjava.model;

import java.sql.Time;

// ==================== Classe Seance ====================
public class Seance {
    private int idSeance;
    private String jour;
    private Time heureDebut;
    private Time heureFin;
    private Professeur professeur;
    private Groupe groupe;
    private Matiere matiere;
    private Salle salle;

    public Seance() {}

    public Seance(int idSeance, String jour, Time heureDebut, Time heureFin,
                  Professeur professeur, Groupe groupe, Matiere matiere, Salle salle) {
        this.idSeance = idSeance;
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.professeur = professeur;
        this.groupe = groupe;
        this.matiere = matiere;
        this.salle = salle;
    }

    // Getters et Setters
    public int getIdSeance() { return idSeance; }
    public void setIdSeance(int idSeance) { this.idSeance = idSeance; }

    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public Time getHeureDebut() { return heureDebut; }
    public void setHeureDebut(Time heureDebut) { this.heureDebut = heureDebut; }

    public Time getHeureFin() { return heureFin; }
    public void setHeureFin(Time heureFin) { this.heureFin = heureFin; }

    public Professeur getProfesseur() { return professeur; }
    public void setProfesseur(Professeur professeur) { this.professeur = professeur; }

    public Groupe getGroupe() { return groupe; }
    public void setGroupe(Groupe groupe) { this.groupe = groupe; }

    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }

    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }

    @Override
    public String toString() {
        return jour + " " + heureDebut + "-" + heureFin + " | " + matiere.getNomMatiere();
    }
}