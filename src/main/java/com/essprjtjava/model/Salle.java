package com.essprjtjava.model;

public class Salle {
    private int idSalle;
    private String nomSalle;
    private String typeSalle; // 'Cours', 'TD', 'TP', 'Amphi', 'Labo'
    private int capacite;

    public Salle() {}

    public Salle(int idSalle, String nomSalle) {
        this.idSalle = idSalle;
        this.nomSalle = nomSalle;
        this.typeSalle = "Cours";
        this.capacite = 30;
    }

    public Salle(int idSalle, String nomSalle, String typeSalle, int capacite) {
        this.idSalle = idSalle;
        this.nomSalle = nomSalle;
        this.typeSalle = typeSalle;
        this.capacite = capacite;
    }

    public int getIdSalle() {
        return idSalle;
    }

    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle;
    }

    public String getNomSalle() {
        return nomSalle;
    }

    public void setNomSalle(String nomSalle) {
        this.nomSalle = nomSalle;
    }

    public String getTypeSalle() {
        return typeSalle;
    }

    public void setTypeSalle(String typeSalle) {
        this.typeSalle = typeSalle;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return nomSalle + " (" + typeSalle + ")";
    }
}