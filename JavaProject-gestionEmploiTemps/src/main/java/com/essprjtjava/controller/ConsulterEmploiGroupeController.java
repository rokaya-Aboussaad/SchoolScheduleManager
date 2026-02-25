package com.essprjtjava.controller;

import com.essprjtjava.dao.*;
import com.essprjtjava.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class ConsulterEmploiGroupeController {

    @FXML private ComboBox<Groupe> cbGroupe;
    @FXML private VBox vboxEmploi;
    @FXML private VBox vboxMessage;
    @FXML private GridPane gridEmploi;
    @FXML private Label lblInfo;

    private GroupeDAO groupeDAO;
    private SeanceDAO seanceDAO;

    private String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
    private String[] heures = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

    @FXML
    public void initialize() {
        groupeDAO = new GroupeDAO();
        seanceDAO = new SeanceDAO();

        chargerGroupes();
    }

    private void chargerGroupes() {
        try {
            List<Groupe> groupes = groupeDAO.getAllGroupes();
            cbGroupe.setItems(FXCollections.observableArrayList(groupes));
        } catch (Exception e) {
            showError("Erreur de chargement des groupes: " + e.getMessage());
        }
    }

    @FXML
    private void handleGroupeSelection() {
        Groupe groupe = cbGroupe.getValue();
        if (groupe != null) {
            afficherEmploiDuTemps(groupe);
        }
    }

    private void afficherEmploiDuTemps(Groupe groupe) {
        try {
            // Récupérer les séances du groupe
            List<Seance> seances = seanceDAO.getSeancesByGroupe(groupe.getIdGroupe());

            // Masquer le message et afficher la grille
            vboxMessage.setVisible(false);
            vboxMessage.setManaged(false);
            gridEmploi.setVisible(true);
            gridEmploi.setManaged(true);

            // Vider la grille
            gridEmploi.getChildren().clear();

            // Créer la grille emploi du temps
            creerGrilleEmploi(seances);

            // Mettre à jour les infos
            lblInfo.setText("Emploi du temps de " + groupe.getNomGroupe() + " - " + seances.size() + " séance(s)");

        } catch (Exception e) {
            showError("Erreur lors de l'affichage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void creerGrilleEmploi(List<Seance> seances) {
        // Vider la grille
        gridEmploi.getChildren().clear();

        // Organiser les séances par jour
        Map<String, List<Seance>> seancesParJour = new HashMap<>();
        for (String jour : jours) {
            seancesParJour.put(jour, new ArrayList<>());
        }

        for (Seance seance : seances) {
            seancesParJour.get(seance.getJour()).add(seance);
        }

        // Trier les séances par heure
        for (String jour : jours) {
            seancesParJour.get(jour).sort((s1, s2) ->
                    s1.getHeureDebut().compareTo(s2.getHeureDebut())
            );
        }

        // Créer l'affichage simple par jour
        VBox contenu = new VBox(15);
        contenu.setStyle("-fx-padding: 20;");

        for (String jour : jours) {
            List<Seance> seancesJour = seancesParJour.get(jour);

            if (!seancesJour.isEmpty()) {
                // En-tête du jour
                Label lblJour = new Label(jour);
                lblJour.setFont(Font.font("System", FontWeight.BOLD, 18));
                lblJour.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 10 0 5 0;");
                contenu.getChildren().add(lblJour);

                // Séances du jour
                for (Seance seance : seancesJour) {
                    HBox seanceBox = new HBox(15);
                    seanceBox.setAlignment(Pos.CENTER_LEFT);
                    seanceBox.setStyle("-fx-background-color: " + getCouleurMatiere(seance.getMatiere().getNomMatiere()) +
                            "; -fx-padding: 15; -fx-background-radius: 8;");

                    // Horaire
                    VBox vboxHoraire = new VBox(2);
                    vboxHoraire.setAlignment(Pos.CENTER);
                    Label lblHoraire = new Label(
                            seance.getHeureDebut().toString().substring(0, 5) + " - " +
                                    seance.getHeureFin().toString().substring(0, 5)
                    );
                    lblHoraire.setFont(Font.font("System", FontWeight.BOLD, 16));
                    lblHoraire.setStyle("-fx-text-fill: white;");
                    vboxHoraire.getChildren().add(lblHoraire);
                    vboxHoraire.setPrefWidth(120);

                    // Matière
                    VBox vboxMatiere = new VBox(3);
                    Label lblMatiere = new Label(seance.getMatiere().getNomMatiere());
                    lblMatiere.setFont(Font.font("System", FontWeight.BOLD, 15));
                    lblMatiere.setStyle("-fx-text-fill: white;");
                    Label lblProf = new Label(seance.getProfesseur().getNom() + " " +
                            seance.getProfesseur().getPrenom());
                    lblProf.setFont(Font.font("System", 13));
                    lblProf.setStyle("-fx-text-fill: white;");
                    vboxMatiere.getChildren().addAll(lblMatiere, lblProf);
                    vboxMatiere.setPrefWidth(250);

                    // Salle
                    Label lblSalle = new Label("📍 Salle " + seance.getSalle().getNomSalle());
                    lblSalle.setFont(Font.font("System", FontWeight.BOLD, 13));
                    lblSalle.setStyle("-fx-text-fill: white;");
                    lblSalle.setPrefWidth(120);

                    seanceBox.getChildren().addAll(vboxHoraire, vboxMatiere, lblSalle);
                    contenu.getChildren().add(seanceBox);
                }

                // Séparateur
                Separator sep = new Separator();
                sep.setStyle("-fx-background-color: #bdc3c7;");
                contenu.getChildren().add(sep);
            }
        }

        // Ajouter dans un ScrollPane si nécessaire
        ScrollPane scroll = new ScrollPane(contenu);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        gridEmploi.add(scroll, 0, 0);
    }

    private VBox creerCelluleSeance(Seance seance) {
        VBox cellule = new VBox(5);
        cellule.setAlignment(Pos.CENTER);
        cellule.setPadding(new Insets(8));
        cellule.setPrefSize(150, 60);
        cellule.setMaxWidth(Double.MAX_VALUE);

        // Couleur selon la matière
        String couleur = getCouleurMatiere(seance.getMatiere().getNomMatiere());
        cellule.setStyle("-fx-background-color: " + couleur + "; -fx-border-color: white; -fx-border-width: 1; -fx-background-radius: 3;");

        // Horaire
        Label lblHoraire = new Label(
                seance.getHeureDebut().toString().substring(0, 5) + "-" +
                        seance.getHeureFin().toString().substring(0, 5)
        );
        lblHoraire.setFont(Font.font("System", FontWeight.BOLD, 11));
        lblHoraire.setStyle("-fx-text-fill: white;");

        // Matière
        Label lblMatiere = new Label(seance.getMatiere().getNomMatiere());
        lblMatiere.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblMatiere.setStyle("-fx-text-fill: white;");
        lblMatiere.setWrapText(true);

        // Professeur
        Label lblProf = new Label(
                seance.getProfesseur().getNom() + " " +
                        seance.getProfesseur().getPrenom().charAt(0) + "."
        );
        lblProf.setFont(Font.font("System", 10));
        lblProf.setStyle("-fx-text-fill: white;");

        // Salle
        Label lblSalle = new Label("📍 " + seance.getSalle().getNomSalle());
        lblSalle.setFont(Font.font("System", FontWeight.BOLD, 10));
        lblSalle.setStyle("-fx-text-fill: white;");

        cellule.getChildren().addAll(lblHoraire, lblMatiere, lblProf, lblSalle);

        // Tooltip avec plus d'infos
        Tooltip tooltip = new Tooltip(
                seance.getMatiere().getNomMatiere() + "\n" +
                        seance.getProfesseur().getNom() + " " + seance.getProfesseur().getPrenom() + "\n" +
                        "Salle: " + seance.getSalle().getNomSalle() + "\n" +
                        seance.getHeureDebut().toString().substring(0, 5) + " - " +
                        seance.getHeureFin().toString().substring(0, 5)
        );
        Tooltip.install(cellule, tooltip);

        return cellule;
    }

    private VBox creerCelluleVide() {
        VBox cellule = new VBox();
        cellule.setAlignment(Pos.CENTER);
        cellule.setPrefSize(150, 60);
        cellule.setMaxWidth(Double.MAX_VALUE);
        cellule.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");

        Label lblVide = new Label("-");
        lblVide.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 18;");
        cellule.getChildren().add(lblVide);

        return cellule;
    }

    private String getCouleurMatiere(String matiere) {
        // Associer des couleurs aux matières
        Map<String, String> couleurs = new HashMap<>();
        couleurs.put("Programmation Java", "#e74c3c");
        couleurs.put("Base de Données", "#3498db");
        couleurs.put("Mathématiques", "#9b59b6");
        couleurs.put("Physique", "#1abc9c");
        couleurs.put("Réseaux Informatiques", "#f39c12");
        couleurs.put("Algorithmique", "#27ae60");
        couleurs.put("Systèmes d'Exploitation", "#e67e22");

        return couleurs.getOrDefault(matiere, "#95a5a6");
    }

    private int getHeureIndex(String heure) {
        for (int i = 0; i < heures.length; i++) {
            if (heures[i].equals(heure)) {
                return i;
            }
        }
        return -1;
    }

    @FXML
    private void handleImprimer() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Imprimer");
        alert.setHeaderText("Fonctionnalité d'impression");
        alert.setContentText("Cette fonctionnalité sera implémentée prochainement.");
        alert.showAndWait();
    }

    @FXML
    private void handleExporterPDF() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export PDF");
        alert.setHeaderText("Export en PDF");
        alert.setContentText("Cette fonctionnalité sera implémentée prochainement.");
        alert.showAndWait();
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) cbGroupe.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}