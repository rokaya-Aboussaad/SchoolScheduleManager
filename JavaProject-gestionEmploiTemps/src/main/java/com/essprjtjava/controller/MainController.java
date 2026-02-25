package com.essprjtjava.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Button btnCreerSeance;

    @FXML
    private Button btnModifierSeance;

    @FXML
    private Button btnConsulterGroupe;

    @FXML
    private Button btnConsulterProf;

    @FXML
    private Button btnGererGroupes;

    @FXML
    private Button btnGererProfs;

    @FXML
    private Button btnGererSalles;

    @FXML
    private Button btnGererMatieres;

    @FXML
    public void initialize() {
        // Ajout d'effets hover sur les boutons
        addHoverEffect(btnCreerSeance);
        addHoverEffect(btnModifierSeance);
        addHoverEffect(btnConsulterGroupe);
        addHoverEffect(btnConsulterProf);
        addHoverEffect(btnGererGroupes);
        addHoverEffect(btnGererProfs);
        addHoverEffect(btnGererSalles);
        addHoverEffect(btnGererMatieres);
    }

    @FXML
    private void handleCreerSeance() {
        openWindow("CreerSeanceView.fxml", "Créer un Emploi du Temps", 1000, 700);
    }

    @FXML
    private void handleModifierSeance() {
        openWindow("ModifierSeanceView.fxml", "Modifier / Supprimer des Séances", 1200, 800);
    }

    @FXML
    private void handleConsulterGroupe() {
        openWindow("ConsulterEmploiGroupeView.fxml", "Emploi du Temps - Groupe", 1000, 700);
    }

    @FXML
    private void handleConsulterProf() {
        openWindow("ConsulterEmploiProfView.fxml", "Emploi du Temps - Professeur", 1000, 700);
    }

    @FXML
    private void handleGererGroupes() {
        openWindow("GererGroupesView.fxml", "Gestion des Groupes", 700, 500);
    }

    @FXML
    private void handleGererProfs() {
        openWindow("GererProfsView.fxml", "Gestion des Professeurs", 800, 500);
    }

    @FXML
    private void handleGererSalles() {
        openWindow("GererSallesView.fxml", "Gestion des Salles", 700, 500);
    }

    @FXML
    private void handleGererMatieres() {
        openWindow("GererMatieresView.fxml", "Gestion des Matières", 700, 500);
    }

    /**
     * Ouvre une nouvelle fenêtre
     */
    private void openWindow(String fxmlFile, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();

        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la fenêtre: " + title,
                    "Le fichier " + fxmlFile + " n'existe pas encore.");
            e.printStackTrace();
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Ajoute un effet hover aux boutons
     */
    private void addHoverEffect(Button button) {
        String originalStyle = button.getStyle();

        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle + "-fx-opacity: 0.8; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });
    }
}