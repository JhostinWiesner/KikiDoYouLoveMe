package org.example.tareaintegradora2apo.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.tareaintegradora2apo.model.incidentes.Incidente;
import org.example.tareaintegradora2apo.model.vehiculos.Vehiculo;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controlador para la vista del Centro de Monitoreo.
 * Implementa el patrón Observer para recibir actualizaciones del simulador.
 */
public class CentroMonitoreoController implements Initializable, SimuladorSGMMS.Observer {
    
    @FXML private Label lblPuntuacion;
    @FXML private Label lblIncidentesActivos;
    @FXML private Label lblVehiculosDisponibles;
    @FXML private Label lblTiempoSimulacion;
    @FXML private Label lblEficiencia;
    @FXML private Label lblRanking;
    
    @FXML private Label lblAccidentes;
    @FXML private Label lblRobos;
    @FXML private Label lblIncendios;
    
    @FXML private Label lblPatrullas;
    @FXML private Label lblAmbulancias;
    @FXML private Label lblBomberos;
    
    @FXML private Label lblSemaforosRespetados;
    @FXML private Label lblSemaforosViolados;
    

    @FXML private Button btnVerMapa;
    @FXML private Button btnVerIncidentes;
    
    private SimuladorSGMMS simulador;
    private Stage primaryStage;
    
    /**
     * Establece el simulador y la ventana principal
     * @param simulador Instancia del simulador
     * @param primaryStage Ventana principal
     */
    public void setSimulador(SimuladorSGMMS simulador, Stage primaryStage) {
        this.simulador = simulador;
        this.primaryStage = primaryStage;
        
        // Registrarse como observador
        simulador.agregarObservador(this);
        
        // Actualizar estado inicial
        actualizarInterfaz();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar estado inicial de los botones

        
        // Configurar acciones de los botones

        btnVerMapa.setOnAction(e -> abrirVentanaMapa());
        btnVerIncidentes.setOnAction(e -> abrirVentanaIncidentes());
    }



    // Modifica el método abrirVentanaMapa() en CentroMonitoreoController.java
    private void abrirVentanaMapa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/mapaTrafico.fxml"));
            Parent root = loader.load();

            MapaTraficoController controller = loader.getController();
            // Pasar ambos parámetros: simulador y primaryStage
            controller.setSimulador(simulador, primaryStage);

            // Cambiar a usar la escena existente en lugar de crear una nueva ventana
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SGMMS - Mapa de Tráfico");

        } catch (IOException e) {
            System.err.println("Error al cargar mapa de tráfico: " + e.getMessage());
        }
    }

    // Modifica el método abrirVentanaIncidentes() en CentroMonitoreoController.java
    private void abrirVentanaIncidentes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/panelIncidentes.fxml"));
            Parent root = loader.load();

            PanelIncidentesController controller = loader.getController();
            // Pasar ambos parámetros: simulador y primaryStage
            controller.setSimulador(simulador, primaryStage);

            // Cambiar a usar la escena existente en lugar de crear una nueva ventana
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SGMMS - Panel de Incidentes");

        } catch (IOException e) {
            System.err.println("Error al cargar panel de incidentes: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la interfaz con los datos actuales
     */
    private void actualizarInterfaz() {
        if (simulador == null) {
            return;
        }
        
        Platform.runLater(() -> {
            Map<String, Object> estadisticas = simulador.obtenerEstadisticas();
            
            // Actualizar puntuación y tiempo
            lblPuntuacion.setText(String.valueOf(estadisticas.get("puntuacion")));
            lblTiempoSimulacion.setText(estadisticas.get("tiempo_simulacion") + " seg");
            lblRanking.setText(simulador.getGestorPuntuacion().calcularRanking());
            lblEficiencia.setText(String.format("%.1f%%", simulador.getGestorPuntuacion().calcularEficiencia()));
            
            // Actualizar contadores de incidentes
            lblIncidentesActivos.setText(String.valueOf(estadisticas.get("incidentes_totales")));
            lblAccidentes.setText(String.valueOf(estadisticas.get("accidentes")));
            lblRobos.setText(String.valueOf(estadisticas.get("robos")));
            lblIncendios.setText(String.valueOf(estadisticas.get("incendios")));
            
            // Actualizar contadores de vehículos
            lblVehiculosDisponibles.setText(String.valueOf(estadisticas.get("vehiculos_disponibles")));
            lblPatrullas.setText(String.valueOf(estadisticas.get("patrullas")));
            lblAmbulancias.setText(String.valueOf(estadisticas.get("ambulancias")));
            lblBomberos.setText(String.valueOf(estadisticas.get("bomberos")));
            
            // Actualizar estadísticas de semáforos
            lblSemaforosRespetados.setText(String.valueOf(estadisticas.get("semaforos_respetados")));
            lblSemaforosViolados.setText(String.valueOf(estadisticas.get("semaforos_violados")));
        });
    }
    
    // Implementación de SimuladorSGMMS.Observer
    
    @Override
    public void onIncidenteCreado(Incidente incidente) {
        actualizarInterfaz();
    }
    
    @Override
    public void onIncidenteAtendido(Incidente incidente) {
        actualizarInterfaz();
    }
    
    @Override
    public void onVehiculoCreado(Vehiculo vehiculo) {
        actualizarInterfaz();
    }
    
    @Override
    public void onVehiculoAsignado(Vehiculo vehiculo, Incidente incidente) {
        actualizarInterfaz();
    }
    
    @Override
    public void onPuntuacionActualizada(int puntuacion) {
        Platform.runLater(() -> lblPuntuacion.setText(String.valueOf(puntuacion)));
    }
    
    @Override
    public void onEstadisticasActualizadas(Map<String, Object> estadisticas) {
        actualizarInterfaz();
    }

    @Override
    public void onVehiculoDisponible(Vehiculo vehiculo) {

    }
}