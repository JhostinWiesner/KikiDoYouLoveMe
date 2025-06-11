package org.example.tareaintegradora2apo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MenuPrincipalController implements Initializable {
    
    @FXML private Button btnJugar;
    @FXML private Button btnManual;
    @FXML private Button btnSalir;
    
    private Stage primaryStage;
    private SimuladorSGMMS simulador;
    


    public void setPrimaryStage(Stage primaryStage, SimuladorSGMMS simulador) {
        this.primaryStage = primaryStage;
        this.simulador = simulador;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar acciones de los botones
        btnJugar.setOnAction(e -> iniciarJuego());
        btnManual.setOnAction(e -> abrirManual());
        btnSalir.setOnAction(e -> salirAplicacion());
    }
    

    private void iniciarJuego() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/mapaTrafico.fxml"));
            Parent root = loader.load();
            
            MapaTraficoController controller = loader.getController();
            controller.setSimulador(simulador, primaryStage);
            
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SGMMS - Mapa de Tráfico");
            
            // Iniciar la simulación automáticamente
            if (!simulador.isSimulacionActiva()) {
                simulador.iniciarSimulacion();
            }
            
        } catch (IOException e) {
            System.err.println("Error al cargar el mapa de tráfico: " + e.getMessage());
        }
    }
    

    private void abrirManual() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/manualUsuario.fxml"));
            Parent root = loader.load();
            
            ManualUsuarioController controller = loader.getController();
            controller.setPrimaryStage(primaryStage, simulador);
            
            Stage manualStage = new Stage();
            manualStage.setTitle("Manual del Usuario - SGMMS");
            manualStage.setScene(new Scene(root));
            manualStage.show();
            
        } catch (IOException e) {
            System.err.println("Error al abrir manual: " + e.getMessage());
        }
    }
    

    private void salirAplicacion() {
        if (simulador != null && simulador.isSimulacionActiva()) {
            simulador.detenerSimulacion();
        }
        System.exit(0);
    }
}