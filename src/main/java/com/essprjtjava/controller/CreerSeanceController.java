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

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreerSeanceController {

    @FXML private ComboBox<Groupe> cbGroupe;
    @FXML private VBox vboxEmploi;
    @FXML private GridPane gridEmploi;
    @FXML private VBox vboxEmploisGroupes;
    @FXML private Label lblMessage;
    @FXML private Button btnEnregistrer;
    @FXML private Button btnAnnuler;

    // Services au lieu de DAO
    private GroupeService groupeService;
    private MatiereService matiereService;
    private SalleService salleService;
    private ISeanceService seanceService;

    private List<SeanceRow> seancesList = new ArrayList<>();
    private int rowIndex = 0;

    private class SeanceRow {
        ComboBox<String> cbJour;
        ComboBox<String> cbHeureDebut;
        ComboBox<String> cbHeureFin;
        ComboBox<Matiere> cbMatiere;
        ComboBox<Salle> cbSalle;

        public SeanceRow() {
            cbJour = new ComboBox<>();
            cbHeureDebut = new ComboBox<>();
            cbHeureFin = new ComboBox<>();
            cbMatiere = new ComboBox<>();
            cbSalle = new ComboBox<>();
        }
    }

    @FXML
    public void initialize() {
        // Initialiser les services
        groupeService = new GroupeService();
        matiereService = new MatiereService();
        salleService = new SalleService();
        seanceService = new SeanceServiceImpl();

        chargerGroupes();
        vboxEmploi.setDisable(true);

        afficherTousLesEmplois();

        addHoverEffect(btnEnregistrer);
        addHoverEffect(btnAnnuler);
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
        if (cbGroupe.getValue() != null) {
            vboxEmploi.setDisable(false);
            gridEmploi.getChildren().clear();
            seancesList.clear();
            rowIndex = 0;

            ajouterEntetes();

            for (int i = 0; i < 5; i++) {
                ajouterLigneSeance();
            }
        }
    }

    private void ajouterEntetes() {
        String[] entetes = {"Jour", "Heure Début", "Heure Fin", "Matière / Professeur", "Salle", "Action"};

        for (int i = 0; i < entetes.length; i++) {
            Label label = new Label(entetes[i]);
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: #2c3e50;");
            label.setPadding(new Insets(5));
            gridEmploi.add(label, i, 0);
        }
    }

    @FXML
    private void handleAjouterSeance() {
        ajouterLigneSeance();
    }

    private void ajouterLigneSeance() {
        rowIndex++;
        SeanceRow row = new SeanceRow();

        row.cbJour.setItems(FXCollections.observableArrayList(
                "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"
        ));
        row.cbJour.setPromptText("Jour");
        row.cbJour.setPrefWidth(120);

        List<String> heures = new ArrayList<>();
        for (int h = 8; h <= 18; h++) {
            heures.add(String.format("%02d:00", h));
            heures.add(String.format("%02d:30", h));
        }
        row.cbHeureDebut.setItems(FXCollections.observableArrayList(heures));
        row.cbHeureDebut.setPromptText("08:00");
        row.cbHeureDebut.setPrefWidth(100);

        row.cbHeureFin.setItems(FXCollections.observableArrayList(heures));
        row.cbHeureFin.setPromptText("10:00");
        row.cbHeureFin.setPrefWidth(100);

        try {
            List<Matiere> matieres = matiereService.getAllMatieres();
            row.cbMatiere.setItems(FXCollections.observableArrayList(matieres));
            row.cbMatiere.setPromptText("Sélectionner matière");
            row.cbMatiere.setPrefWidth(300);
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }

        try {
            List<Salle> salles = salleService.getAllSalles();
            row.cbSalle.setItems(FXCollections.observableArrayList(salles));
            row.cbSalle.setPromptText("Salle");
            row.cbSalle.setPrefWidth(150);
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }

        Button btnSupprimer = new Button("🗑");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14;");
        final int currentRow = rowIndex;
        btnSupprimer.setOnAction(e -> supprimerLigne(currentRow));

        gridEmploi.add(row.cbJour, 0, rowIndex);
        gridEmploi.add(row.cbHeureDebut, 1, rowIndex);
        gridEmploi.add(row.cbHeureFin, 2, rowIndex);
        gridEmploi.add(row.cbMatiere, 3, rowIndex);
        gridEmploi.add(row.cbSalle, 4, rowIndex);
        gridEmploi.add(btnSupprimer, 5, rowIndex);

        seancesList.add(row);
    }

    private void supprimerLigne(int row) {
        gridEmploi.getChildren().clear();
        seancesList.remove(row - 1);
        rowIndex = 0;

        ajouterEntetes();

        List<SeanceRow> temp = new ArrayList<>(seancesList);
        seancesList.clear();

        for (SeanceRow seanceRow : temp) {
            rowIndex++;
            gridEmploi.add(seanceRow.cbJour, 0, rowIndex);
            gridEmploi.add(seanceRow.cbHeureDebut, 1, rowIndex);
            gridEmploi.add(seanceRow.cbHeureFin, 2, rowIndex);
            gridEmploi.add(seanceRow.cbMatiere, 3, rowIndex);
            gridEmploi.add(seanceRow.cbSalle, 4, rowIndex);

            Button btnSupprimer = new Button("🗑");
            btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14;");
            final int currentRow = rowIndex;
            btnSupprimer.setOnAction(e -> supprimerLigne(currentRow));
            gridEmploi.add(btnSupprimer, 5, rowIndex);

            seancesList.add(seanceRow);
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (cbGroupe.getValue() == null) {
            showError("Veuillez sélectionner un groupe!");
            return;
        }

        if (seancesList.isEmpty()) {
            showError("Veuillez ajouter au moins une séance!");
            return;
        }

        try {
            int seancesCreees = 0;

            for (SeanceRow row : seancesList) {
                if (row.cbJour.getValue() == null || row.cbHeureDebut.getValue() == null ||
                        row.cbHeureFin.getValue() == null || row.cbMatiere.getValue() == null ||
                        row.cbSalle.getValue() == null) {
                    continue;
                }

                Seance seance = new Seance();
                seance.setJour(row.cbJour.getValue());
                seance.setHeureDebut(Time.valueOf(LocalTime.parse(row.cbHeureDebut.getValue())));
                seance.setHeureFin(Time.valueOf(LocalTime.parse(row.cbHeureFin.getValue())));
                seance.setGroupe(cbGroupe.getValue());
                seance.setMatiere(row.cbMatiere.getValue());
                seance.setSalle(row.cbSalle.getValue());

                // Utiliser le service qui gère automatiquement les conflits
                try {
                    seanceService.creerSeance(seance);
                    seancesCreees++;
                } catch (ConflitException ce) {
                    showError(ce.getMessageDetaille());
                    continue;
                }
            }

            if (seancesCreees > 0) {
                showSuccess(seancesCreees + " séance(s) créée(s) pour " + cbGroupe.getValue());

                gridEmploi.getChildren().clear();
                seancesList.clear();
                rowIndex = 0;
                vboxEmploi.setDisable(true);
                cbGroupe.setValue(null);

                afficherTousLesEmplois();
            }

        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleActualiser() {
        afficherTousLesEmplois();
        showSuccess("Emplois du temps actualisés!");
    }

    private void afficherTousLesEmplois() {
        vboxEmploisGroupes.getChildren().clear();

        Label titre = new Label("📅 Emplois du Temps des Groupes");
        titre.setFont(Font.font("System", FontWeight.BOLD, 20));
        titre.setStyle("-fx-text-fill: #2c3e50;");
        vboxEmploisGroupes.getChildren().add(titre);

        try {
            List<Groupe> groupes = groupeService.getAllGroupes();

            for (Groupe groupe : groupes) {
                List<Seance> seances = seanceService.getSeancesByGroupe(groupe.getIdGroupe());

                VBox carteGroupe = new VBox(10);
                carteGroupe.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

                HBox headerBox = new HBox(10);
                headerBox.setAlignment(Pos.CENTER_LEFT);

                Label lblGroupe = new Label(groupe.getNomGroupe());
                lblGroupe.setFont(Font.font("System", FontWeight.BOLD, 18));
                lblGroupe.setStyle("-fx-text-fill: #27ae60;");

                Label lblNbSeances = new Label("(" + seances.size() + " séances)");
                lblNbSeances.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14;");

                headerBox.getChildren().addAll(lblGroupe, lblNbSeances);
                carteGroupe.getChildren().add(headerBox);

                if (seances.isEmpty()) {
                    Label lblVide = new Label("Aucune séance planifiée");
                    lblVide.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
                    carteGroupe.getChildren().add(lblVide);
                } else {
                    Map<String, List<Seance>> seancesParJour = new HashMap<>();
                    String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};

                    for (String jour : jours) {
                        seancesParJour.put(jour, new ArrayList<>());
                    }

                    for (Seance seance : seances) {
                        seancesParJour.get(seance.getJour()).add(seance);
                    }

                    GridPane gridSeances = new GridPane();
                    gridSeances.setHgap(15);
                    gridSeances.setVgap(8);
                    gridSeances.setPadding(new Insets(10, 0, 0, 0));

                    int row = 0;
                    for (String jour : jours) {
                        List<Seance> seancesJour = seancesParJour.get(jour);

                        if (!seancesJour.isEmpty()) {
                            Label lblJour = new Label(jour);
                            lblJour.setFont(Font.font("System", FontWeight.BOLD, 14));
                            lblJour.setStyle("-fx-text-fill: #34495e; -fx-min-width: 100;");
                            gridSeances.add(lblJour, 0, row);

                            VBox vboxJour = new VBox(5);
                            for (Seance seance : seancesJour) {
                                HBox seanceBox = new HBox(10);
                                seanceBox.setAlignment(Pos.CENTER_LEFT);
                                seanceBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 8; -fx-background-radius: 5;");

                                Label lblHoraire = new Label(
                                        seance.getHeureDebut().toString().substring(0, 5) + " - " +
                                                seance.getHeureFin().toString().substring(0, 5)
                                );
                                lblHoraire.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-min-width: 90;");

                                Label lblMatiere = new Label(seance.getMatiere().getNomMatiere());
                                lblMatiere.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-min-width: 180;");

                                Label lblProf = new Label(
                                        seance.getProfesseur().getNom() + " " +
                                                seance.getProfesseur().getPrenom()
                                );
                                lblProf.setStyle("-fx-text-fill: #7f8c8d; -fx-min-width: 150;");

                                Label lblSalle = new Label("📍 " + seance.getSalle().getNomSalle() +
                                        " (" + seance.getSalle().getTypeSalle() + ")");
                                lblSalle.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

                                seanceBox.getChildren().addAll(lblHoraire, lblMatiere, lblProf, lblSalle);
                                vboxJour.getChildren().add(seanceBox);
                            }

                            gridSeances.add(vboxJour, 1, row);
                            row++;
                        }
                    }

                    carteGroupe.getChildren().add(gridSeances);
                }

                vboxEmploisGroupes.getChildren().add(carteGroupe);
            }

        } catch (Exception e) {
            showError("Erreur lors de l'affichage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        lblMessage.setText("❌ " + message);
        lblMessage.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        lblMessage.setText("✅ " + message);
        lblMessage.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14; -fx-font-weight: bold;");
    }

    private void addHoverEffect(Button button) {
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> button.setStyle(originalStyle + "-fx-opacity: 0.8;"));
        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }
}