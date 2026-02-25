// ==================== Classe Matiere ====================
package com.essprjtjava.model;

public class Matiere {
    private int idMatiere;
    private String nomMatiere;

    public Matiere() {}

    public Matiere(int idMatiere, String nomMatiere) {
        this.idMatiere = idMatiere;
        this.nomMatiere = nomMatiere;
    }

    public int getIdMatiere() { return idMatiere; }
    public void setIdMatiere(int idMatiere) { this.idMatiere = idMatiere; }

    public String getNomMatiere() { return nomMatiere; }
    public void setNomMatiere(String nomMatiere) { this.nomMatiere = nomMatiere; }

    @Override
    public String toString() {
        return nomMatiere;
    }
}
