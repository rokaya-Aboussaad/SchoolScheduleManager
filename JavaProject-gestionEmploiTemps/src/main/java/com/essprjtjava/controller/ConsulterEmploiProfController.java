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

public class ConsulterEmploiProfController {

    @FXML private ComboBox<Professeur> cbProfesseur;
    @FXML private VBox vboxEmploi;
    @FXML private VBox vboxMessage;
    @FXML private GridPane gridEmploi;
    @FXML private Label lblInfo;

    private ProfesseurDAO professeurDAO;
    private SeanceDAO seanceDAO;

    private String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
    private String[] heures = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

    @FXML
    public void initialize() {
        professeurDAO = new ProfesseurDAO();
        seanceDAO = new SeanceDAO();

        chargerProfesseurs();
    }

    private void chargerProfesseurs() {
        try {
            List<Professeur> professeurs = professeurDAO.getAllProfesseurs();
            cbProfesseur.setItems(FXCollections.observableArrayList(professeurs));
        } catch (Exception e) {
            showError("Erreur de chargement des professeurs: " + e.getMessage());
        }
    }

    @FXML
    private void handleProfesseurSelection() {
        Professeur professeur = cbProfesseur.getValue();
        if (professeur != null) {
            afficherEmploiDuTemps(professeur);
        }
    }

    private void afficherEmploiDuTemps(Professeur professeur) {
        try {
            List<Seance> seances = seanceDAO.getSeancesByProfesseur(professeur.getIdProf());

            vboxMessage.setVisible(false);
            vboxMessage.setManaged(false);
            gridEmploi.setVisible(true);
            gridEmploi.setManaged(true);

            gridEmploi.getChildren().clear();

            creerGrilleEmploi(seances);

            lblInfo.setText("Emploi du temps de " + professeur.getNom() + " " + professeur.getPrenom() +
                    " (" + professeur.getSpecialite() + ") - " + seances.size() + " séance(s)");

        } catch (Exception e) {
            showError("Erreur lors de l'affichage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void creerGrilleEmploi(List<Seance> seances) {
        gridEmploi.getChildren().clear();

        // Organiser par jour
        Map<String, List<Seance>> seancesParJour = new HashMap<>();
        for (String jour : jours) {
            seancesParJour.put(jour, new ArrayList<>());
        }

        for (Seance seance : seances) {
            seancesParJour.get(seance.getJour()).add(seance);
        }

        // Trier par heure
        for (String jour : jours) {
            seancesParJour.get(jour).sort((s1, s2) ->
                    s1.getHeureDebut().compareTo(s2.getHeureDebut())
            );
        }

        VBox contenu = new VBox(15);
        contenu.setStyle("-fx-padding: 20;");

        for (String jour : jours) {
            List<Seance> seancesJour = seancesParJour.get(jour);

            if (!seancesJour.isEmpty()) {
                Label lblJour = new Label(jour);
                lblJour.setFont(Font.font("System", FontWeight.BOLD, 18));
                lblJour.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 10 0 5 0;");
                contenu.getChildren().add(lblJour);

                for (Seance seance : seancesJour) {
                    HBox seanceBox = new HBox(15);
                    seanceBox.setAlignment(Pos.CENTER_LEFT);
                    seanceBox.setStyle("-fx-background-color: #9b59b6; -fx-padding: 15; -fx-background-radius: 8;");

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

                    // Groupe + Matière
                    VBox vboxInfo = new VBox(3);
                    Label lblGroupe = new Label(seance.getGroupe().getNomGroupe());
                    lblGroupe.setFont(Font.font("System", FontWeight.BOLD, 16));
                    lblGroupe.setStyle("-fx-text-fill: white;");
                    Label lblMatiere = new Label(seance.getMatiere().getNomMatiere());
                    lblMatiere.setFont(Font.font("System", 13));
                    lblMatiere.setStyle("-fx-text-fill: white;");
                    vboxInfo.getChildren().addAll(lblGroupe, lblMatiere);
                    vboxInfo.setPrefWidth(250);

                    // Salle
                    Label lblSalle = new Label("📍 Salle " + seance.getSalle().getNomSalle());
                    lblSalle.setFont(Font.font("System", FontWeight.BOLD, 13));
                    lblSalle.setStyle("-fx-text-fill: white;");
                    lblSalle.setPrefWidth(120);

                    seanceBox.getChildren().addAll(vboxHoraire, vboxInfo, lblSalle);
                    contenu.getChildren().add(seanceBox);
                }

                Separator sep = new Separator();
                sep.setStyle("-fx-background-color: #bdc3c7;");
                contenu.getChildren().add(sep);
            }
        }

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
        cellule.setStyle("-fx-background-color: #9b59b6; -fx-border-color: white; -fx-border-width: 1; -fx-background-radius: 3;");

        // Horaire
        Label lblHoraire = new Label(
                seance.getHeureDebut().toString().substring(0, 5) + "-" +
                        seance.getHeureFin().toString().substring(0, 5)
        );
        lblHoraire.setFont(Font.font("System", FontWeight.BOLD, 11));
        lblHoraire.setStyle("-fx-text-fill: white;");

        // Groupe
        Label lblGroupe = new Label(seance.getGroupe().getNomGroupe());
        lblGroupe.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblGroupe.setStyle("-fx-text-fill: white;");

        // Matière
        Label lblMatiere = new Label(seance.getMatiere().getNomMatiere());
        lblMatiere.setFont(Font.font("System", 11));
        lblMatiere.setStyle("-fx-text-fill: white;");
        lblMatiere.setWrapText(true);

        // Salle
        Label lblSalle = new Label("📍 " + seance.getSalle().getNomSalle());
        lblSalle.setFont(Font.font("System", FontWeight.BOLD, 10));
        lblSalle.setStyle("-fx-text-fill: white;");

        cellule.getChildren().addAll(lblHoraire, lblGroupe, lblMatiere, lblSalle);

        Tooltip tooltip = new Tooltip(
                "Groupe: " + seance.getGroupe().getNomGroupe() + "\n" +
                        seance.getMatiere().getNomMatiere() + "\n" +
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

        Label lblVide = new Label("Libre");
        lblVide.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12; -fx-font-weight: bold;");
        cellule.getChildren().add(lblVide);

        return cellule;
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
    private void handleFermer() {
        Stage stage = (Stage) cbProfesseur.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}