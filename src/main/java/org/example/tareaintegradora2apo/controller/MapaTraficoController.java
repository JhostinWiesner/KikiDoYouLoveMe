package org.example.tareaintegradora2apo.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.tareaintegradora2apo.model.incidentes.*;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;
import org.example.tareaintegradora2apo.model.vehiculos.*;


import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controlador para la vista del Mapa de Tráfico.
 * Maneja la visualización en tiempo real del mapa, vehículos, semáforos e incidentes.
 */
public class MapaTraficoController implements Initializable, SimuladorSGMMS.Observer {

    @FXML
    private Canvas canvasMapa;
    @FXML
    private Label lblInstrucciones;
    @FXML
    private Label lblCoordenadas;
    @FXML
    private Button btnCentroMonitoreo;
    @FXML
    private Button btnPanelIncidentes;
    @FXML
    private Button btnManual;
    @FXML
    private Button btnSalir;

    private SimuladorSGMMS simulador;
    private Stage primaryStage;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;

    // Variables para navegación de cámara
    private double camaraX = 412.5;
    private double camaraY = 412.5;
    private final double VELOCIDAD_CAMARA = 7.0;

    // Teclas presionadas
    private boolean teclaW = false;
    private boolean teclaA = false;
    private boolean teclaS = false;
    private boolean teclaD = false;

