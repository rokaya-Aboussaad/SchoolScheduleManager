package com.essprjtjava.model;

public class Matiere {
    private int idMatiere;
    private String nomMatiere;
    private Professeur professeur; // sera lié via Seance.idProf

    public Matiere() {}

    public Matiere(int idMatiere, String nomMatiere) {
        this.idMatiere = idMatiere;
        this.nomMatiere = nomMatiere;
    }

    public Matiere(int idMatiere, String nomMatiere, Professeur professeur) {
        this.idMatiere = idMatiere;
        this.nomMatiere = nomMatiere;
        this.professeur = professeur;
    }

    public int getIdMatiere() { return idMatiere; }
    public void setIdMatiere(int idMatiere) { this.idMatiere = idMatiere; }

    public String getNomMatiere() { return nomMatiere; }
    public void setNomMatiere(String nomMatiere) { this.nomMatiere = nomMatiere; }

    public Professeur getProfesseur() { return professeur; }
    public void setProfesseur(Professeur professeur) { this.professeur = professeur; }

    @Override
    public String toString() {
        return nomMatiere + (professeur != null ? " - " + professeur.getNom() : "");
    }
}