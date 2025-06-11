package org.example.tareaintegradora2apo.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.tareaintegradora2apo.model.incidentes.Incendio;
import org.example.tareaintegradora2apo.model.incidentes.Incidente;
import org.example.tareaintegradora2apo.model.vehiculos.Ambulancia;
import org.example.tareaintegradora2apo.model.vehiculos.Bombero;
import org.example.tareaintegradora2apo.model.vehiculos.Vehiculo;
import org.example.tareaintegradora2apo.model.incidentes.Robo;
import org.example.tareaintegradora2apo.model.incidentes.Accidente;


import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para la vista del Panel de Incidentes.
 * Muestra la lista detallada de incidentes y permite su gestión.
 */
public class PanelIncidentesController implements Initializable, SimuladorSGMMS.Observer {

    @FXML private TableView<Incidente> tablaIncidentes;
    @FXML private TableColumn<Incidente, String> colId;
    @FXML private TableColumn<Incidente, String> colTipo;
    @FXML private TableColumn<Incidente, Integer> colPrioridad;
    @FXML private TableColumn<Incidente, String> colHoraInicio;
    @FXML private TableColumn<Incidente, String> colEstado;
    @FXML private TableColumn<Incidente, String> colTiempoTranscurrido;

    @FXML private ListView<Vehiculo> listaVehiculos;
    @FXML private TextArea areaDetalles;

    @FXML private Label lblTotalIncidentes;
    @FXML private Label lblIncidentesAtendidos;
    @FXML private Label lblIncidentesPendientes;
    @FXML private Label lblTiempoPromedioRespuesta;

    @FXML private Button btnAsignarVehiculo;
    @FXML private Button btnMarcarAtendido;
    @FXML private Button btnActualizar;
    @FXML private Button btnManual;
    @FXML private Button btnMapa;
    @FXML private Button btnCentroMonitoreo;

    private SimuladorSGMMS simulador;
    private Stage primaryStage;
    private ObservableList<Incidente> incidentes;
    private ObservableList<Vehiculo> vehiculos;

