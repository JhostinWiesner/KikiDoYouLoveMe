package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.map.Grafo;
import org.example.tareaintegradora2apo.model.trafico.Ruta;
import org.example.tareaintegradora2apo.model.incidentes.Incidente;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase abstracta que representa un vehículo en el sistema SGMMS.
 * Implementa Runnable para permitir que cada vehículo se ejecute en su propio hilo.
 */
public abstract class Vehiculo implements Runnable {
    protected String id;
    protected Point2D posicion;
    protected double velocidad;
    protected boolean disponible;
    protected Ruta rutaActual;
    protected Incidente incidenteAsignado;
    protected Thread hiloVehiculo;
    protected AtomicBoolean enMovimiento;
    protected List<Point2D> puntosRuta;
    protected int puntoActual;
    protected double prioridad;
    protected SimuladorSGMMS simulador;

    /**
     * Constructor para la clase Vehiculo
     *
     * @param id              Identificador único del vehículo
     * @param posicionInicial Posición inicial en el mapa
     * @param velocidad       Velocidad base del vehículo
     * @param simulador       Instancia del simulador para acceder a la puntuación
     */
    public Vehiculo(String id, Point2D posicionInicial, double velocidad, SimuladorSGMMS simulador) {
        this.id = id;
        this.posicion = posicionInicial;
        this.velocidad = velocidad;
        this.disponible = true;
        this.enMovimiento = new AtomicBoolean(false);
        this.puntosRuta = new ArrayList<>();
        this.puntoActual = 0;
        this.simulador = simulador;
    }

    /**
     * Inicia el hilo del vehículo para comenzar su movimiento autónomo
     */
    public void iniciar() {
        if (hiloVehiculo == null || !hiloVehiculo.isAlive()) {
            hiloVehiculo = new Thread(this);
            hiloVehiculo.setDaemon(true);
            hiloVehiculo.start();
        }
    }

    /**
     * Detiene el movimiento del vehículo
     */
    public void detener() {
        enMovimiento.set(false);
        if (hiloVehiculo != null) {
            hiloVehiculo.interrupt();
        }
    }

    /**
     * Asigna una ruta al vehículo
     *
     * @param ruta Ruta a seguir
     */
    public void asignarRuta(Ruta ruta) {
        this.rutaActual = ruta;
        this.puntosRuta = ruta.getPuntos();
        this.puntoActual = 0;
        this.disponible = false;
        this.enMovimiento.set(true);
    }

    /**
     * Asigna un incidente al vehículo
     *
     * @param incidente Incidente a atender
     * @param grafo     Grafo de navegación para calcular la ruta
     */
    public void asignarIncidente(Incidente incidente, Grafo grafo) {
        if (!this.disponible){
            return;
        }

        this.incidenteAsignado = incidente;
        // Calcular ruta desde posición actual hasta el incidente
        Ruta rutaHaciaIncidente = grafo.calcularRuta(this.posicion, incidente.getPosicion());
        asignarRuta(rutaHaciaIncidente);

        // Puntaje no se debe manejar aquí, solo se asigna el incidente y se calcula la ruta
    }

    /**
     * Verifica si el vehículo debe detenerse en un semáforo
     *
     * @param semaforo Semáforo a verificar
     * @return true si debe detenerse, false en caso contrario
     */
    public abstract boolean debeDetenerse(Semaforo semaforo);

    /**
     * Método que se ejecuta en el hilo del vehículo
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (enMovimiento.get() && puntosRuta.size() > 0) {
                        mover();
                    }
                    Thread.sleep((long) (100 / velocidad)); // Ajustar velocidad de movimiento
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error en hilo de vehículo " + id + ": " + e.getMessage());
                    // Continuar con la ejecución
                }
            }
        } catch (Exception e) {
            System.err.println("Error fatal en hilo de vehículo " + id + ": " + e.getMessage());
        }
    }

    /**
     * Mueve el vehículo hacia el siguiente punto de la ruta
     */
    protected void mover() {
        if (puntoActual >= puntosRuta.size()) {
            // Llegó al destino
            if (incidenteAsignado != null) {
                atenderIncidente();
            }
            return;
        }

        // Mover directamente al siguiente nodo (sin interpolación)
        posicion = puntosRuta.get(puntoActual);
        puntoActual++;

        // Simular el tiempo que tardaría en llegar al siguiente nodo
        try {
            Thread.sleep((long) (500 / velocidad)); // 500 puede ser calibrado
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Atiende el incidente asignado
     */
    protected void atenderIncidente() {
        if (incidenteAsignado != null) {
            // Simular tiempo de atención
            try {
                Thread.sleep(incidenteAsignado.getTiempoAtencion());

                int puntos = simulador.getGestorPuntuacion().calcularPuntos(incidenteAsignado);
                simulador.getGestorPuntuacion().agregarPuntos(puntos);

                incidenteAsignado.setAtendido(true);  // Marca el incidente como atendido
                // Liberamos el vehículo después de atender el incidente
                incidenteAsignado = null;
                disponible = true;

                // Notificamos que el vehículo está disponible ahora
                simulador.notificarVehiculoDisponible(this);


            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public Point2D getPosicion() {
        return posicion;
    }

    public void setPosicion(Point2D posicion) {
        this.posicion = posicion;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Incidente getIncidenteAsignado() {
        return incidenteAsignado;
    }

    public double getPrioridad() {
        return prioridad;
    }

    public boolean isEnMovimiento() {
        return enMovimiento.get();
    }

    public void setIncidenteAsignado(Incidente incidenteAsignado) {
        this.incidenteAsignado = incidenteAsignado;
    }
}
