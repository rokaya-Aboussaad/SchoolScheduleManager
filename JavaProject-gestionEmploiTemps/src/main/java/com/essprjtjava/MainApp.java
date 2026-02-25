package com.essprjtjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML avec le bon chemin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root, 900, 600);

            // Configurer le stage
            primaryStage.setTitle("Gestion des Emplois du Temps");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();

            // Afficher la fenêtre
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de l'application");
            System.err.println("Vérifiez que le fichier MainView.fxml existe dans src/main/resources/view/");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}