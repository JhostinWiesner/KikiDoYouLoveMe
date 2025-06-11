package org.example.tareaintegradora2apo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.controller.MenuPrincipalController;



public class Main extends Application {

    private SimuladorSGMMS simulador;

    @Override
    public void start(Stage primaryStage) {
        try {

            simulador = new SimuladorSGMMS();

            // Cargar la pantalla principal (el menu)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/menuPrincipal.fxml"));
            Parent root = loader.load();


            MenuPrincipalController controller = loader.getController();
            controller.setPrimaryStage(primaryStage, simulador);


            Scene scene = new Scene(root);
            primaryStage.setTitle("SGMMS - Sistema de Gestión y Monitoreo de Movilidad y Seguridad");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);


            primaryStage.setOnCloseRequest(event -> {
                if (simulador != null && simulador.isSimulacionActiva()) {
                    simulador.detenerSimulacion();
                }
                System.exit(0);
            });

            // Mostrar la ventana
            primaryStage.show();


            System.out.println("=== Sistema SGMMS Iniciado ===");
            System.out.println("Simulador de Gestión y Monitoreo de Movilidad y Seguridad");
            System.out.println("=============================");

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

        if (simulador != null && simulador.isSimulacionActiva()) {
            simulador.detenerSimulacion();
        }
        System.out.println("=== Sistema SGMMS Detenido ===");
    }


    public static void main(String[] args) {

        launch(args);
    }
}