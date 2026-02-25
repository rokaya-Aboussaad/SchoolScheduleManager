package com.essprjtjava.controller;

import com.essprjtjava.model.*;
import com.essprjtjava.service.*;
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

    // Services
    private GroupeService groupeService;
    private ISeanceService seanceService;

    private String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};

    @FXML
    public void initialize() {
        // Initialiser les services
        groupeService = new GroupeService();
        seanceService = new SeanceServiceImpl();

        chargerGroupes();
    }

    private void chargerGroupes() {
        try {
            List<Groupe> groupes = groupeService.getAllGroupes();
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
            List<Seance> seances = seanceService.getSeancesByGroupe(groupe.getIdGroupe());

            vboxMessage.setVisible(false);
            vboxMessage.setManaged(false);
            gridEmploi.setVisible(true);
            gridEmploi.setManaged(true);

            gridEmploi.getChildren().clear();

            creerGrilleEmploi(seances);

            lblInfo.setText("Emploi du temps de " + groupe.getNomGroupe() + " - " + seances.size() + " séance(s)");

        } catch (Exception e) {
            showError("Erreur lors de l'affichage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void creerGrilleEmploi(List<Seance> seances) {
        gridEmploi.getChildren().clear();

        Map<String, List<Seance>> seancesParJour = new HashMap<>();
        for (String jour : jours) {
            seancesParJour.put(jour, new ArrayList<>());
        }

        for (Seance seance : seances) {
            seancesParJour.get(seance.getJour()).add(seance);
        }

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

                    // Matière + Professeur
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

                    // Salle avec type
                    Label lblSalle = new Label("📍 " + seance.getSalle().getNomSalle() +
                            " (" + seance.getSalle().getTypeSalle() + ")");
                    lblSalle.setFont(Font.font("System", FontWeight.BOLD, 13));
                    lblSalle.setStyle("-fx-text-fill: white;");
                    lblSalle.setPrefWidth(150);

                    seanceBox.getChildren().addAll(vboxHoraire, vboxMatiere, lblSalle);
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

    private String getCouleurMatiere(String matiere) {
        Map<String, String> couleurs = new HashMap<>();
        couleurs.put("Programmation Java", "#e74c3c");
        couleurs.put("Base de Données", "#3498db");
        couleurs.put("Mathématiques", "#9b59b6");
        couleurs.put("Algèbre", "#8e44ad");
        couleurs.put("Physique", "#1abc9c");
        couleurs.put("Réseaux Informatiques", "#f39c12");
        couleurs.put("Algorithmique", "#27ae60");
        couleurs.put("Systèmes d'Exploitation", "#e67e22");

        return couleurs.getOrDefault(matiere, "#95a5a6");
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