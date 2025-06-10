package org.example.tareaintegradora2apo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.controller.MenuPrincipalController;


/**
 * Clase principal del Sistema de Gestión y Monitoreo de Movilidad y Seguridad (SGMMS).
 * Inicia la aplicación JavaFX y configura la ventana principal.
 */
public class Main extends Application {

    private SimuladorSGMMS simulador;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Crear instancia del simulador
            simulador = new SimuladorSGMMS();

            // Cargar la pantalla principal (menú)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/menuPrincipal.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y configurarlo
            MenuPrincipalController controller = loader.getController();
            controller.setPrimaryStage(primaryStage, simulador);

            // Configurar la escena principal
            Scene scene = new Scene(root);
            primaryStage.setTitle("SGMMS - Sistema de Gestión y Monitoreo de Movilidad y Seguridad");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Configurar evento de cierre
            primaryStage.setOnCloseRequest(event -> {
                if (simulador != null && simulador.isSimulacionActiva()) {
                    simulador.detenerSimulacion();
                }
                System.exit(0);
            });

            // Mostrar la ventana
            primaryStage.show();

            // Mensaje de bienvenida en consola
            System.out.println("=== Sistema SGMMS Iniciado ===");
            System.out.println("Simulador de Gestión y Monitoreo de Movilidad y Seguridad");
            System.out.println("Ciudad: Palmira");
            System.out.println("Versión: 2.0");
            System.out.println("=============================");

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Detener el simulador al cerrar la aplicación
        if (simulador != null && simulador.isSimulacionActiva()) {
            simulador.detenerSimulacion();
        }
        System.out.println("=== Sistema SGMMS Detenido ===");
    }

    /**
     * Método principal de la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Lanzar la aplicación JavaFX
        launch(args);
    }
}