    /**
     * Establece el simulador y la ventana principal
     */
    public void setSimulador(SimuladorSGMMS simulador, Stage primaryStage) {
        this.simulador = simulador;
        this.primaryStage = primaryStage;
        simulador.agregarObservador(this);
        iniciarRenderizado();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvasMapa.getGraphicsContext2D();


        // Configurar canvas para recibir eventos de teclado
        canvasMapa.setFocusTraversable(true);

        // Solicitar foco cuando se hace clic en el canvas
        canvasMapa.setOnMouseClicked(event -> {
            canvasMapa.requestFocus();
        });

        // También solicitar foco al inicializar
        Platform.runLater(() -> canvasMapa.requestFocus());

        canvasMapa.setOnKeyPressed(this::manejarTeclaPresionada);
        canvasMapa.setOnKeyReleased(this::manejarTeclaLiberada);

        // Configurar eventos de mouse para mostrar coordenadas
        canvasMapa.setOnMouseMoved(event -> {
            double x = event.getX() + camaraX;
            double y = event.getY() + camaraY;
            lblCoordenadas.setText(String.format("X: %.0f, Y: %.0f", x, y));
        });

        // Configurar botones de navegación
        btnCentroMonitoreo.setOnAction(e -> irACentroMonitoreo());
        btnPanelIncidentes.setOnAction(e -> irAPanelIncidentes());
        btnManual.setOnAction(e -> abrirManual());
        btnSalir.setOnAction(e -> salirAplicacion());

        // Configurar instrucciones
        lblInstrucciones.setText("WASD: Mover cámara");
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

            // Detener renderizado
            detenerRenderizado();

        } catch (IOException e) {
            System.err.println("Error al cargar centro de monitoreo: " + e.getMessage());
        }
    }

    /**
     * Va al panel de incidentes
     */
    private void irAPanelIncidentes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tareaintegradora2apo/panelIncidentes.fxml"));
            Parent root = loader.load();

            PanelIncidentesController controller = loader.getController();
            controller.setSimulador(simulador, primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SGMMS - Panel de Incidentes");

            // Detener renderizado
            detenerRenderizado();

        } catch (IOException e) {
            System.err.println("Error al cargar panel de incidentes: " + e.getMessage());
        }
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
     * Sale de la aplicación
     */
    private void salirAplicacion() {
        if (simulador != null && simulador.isSimulacionActiva()) {
            simulador.detenerSimulacion();
        }
        System.exit(0);
    }

    /**
     * Maneja las teclas presionadas para navegación de cámara
     */
    private void manejarTeclaPresionada(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                teclaW = true;
                break;
            case A:
                teclaA = true;
                break;
            case S:
                teclaS = true;
                break;
            case D:
                teclaD = true;
                break;
        }
    }

    /**
     * Maneja las teclas liberadas
     */
    private void manejarTeclaLiberada(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                teclaW = false;
                break;
            case A:
                teclaA = false;
                break;
            case S:
                teclaS = false;
                break;
            case D:
                teclaD = false;
                break;
        }
    }

    /**
     * Actualiza la posición de la cámara basada en las teclas presionadas
     */
    private void actualizarCamara() {
        if (teclaW) camaraY -= VELOCIDAD_CAMARA;
        if (teclaS) camaraY += VELOCIDAD_CAMARA;
        if (teclaA) camaraX -= VELOCIDAD_CAMARA;
        if (teclaD) camaraX += VELOCIDAD_CAMARA;


        double maxX = 1000 * 1.65 - canvasMapa.getWidth(); // Ajustar según la escala
        double maxY = 1000 * 1.65 - canvasMapa.getHeight(); // Ajustar según la escala

        camaraX = Math.max(0, Math.min(camaraX, maxX));
        camaraY = Math.max(0, Math.min(camaraY, maxY));

    }

    /**
     * Inicia el bucle de renderizado
     */
    private void iniciarRenderizado() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    actualizarCamara();
                    renderizar();

                    // Procesar incidentes atendidos con menos frecuencia
                    if (now % 1_000_000_000 < 16_666_667) { // Aproximadamente cada segundo
                        if (simulador != null) {
                            simulador.procesarIncidentesAtendidos();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error en bucle de renderizado: " + e.getMessage());
                    // No detener el bucle por errores menores
                }
            }
        };
        animationTimer.start();
    }


    /**
     * Renderiza el fondo del mapa
     */
    private void renderizarFondoMapa() {
        // Renderizar imagen de fondo si está disponible
        if (simulador.getMapa().getImagenMapa() != null) {
            gc.drawImage(simulador.getMapa().getImagenMapa(), 0, 0, 1000, 1000);
        } else {
            // Renderizar fondo básico
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, 1000, 1000);

            // Renderizar grid
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(0.5);

            for (int i = 0; i <= 1000; i += 50) {
                gc.strokeLine(i, 0, i, 1000);
                gc.strokeLine(0, i, 1000, i);
            }

            // Mensaje indicando que no hay imagen
            gc.setFill(Color.BLACK);
            gc.fillText("Mapa", 400, 500);
        }
    }

    /**
     * Renderiza los semáforos
     */
    private void renderizarSemaforos() {
        for (Semaforo semaforo : simulador.getMapa().getSemaforos()) {
            double x = semaforo.getPosicion().getX();
            double y = semaforo.getPosicion().getY();

            // Fondo del semáforo
            gc.setFill(Color.BLACK);
            gc.fillRect(x - 8, y - 12, 16, 24);

            // Luces del semáforo
            Color colorLuz;
            switch (semaforo.getEstado()) {
                case VERDE:
                    colorLuz = Color.GREEN;
                    break;
                case AMARILLO:
                    colorLuz = Color.YELLOW;
                    break;
                case ROJO:
                    colorLuz = Color.RED;
                    break;
                default:
                    colorLuz = Color.GRAY;
                    break;
            }

            gc.setFill(colorLuz);
            gc.fillOval(x - 6, y - 10, 12, 12);

            // Borde
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.0);
            gc.strokeOval(x - 6, y - 10, 12, 12);
        }
    }

    /**
     * Renderiza los vehículos
     */
    private void renderizarVehiculos() {
        for (Vehiculo vehiculo : simulador.getVehiculos()) {
            double x = vehiculo.getPosicion().getX();
            double y = vehiculo.getPosicion().getY();

            // Color según tipo de vehículo
            Color color;
            String simbolo;

            if (vehiculo instanceof Patrulla) {
                color = Color.BLUE;
                simbolo = "P";
            } else if (vehiculo instanceof Ambulancia) {
                color = Color.WHITE;
                simbolo = "A";
            } else if (vehiculo instanceof Bombero) {
                color = Color.RED;
                simbolo = "B";
            } else {
                color = Color.GRAY;
                simbolo = "V";
            }

            // Cuerpo del vehículo
            gc.setFill(color);
            gc.fillOval(x - 8, y - 8, 16, 16);

            // Borde
            gc.setStroke(vehiculo.isDisponible() ? Color.GREEN : Color.ORANGE);
            gc.setLineWidth(2.0);
            gc.strokeOval(x - 8, y - 8, 16, 16);

            // Símbolo del vehículo
            gc.setFill(Color.BLACK);
            gc.fillText(simbolo, x - 4, y + 4);

            // Indicador de movimiento
            if (vehiculo.isEnMovimiento()) {
                gc.setFill(Color.YELLOW);
                gc.fillOval(x - 2, y - 12, 4, 4);
            }
        }
    }

    /**
     * Renderiza los incidentes
     */
    private void renderizarIncidentes() {
        for (Incidente incidente : simulador.getIncidentesActivos()) {
            double x = incidente.getPosicion().getX();
            double y = incidente.getPosicion().getY();

            // Color y símbolo según tipo de incidente
            Color color;
            String simbolo;

            if (incidente instanceof Accidente) {
                color = Color.ORANGE;
                simbolo = "!";
            } else if (incidente instanceof Robo) {
                color = Color.PURPLE;
                simbolo = "$";
            } else if (incidente instanceof Incendio) {
                color = Color.RED;
                simbolo = "F";
            } else {
                color = Color.GRAY;
                simbolo = "?";
            }

            // Fondo del incidente
            gc.setFill(color);
            gc.fillOval(x - 12, y - 12, 24, 24);

            // Borde parpadeante para incidentes no atendidos
            if (!incidente.isAtendido()) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(3.0);
                gc.strokeOval(x - 12, y - 12, 24, 24);
            }

            // Símbolo del incidente
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText(simbolo, x - 6, y + 6);

            // Indicador de prioridad
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(incidente.getPrioridad()), x + 8, y - 8);
        }
    }

    /**
     * Detiene el renderizado cuando se cierra la ventana
     */
    public void detenerRenderizado() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    // Implementación de SimuladorSGMMS.Observer

    @Override
    public void onIncidenteCreado(Incidente incidente) {
        // El renderizado se actualiza automáticamente en el bucle
    }

    @Override
    public void onIncidenteAtendido(Incidente incidente) {
        // El renderizado se actualiza automáticamente en el bucle
    }

    @Override
    public void onVehiculoCreado(Vehiculo vehiculo) {
        // El renderizado se actualiza automáticamente en el bucle
    }

    @Override
    public void onVehiculoAsignado(Vehiculo vehiculo, Incidente incidente) {
        // El renderizado se actualiza automáticamente en el bucle
    }

    @Override
    public void onPuntuacionActualizada(int puntuacion) {
        // No se necesita acción específica en esta vista
    }

    @Override
    public void onEstadisticasActualizadas(Map<String, Object> estadisticas) {
        // No se necesita acción específica en esta vista
    }

    // Agregar método para renderizar vehículos civiles
    private void renderizarVehiculosCiviles() {
        if (simulador == null) return;

        for (VehiculoCivil vehiculo : simulador.getVehiculosCiviles()) {
            double x = vehiculo.getPosicion().getX();
            double y = vehiculo.getPosicion().getY();

            // Color según tipo de vehículo
            Color color;
            String simbolo;

            switch (vehiculo.getTipo()) {
                case "Sedan":
                    color = Color.LIGHTBLUE;
                    simbolo = "S";
                    break;
                case "SUV":
                    color = Color.LIGHTGREEN;
                    simbolo = "U";
                    break;
                case "Camión":
                    color = Color.BROWN;
                    simbolo = "C";
                    break;
                case "Motocicleta":
                    color = Color.YELLOW;
                    simbolo = "M";
                    break;
                default:
                    color = Color.GRAY;
                    simbolo = "V";
                    break;
            }

            // Cuerpo del vehículo (más pequeño que los vehículos de servicio)
            gc.setFill(color);
            gc.fillOval(x - 6, y - 6, 12, 12);

            // Borde
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0);
            gc.strokeOval(x - 6, y - 6, 12, 12);

            // Símbolo del vehículo
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(10));
            gc.fillText(simbolo, x - 3, y + 3);

            // Indicador de movimiento
            if (vehiculo.isEnMovimiento()) {
                gc.setFill(Color.RED);
                gc.fillOval(x - 2, y - 10, 4, 4);
            }
        }
    }


    //     * Renderiza todos los elementos del mapa
    //     */
    private void renderizar() {
        if (simulador == null) {
            return;
        }
        // Limpiar canvas
        gc.clearRect(0, 0, canvasMapa.getWidth(), canvasMapa.getHeight());

        // Aplicar transformación de cámara
        gc.save();

        double escala = 1.65; // Puedes ajustar este valor para hacer el mapa más grande (0.5 es un 50% del tamaño original)
        gc.scale(escala, escala); // Aplica el zoom
        gc.translate(-camaraX / escala, -camaraY / escala);

        // Renderizar fondo del mapa
        renderizarFondoMapa();

        // NO renderizar grafo de calles (eliminado)
        // renderizarGrafoCalles(); - ELIMINADO

        // Renderizar semáforos
        renderizarSemaforos();

        // Renderizar vehículos
        renderizarVehiculos();

        // Renderizar incidentes
        renderizarIncidentes();

        // Restaurar transformación
        gc.restore();
    }


}