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

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

public class ModifierSeanceController {

    @FXML private ComboBox<Groupe> cbGroupe;
    @FXML private VBox vboxModif;
    @FXML private VBox vboxEmploisGroupes;
    @FXML private ComboBox<String> cbJour;
    @FXML private ComboBox<String> cbHeureDebut;
    @FXML private ComboBox<String> cbHeureFin;
    @FXML private ComboBox<Professeur> cbProfesseur;
    @FXML private ComboBox<Salle> cbSalle;
    @FXML private Label lblMessage;

    private GroupeDAO groupeDAO;
    private SeanceDAO seanceDAO;
    private ProfesseurDAO professeurDAO;
    private SalleDAO salleDAO;
    private MatiereDAO matiereDAO;

    private Seance seanceSelectionnee = null;
    private String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};

    @FXML
    public void initialize() {
        groupeDAO = new GroupeDAO();
        seanceDAO = new SeanceDAO();
        professeurDAO = new ProfesseurDAO();
        salleDAO = new SalleDAO();
        matiereDAO = new MatiereDAO();

        chargerGroupes();
        initFormulaire();
        afficherTousLesEmplois();
    }

    private void chargerGroupes() {
        try {
            List<Groupe> groupes = groupeDAO.getAllGroupes();
            cbGroupe.setItems(FXCollections.observableArrayList(groupes));
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void initFormulaire() {
        try {
            // Jours
            cbJour.setItems(FXCollections.observableArrayList(jours));

            // Heures
            List<String> heures = new ArrayList<>();
            for (int h = 8; h <= 18; h++) {
                heures.add(String.format("%02d:00", h));
                heures.add(String.format("%02d:30", h));
            }
            cbHeureDebut.setItems(FXCollections.observableArrayList(heures));
            cbHeureFin.setItems(FXCollections.observableArrayList(heures));

            // Professeurs et Salles
            cbProfesseur.setItems(FXCollections.observableArrayList(professeurDAO.getAllProfesseurs()));
            cbSalle.setItems(FXCollections.observableArrayList(salleDAO.getAllSalles()));

        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleGroupeSelection() {
        if (cbGroupe.getValue() != null) {
            afficherTousLesEmplois();
        }
    }

    @FXML
    private void handleActualiser() {
        afficherTousLesEmplois();
        showSuccess("✅ Emplois du temps actualisés!");
    }

    private void afficherTousLesEmplois() {
        vboxEmploisGroupes.getChildren().clear();

        Label titre = new Label("📅 Emplois du Temps - Cliquez sur une séance pour la modifier");
        titre.setFont(Font.font("System", FontWeight.BOLD, 20));
        titre.setStyle("-fx-text-fill: #2c3e50;");
        vboxEmploisGroupes.getChildren().add(titre);

        try {
            List<Groupe> groupes = groupeDAO.getAllGroupes();
            Groupe filtreGroupe = cbGroupe.getValue();

            // Si un groupe est sélectionné, n'afficher que lui
            if (filtreGroupe != null) {
                groupes = List.of(filtreGroupe);
            }

            for (Groupe groupe : groupes) {
                List<Seance> seances = seanceDAO.getSeancesByGroupe(groupe.getIdGroupe());

                VBox carteGroupe = new VBox(10);
                carteGroupe.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

                HBox headerBox = new HBox(10);
                headerBox.setAlignment(Pos.CENTER_LEFT);

                Label lblGroupe = new Label(groupe.getNomGroupe());
                lblGroupe.setFont(Font.font("System", FontWeight.BOLD, 18));
                lblGroupe.setStyle("-fx-text-fill: #f39c12;");

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
                                HBox seanceBox = creerSeanceClickable(seance);
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
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HBox creerSeanceClickable(Seance seance) {
        HBox seanceBox = new HBox(10);
        seanceBox.setAlignment(Pos.CENTER_LEFT);
        seanceBox.setStyle("-fx-background-color: #f39c12; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");
        seanceBox.setPrefHeight(60);

        Label lblHoraire = new Label(
                seance.getHeureDebut().toString().substring(0, 5) + " - " +
                        seance.getHeureFin().toString().substring(0, 5)
        );
        lblHoraire.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-min-width: 90; -fx-font-size: 13;");

        Label lblMatiere = new Label(seance.getMatiere().getNomMatiere());
        lblMatiere.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 180; -fx-font-size: 13;");

        Label lblProf = new Label(
                seance.getProfesseur().getNom() + " " +
                        seance.getProfesseur().getPrenom()
        );
        lblProf.setStyle("-fx-text-fill: white; -fx-min-width: 150; -fx-font-size: 12;");

        Label lblSalle = new Label("📍 " + seance.getSalle().getNomSalle());
        lblSalle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        seanceBox.getChildren().addAll(lblHoraire, lblMatiere, lblProf, lblSalle);

        // Effet hover
        seanceBox.setOnMouseEntered(e ->
                seanceBox.setStyle("-fx-background-color: #e67e22; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;"));
        seanceBox.setOnMouseExited(e ->
                seanceBox.setStyle("-fx-background-color: #f39c12; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;"));

        // Click pour modifier
        seanceBox.setOnMouseClicked(e -> chargerSeancePourModif(seance));

        return seanceBox;
    }

    private void chargerSeancePourModif(Seance seance) {
        seanceSelectionnee = seance;

        cbJour.setValue(seance.getJour());
        cbHeureDebut.setValue(seance.getHeureDebut().toString().substring(0, 5));
        cbHeureFin.setValue(seance.getHeureFin().toString().substring(0, 5));
        cbProfesseur.setValue(seance.getProfesseur());
        cbSalle.setValue(seance.getSalle());

        vboxModif.setVisible(true);
        vboxModif.setManaged(true);

        lblMessage.setText("ℹ️ Modification de la séance: " + seance.getGroupe().getNomGroupe() +
                " - " + seance.getJour() + " " +
                seance.getHeureDebut().toString().substring(0, 5));
        lblMessage.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13; -fx-font-weight: bold;");
    }

    @FXML
    private void handleEnregistrerModif() {
        if (seanceSelectionnee == null) return;

        if (!validerChamps()) return;

        try {
            Seance seanceModif = new Seance();
            seanceModif.setIdSeance(seanceSelectionnee.getIdSeance());
            seanceModif.setJour(cbJour.getValue());
            seanceModif.setHeureDebut(Time.valueOf(LocalTime.parse(cbHeureDebut.getValue())));
            seanceModif.setHeureFin(Time.valueOf(LocalTime.parse(cbHeureFin.getValue())));
            seanceModif.setGroupe(seanceSelectionnee.getGroupe());
            seanceModif.setProfesseur(cbProfesseur.getValue());
            seanceModif.setSalle(cbSalle.getValue());

            Matiere matiere = matiereDAO.getMatiereByNom(cbProfesseur.getValue().getSpecialite());
            seanceModif.setMatiere(matiere);

            if (seanceDAO.verifierConflitSalleExcept(seanceModif, seanceSelectionnee.getIdSeance())) {
                showError("❌ La salle est déjà occupée!");
                return;
            }

            if (seanceDAO.verifierConflitProfesseurExcept(seanceModif, seanceSelectionnee.getIdSeance())) {
                showError("❌ Le professeur a déjà cours!");
                return;
            }

            if (seanceDAO.verifierConflitGroupeExcept(seanceModif, seanceSelectionnee.getIdSeance())) {
                showError("❌ Le groupe a déjà cours!");
                return;
            }

            if (seanceDAO.modifierSeance(seanceModif)) {
                showSuccess("✅ Séance modifiée!");
                afficherTousLesEmplois();
                handleAnnuler();
            }

        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerSeance() {
        if (seanceSelectionnee == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer cette séance ?");
        confirm.setContentText(
                seanceSelectionnee.getGroupe().getNomGroupe() + "\n" +
                        seanceSelectionnee.getJour() + " " +
                        seanceSelectionnee.getHeureDebut().toString().substring(0, 5) + "-" +
                        seanceSelectionnee.getHeureFin().toString().substring(0, 5) + "\n" +
                        seanceSelectionnee.getMatiere().getNomMatiere()
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (seanceDAO.supprimerSeance(seanceSelectionnee.getIdSeance())) {
                    showSuccess("✅ Séance supprimée!");
                    afficherTousLesEmplois();
                    handleAnnuler();
                }
            } catch (Exception e) {
                showError("Erreur: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        seanceSelectionnee = null;
        vboxModif.setVisible(false);
        vboxModif.setManaged(false);
        lblMessage.setText("");
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) cbGroupe.getScene().getWindow();
        stage.close();
    }

    private boolean validerChamps() {
        if (cbJour.getValue() == null || cbHeureDebut.getValue() == null ||
                cbHeureFin.getValue() == null || cbProfesseur.getValue() == null ||
                cbSalle.getValue() == null) {
            showError("Veuillez remplir tous les champs!");
            return false;
        }

        LocalTime debut = LocalTime.parse(cbHeureDebut.getValue());
        LocalTime fin = LocalTime.parse(cbHeureFin.getValue());
        if (!fin.isAfter(debut)) {
            showError("L'heure de fin doit être après l'heure de début!");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13; -fx-font-weight: bold;");
    }
}