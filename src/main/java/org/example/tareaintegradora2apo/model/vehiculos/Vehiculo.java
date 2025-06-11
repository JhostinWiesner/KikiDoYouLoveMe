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


    public void iniciar() {
        if (hiloVehiculo == null || !hiloVehiculo.isAlive()) {
            hiloVehiculo = new Thread(this);
            hiloVehiculo.setDaemon(true);
            hiloVehiculo.start();
        }
    }


    public void detener() {
        enMovimiento.set(false);
        if (hiloVehiculo != null) {
            hiloVehiculo.interrupt();
        }
    }


    public void asignarRuta(Ruta ruta) {
        this.rutaActual = ruta;
        this.puntosRuta = ruta.getPuntos();
        this.puntoActual = 0;
        this.disponible = false;
        this.enMovimiento.set(true);
    }



    public void asignarIncidente(Incidente incidente, Grafo grafo) {
        if (!this.disponible){
            return;
        }

        this.incidenteAsignado = incidente;

        Ruta rutaHaciaIncidente = grafo.calcularRuta(this.posicion, incidente.getPosicion());
        asignarRuta(rutaHaciaIncidente);


    }


    public abstract boolean debeDetenerse(Semaforo semaforo);


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

                }
            }
        } catch (Exception e) {
            System.err.println("Error fatal en hilo de vehículo " + id + ": " + e.getMessage());
        }
    }


    protected void mover() {
        if (puntoActual >= puntosRuta.size()) {
            // Llegó al destino
            if (incidenteAsignado != null) {
                atenderIncidente();
            }
            return;
        }


        posicion = puntosRuta.get(puntoActual);
        puntoActual++;


        try {
            Thread.sleep((long) (500 / velocidad));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    protected void atenderIncidente() {
        if (incidenteAsignado != null) {
            // Simular tiempo de atención
            try {
                Thread.sleep(incidenteAsignado.getTiempoAtencion());

                // CORRECCIÓN: Calcular puntos ANTES de poner incidenteAsignado a null
                int puntos = simulador.getGestorPuntuacion().calcularPuntos(incidenteAsignado);
                simulador.getGestorPuntuacion().agregarPuntos(puntos);

                // Marcar el incidente como atendido
                incidenteAsignado.setAtendido(true);

                // Liberar el vehículo DESPUÉS de calcular puntos
                incidenteAsignado = null;
                disponible = true;
                enMovimiento.set(false); // AÑADIR: Detener movimiento

                // Notificar que el vehículo está disponible
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
