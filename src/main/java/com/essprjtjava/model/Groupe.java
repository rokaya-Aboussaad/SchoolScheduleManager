// ==================== Classe Groupe ====================
package com.essprjtjava.model;

public class Groupe {
    private int idGroupe;
    private String nomGroupe;

    public Groupe() {}

    public Groupe(int idGroupe, String nomGroupe) {
        this.idGroupe = idGroupe;
        this.nomGroupe = nomGroupe;
    }

    public int getIdGroupe() {
        return idGroupe;
    }
    public void setIdGroupe(int idGroupe) { this.idGroupe = idGroupe; }

    public String getNomGroupe() { return nomGroupe; }
    public void setNomGroupe(String nomGroupe) { this.nomGroupe = nomGroupe; }

    @Override
    public String toString() {
        return nomGroupe;
    }
}
