package com.essprjtjava.model;

public class Salle {
    private int idSalle;
    private String nomSalle;

    public Salle() {}

    public Salle(int idSalle, String nomSalle) {
        this.idSalle = idSalle;
        this.nomSalle = nomSalle;
    }

    public int getIdSalle() { return idSalle; }
    public void setIdSalle(int idSalle) { this.idSalle = idSalle; }

    public String getNomSalle() { return nomSalle; }
    public void setNomSalle(String nomSalle) { this.nomSalle = nomSalle; }

    // Retourne "" pour éviter les NullPointerException dans les controllers
    public String getTypeSalle() { return ""; }
    public int getCapacite() { return 0; }

    @Override
    public String toString() { return nomSalle; }
}