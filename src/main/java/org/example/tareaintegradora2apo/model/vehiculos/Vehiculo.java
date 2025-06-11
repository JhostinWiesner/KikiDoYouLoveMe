package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
    protected boolean enMovimiento;
    protected List<Point2D> puntosRuta;
    protected int puntoActual;
    protected double prioridad;

    protected Image image;
    protected GraphicsContext gc;
    protected double ancho = 32;
    protected double alto = 32;

    /**
     * Constructor para la clase Vehiculo
     *
     * @param id              Identificador único del vehículo
     * @param posicionInicial Posición inicial en el mapa
     * @param velocidad       Velocidad base del vehículo
     */
    public Vehiculo(String id, Point2D posicionInicial, double velocidad, Image imagen, GraphicsContext gc) {
        this.id = id;
        this.posicion = posicionInicial;
        this.velocidad = velocidad;
        this.disponible = true;
        this.enMovimiento = false;
        this.puntosRuta = new ArrayList<>();
        this.puntoActual = 0;
        this.image = imagen;
        this.gc = gc;
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
        enMovimiento = false;
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
        this.enMovimiento = true;
    }

    /**
     * Asigna un incidente al vehículo
     *
     * @param incidente Incidente a atender
     * @param grafo     Grafo de navegación para calcular la ruta
     */
    public void asignarIncidente(Incidente incidente, Grafo grafo) {
        this.incidenteAsignado = incidente;
        // Calcular ruta desde posición actual hasta el incidente
        Ruta rutaHaciaIncidente = grafo.calcularRuta(this.posicion, incidente.getPosicion());
        asignarRuta(rutaHaciaIncidente);
    }

    /**
     * Verifica si el vehículo debe detenerse en un semáforo
     *
     * @param semaforo Semáforo a verificar
     * @return true si debe detenerse, false en caso contrario
     */
    public abstract boolean debeDetenerse(Semaforo semaforo);


    public void dibujar() {
        double x = posicion.getX() - ancho / 2;
        double y = posicion.getY() - alto / 2;
        gc.drawImage(image, x, y, ancho, alto);
    }
    /**
     * Método que se ejecuta en el hilo del vehículo
     */
    @Override
    public void run() {
        try {
            while (true) {
                try {
                    if (enMovimiento && puntosRuta.size() > 0) {
                        mover();
                        Platform.runLater(this::dibujar);
                    }
                    Thread.sleep(30); // Ajustar velocidad de movimiento
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
            enMovimiento = false;
            disponible = true;
            return;
        }

        Point2D origen = posicion;
        Point2D destino = puntosRuta.get(puntoActual);

        Point2D direccion = destino.subtract(origen).normalize();

        double distancia = origen.distance(destino);
        double paso = velocidad / 10.0;

        while (posicion.distance(destino) > paso) {
            if (Thread.currentThread().isInterrupted() || !enMovimiento) return;

            posicion = posicion.add(direccion.multiply(paso));

            Platform.runLater(() -> dibujar());
        }
        try {
            Thread.sleep(30); // 500 puede ser calibrado
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
                incidenteAsignado.setAtendido(true);
                incidenteAsignado = null;
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
        return enMovimiento;
    }


}