package com.essprjtjava.service;

import com.essprjtjava.model.Seance;

/**
 * Exception levée en cas de conflit lors de la création/modification d'une séance
 */
public class ConflitException extends Exception {

    public enum TypeConflit {
        SALLE,
        PROFESSEUR,
        GROUPE
    }

    private TypeConflit typeConflit;
    private Seance seanceConflictuelle;

    public ConflitException(TypeConflit typeConflit, String message) {
        super(message);
        this.typeConflit = typeConflit;
    }

    public ConflitException(TypeConflit typeConflit, String message, Seance seanceConflictuelle) {
        super(message);
        this.typeConflit = typeConflit;
        this.seanceConflictuelle = seanceConflictuelle;
    }

    public TypeConflit getTypeConflit() {
        return typeConflit;
    }

    public Seance getSeanceConflictuelle() {
        return seanceConflictuelle;
    }

    /**
     * Obtenir un message détaillé du conflit
     */
    public String getMessageDetaille() {
        StringBuilder sb = new StringBuilder();
        sb.append("❌ CONFLIT ");

        switch (typeConflit) {
            case SALLE:
                sb.append("DE SALLE");
                break;
            case PROFESSEUR:
                sb.append("DE PROFESSEUR");
                break;
            case GROUPE:
                sb.append("DE GROUPE");
                break;
        }

        sb.append(" !\n").append(getMessage());

        if (seanceConflictuelle != null) {
            sb.append("\n\nSéance conflictuelle :");
            sb.append("\n- Groupe : ").append(seanceConflictuelle.getGroupe().getNomGroupe());
            sb.append("\n- Matière : ").append(seanceConflictuelle.getMatiere().getNomMatiere());
            sb.append("\n- Professeur : ").append(seanceConflictuelle.getProfesseur().getNom())
                    .append(" ").append(seanceConflictuelle.getProfesseur().getPrenom());
            sb.append("\n- Salle : ").append(seanceConflictuelle.getSalle().getNomSalle());
            sb.append("\n- Horaire : ").append(seanceConflictuelle.getJour())
                    .append(" de ").append(seanceConflictuelle.getHeureDebut().toString().substring(0, 5))
                    .append(" à ").append(seanceConflictuelle.getHeureFin().toString().substring(0, 5));
        }

        return sb.toString();
    }
}