// ==================== Classe Professeur ====================
package com.essprjtjava.model;

public class Professeur {
    private int idProf;
    private String nom;
    private String prenom;
    private String specialite; // La matière qu'il enseigne

    public Professeur() {}

    public Professeur(int idProf, String nom, String prenom, String specialite) {
        this.idProf = idProf;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
    }

    public int getIdProf() { return idProf; }
    public void setIdProf(int idProf) { this.idProf = idProf; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    @Override
    public String toString() {
        return nom + " " + prenom + " - " + specialite;
    }
}