    /**
     * Establece el simulador
     * @param simulador Instancia del simulador
     */
    public void setSimulador(SimuladorSGMMS simulador,Stage primaryStage) {
        this.simulador = simulador;
        this.primaryStage = primaryStage;
        simulador.agregarObservador(this);
        actualizarDatos();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas de la tabla de incidentes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTipo.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colHoraInicio.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        colEstado.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isAtendido() ? "Atendido" : "Pendiente"));
        colTiempoTranscurrido.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTiempoTranscurrido() + " seg"));

        // Configurar listas observables
        incidentes = FXCollections.observableArrayList();
        vehiculos = FXCollections.observableArrayList();

        tablaIncidentes.setItems(incidentes);
        listaVehiculos.setItems(vehiculos);

        // Configurar selección de incidentes
        tablaIncidentes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetallesIncidente(newSelection);
                }
            });

        // Configurar botones
        btnAsignarVehiculo.setOnAction(e -> asignarVehiculoAIncidente());
        btnMarcarAtendido.setOnAction(e -> marcarIncidenteAtendido());
        btnActualizar.setOnAction(e -> actualizarDatos());
        btnManual.setOnAction(e -> abrirManual());
        btnMapa.setOnAction(e -> irAMapa());
        btnCentroMonitoreo.setOnAction(e -> irACentroMonitoreo());

        // Configurar actualización automática cada 2 segundos
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(2),
                e -> actualizarTiemposTranscurridos()
            )
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();

       // actualizarVehiculosDisponibles();
    }
    private void actualizarVehiculosDisponibles() {
        vehiculos.clear();
        vehiculos.addAll(simulador.getVehiculos().stream()
                .filter(Vehiculo::isDisponible)
                .toList());
    }

    /**
     * Abre el manual del usuario
     */
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

    /**
     * Va al mapa de tráfico
     */
    private void irAMapa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/mapaTrafico.fxml"));
            Parent root = loader.load();

            MapaTraficoController controller = loader.getController();
            controller.setSimulador(simulador, primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SGMMS - Mapa de Tráfico");

        } catch (IOException e) {
            System.err.println("Error al cargar mapa de tráfico: " + e.getMessage());
        }
    }

    /**
     * Va al centro de monitoreo
     */
    private void irACentroMonitoreo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/centroMonitoreo.fxml"));
            Parent root = loader.load();

            CentroMonitoreoController controller = loader.getController();
            controller.setSimulador(simulador, primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SGMMS - Centro de Monitoreo");

        } catch (IOException e) {
            System.err.println("Error al cargar centro de monitoreo: " + e.getMessage());
        }
    }


    /**
     * Actualiza los datos mostrados en la interfaz
     */
    private void actualizarDatos() {
        if (simulador == null) {
            return;
        }

        Platform.runLater(() -> {
            // Actualizar lista de incidentes
            incidentes.clear();
            incidentes.addAll(simulador.getIncidentesActivos());

            // Actualizar lista de vehículos disponibles
            vehiculos.clear();
            vehiculos.addAll(simulador.getVehiculos().stream()
                .filter(Vehiculo::isDisponible)
                .toList());
            // Actualizar estadísticas
            actualizarEstadisticas();
        });




    }

    /**
     * Actualiza las estadísticas mostradas
     */
    private void actualizarEstadisticas() {
        if (simulador == null) {
            return;
        }

        Map<String, Object> estadisticas = simulador.obtenerEstadisticas();

        lblTotalIncidentes.setText(String.valueOf(estadisticas.get("incidentes_totales")));
        lblIncidentesAtendidos.setText(String.valueOf(simulador.getGestorPuntuacion().getIncidentesAtendidos()));
        lblIncidentesPendientes.setText(String.valueOf(
            (Integer) estadisticas.get("incidentes_totales") - simulador.getGestorPuntuacion().getIncidentesAtendidos()));
        lblTiempoPromedioRespuesta.setText(simulador.getGestorPuntuacion().getTiempoPromedioRespuesta() + " seg");
    }

    /**
     * Actualiza los tiempos transcurridos en la tabla
     */
    private void actualizarTiemposTranscurridos() {
        Platform.runLater(() -> {
            tablaIncidentes.refresh();
            actualizarEstadisticas();
        });
    }

    /**
     * Muestra los detalles del incidente seleccionado
     * @param incidente Incidente seleccionado
     */
    private void mostrarDetallesIncidente(Incidente incidente) {
        StringBuilder detalles = new StringBuilder();

        detalles.append("ID: ").append(incidente.getId()).append("\n");
        detalles.append("Tipo: ").append(incidente.getTipo()).append("\n");
        detalles.append("Prioridad: ").append(incidente.getPrioridad()).append("\n");
        detalles.append("Posición: ").append(String.format("(%.0f, %.0f)",
            incidente.getPosicion().getX(), incidente.getPosicion().getY())).append("\n");
        detalles.append("Hora de inicio: ").append(
            incidente.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
        detalles.append("Tiempo transcurrido: ").append(incidente.getTiempoTranscurrido()).append(" segundos\n");
        detalles.append("Estado: ").append(incidente.isAtendido() ? "Atendido" : "Pendiente").append("\n");

        if (incidente.isAtendido() && incidente.getHoraAtencion() != null) {
            detalles.append("Hora de atención: ").append(
                incidente.getHoraAtencion().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
            detalles.append("Tiempo de respuesta: ").append(incidente.getTiempoRespuesta()).append(" segundos\n");
        }

        // Detalles específicos según el tipo de incidente
        if (incidente instanceof Accidente) {
            Accidente accidente = (Accidente) incidente;
            detalles.append("Causa: ").append(accidente.getCausa()).append("\n");
            detalles.append("Vehículos involucrados: ").append(accidente.getVehiculosInvolucrados().size()).append("\n");
        } else if (incidente instanceof Robo) {
            Robo robo = (Robo) incidente;
            detalles.append("Tipo de robo: ").append(robo.getTipoRobo()).append("\n");
        } else if (incidente instanceof Incendio) {
            Incendio incendio = (Incendio) incidente;
            detalles.append("Tipo de incendio: ").append(incendio.getTipoIncendio()).append("\n");
            detalles.append("Intensidad: ").append(incendio.getIntensidad()).append("/5\n");
        }
        
        areaDetalles.setText(detalles.toString());
    }
    
    /**
     * Asigna un vehículo al incidente seleccionado
     */
    private void asignarVehiculoAIncidente() {
        Incidente incidenteSeleccionado = tablaIncidentes.getSelectionModel().getSelectedItem();
        Vehiculo vehiculoSeleccionado = listaVehiculos.getSelectionModel().getSelectedItem();
        
        if (incidenteSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un incidente.");
            return;
        }
        
        if (vehiculoSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un vehículo disponible.");
            return;
        }
        
        if (incidenteSeleccionado.isAtendido()) {
            mostrarAlerta("Error", "El incidente ya ha sido atendido.");
            return;
        }
        
        // Asignar vehículo al incidente
        vehiculoSeleccionado.asignarIncidente(incidenteSeleccionado, simulador.getMapa().getGrafo());

        actualizarDatos();

        // Activar modo emergencia si es necesario
        if (vehiculoSeleccionado instanceof Ambulancia) {
            ((Ambulancia) vehiculoSeleccionado).activarEmergencia();
        } else if (vehiculoSeleccionado instanceof Bombero) {
            ((Bombero) vehiculoSeleccionado).activarEmergencia();
        }
        

        mostrarInformacion("Éxito", "Vehículo asignado correctamente al incidente.");
    }
    
    /**
     * Marca el incidente seleccionado como atendido
     */
    private void marcarIncidenteAtendido() {
        Incidente incidenteSeleccionado = tablaIncidentes.getSelectionModel().getSelectedItem();

        if (incidenteSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un incidente.");
            return;
        }

        if (incidenteSeleccionado.isAtendido()) {
            mostrarAlerta("Error", "El incidente ya ha sido atendido.");
            return;
        }

        // Buscar el vehículo asignado a este incidente
        Vehiculo vehiculoAsignado = simulador.getVehiculos().stream()
                .filter(v -> v.getIncidenteAsignado() != null &&
                        v.getIncidenteAsignado().equals(incidenteSeleccionado))
                .findFirst()
                .orElse(null);

        if (vehiculoAsignado == null) {
            mostrarAlerta("Error", "No hay ningún vehículo asignado a este incidente.");
            return;
        }

        // CORRECCIÓN: Marcar incidente como atendido
        incidenteSeleccionado.setAtendido(true);

        // CORRECCIÓN: Liberar el vehículo asignado
        vehiculoAsignado.setDisponible(true);
        vehiculoAsignado.setIncidenteAsignado(null); // Necesitas añadir este setter

        // Calcular y agregar puntos
        int puntos = simulador.getGestorPuntuacion().calcularPuntos(incidenteSeleccionado);
        simulador.getGestorPuntuacion().agregarPuntos(puntos);

        // Notificar que el vehículo está disponible nuevamente
        simulador.notificarVehiculoDisponible(vehiculoAsignado);

        actualizarDatos();
        mostrarInformacion("Éxito", "Incidente marcado como atendido y vehículo liberado.");
    }

    /**
     * Muestra una alerta de error
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra una alerta de información
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Implementación de SimuladorSGMMS.Observer
    
    @Override
    public void onIncidenteCreado(Incidente incidente) {
        actualizarDatos();
    }
    
    @Override
    public void onIncidenteAtendido(Incidente incidente) {
        actualizarDatos();
    }
    
    @Override
    public void onVehiculoCreado(Vehiculo vehiculo) {
        actualizarDatos();
    }
    
    @Override
    public void onVehiculoAsignado(Vehiculo vehiculo, Incidente incidente) {
        actualizarDatos();
    }
    
    @Override
    public void onPuntuacionActualizada(int puntuacion) {
        actualizarEstadisticas();
    }
    
    @Override
    public void onEstadisticasActualizadas(Map<String, Object> estadisticas) {
        actualizarEstadisticas();
    }

    @Override
    public void onVehiculoDisponible(Vehiculo vehiculo) {
        actualizarDatos();
    }
}