package org.example.tareaintegradora2apo.model.trafico;

import javafx.geometry.Point2D;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase que representa un semáforo en el sistema SGMMS.
 * Implementa Runnable para permitir que cada semáforo se ejecute en su propio hilo.
 */
public class Semaforo implements Runnable {

    public enum Estado {
        VERDE, AMARILLO, ROJO
    }

    public enum Patron {
        NORMAL,     // Verde(5s) -> Amarillo(2s) -> Rojo(5s)
        RAPIDO,     // Verde(3s) -> Amarillo(1s) -> Rojo(3s)
        LENTO,      // Verde(8s) -> Amarillo(3s) -> Rojo(8s)
        NOCTURNO    // Amarillo intermitente
    }

    private String id;
    private Point2D posicion;
    private AtomicInteger estado; // 0: Verde, 1: Amarillo, 2: Rojo
    private Thread hiloSemaforo;
    private boolean activo;
    private Patron patron;

    // Tiempos para cada estado en milisegundos según el patrón
    private static final int[][] TIEMPOS = {
            {5000, 2000, 5000},  // NORMAL: Verde, Amarillo, Rojo
            {3000, 1000, 3000},  // RAPIDO
            {8000, 3000, 8000},  // LENTO
            {1000, 1000, 0}      // NOCTURNO (solo usa verde y amarillo)
    };

    // Contadores de vehículos
    private int vehiculosRespetaron;
    private int vehiculosViolaron;

    /**
     * Constructor para la clase Semaforo
     *
     * @param id       Identificador único del semáforo
     * @param posicion Posición en el mapa
     */
    public Semaforo(String id, Point2D posicion) {
        this(id, posicion, Patron.NORMAL);
    }

    /**
     * Constructor para la clase Semaforo con patrón específico
     *
     * @param id       Identificador único del semáforo
     * @param posicion Posición en el mapa
     * @param patron   Patrón de cambio de luces
     */
    public Semaforo(String id, Point2D posicion, Patron patron) {
        this.id = id;
        this.posicion = posicion;
        this.estado = new AtomicInteger(0); // Inicia en verde
        this.activo = false;
        this.vehiculosRespetaron = 0;
        this.vehiculosViolaron = 0;
        this.patron = patron;
    }

    /**
     * Inicia el hilo del semáforo para comenzar su ciclo
     */
    public void iniciar() {
        if (hiloSemaforo == null || !hiloSemaforo.isAlive()) {
            activo = true;
            hiloSemaforo = new Thread(this);
            hiloSemaforo.setDaemon(true);
            hiloSemaforo.start();
        }
    }

    /**
     * Detiene el ciclo del semáforo
     */
    public void detener() {
        activo = false;
        if (hiloSemaforo != null) {
            hiloSemaforo.interrupt();
        }
    }

    /**
     * Método que se ejecuta en el hilo del semáforo
     */
    @Override
    public void run() {
        try {
            while (activo && !Thread.currentThread().isInterrupted()) {
                try {
                    // Obtener tiempos según el patrón
                    int[] tiempos = TIEMPOS[patron.ordinal()];

                    if (patron == Patron.NOCTURNO) {
                        // Modo nocturno: solo parpadea amarillo
                        estado.set(1); // Amarillo
                        Thread.sleep(tiempos[0]);
                        estado.set(0); // Apagado (usamos verde como apagado)
                        Thread.sleep(tiempos[1]);
                    } else {
                        // Ciclo normal
                        // Verde
                        estado.set(0);
                        Thread.sleep(tiempos[0]);

                        // Amarillo
                        estado.set(1);
                        Thread.sleep(tiempos[1]);

                        // Rojo
                        estado.set(2);
                        Thread.sleep(tiempos[2]);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error en semáforo " + id + ": " + e.getMessage());
                    try {
                        Thread.sleep(1000); // Pausa antes de continuar
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fatal en semáforo " + id + ": " + e.getMessage());
        } finally {
            activo = false;
            System.out.println("Semáforo " + id + " detenido");
        }
    }

    /**
     * Registra que un vehículo respetó el semáforo
     */
    public void registrarRespeto() {
        vehiculosRespetaron++;
    }

    /**
     * Registra que un vehículo violó el semáforo
     */
    public void registrarViolacion() {
        vehiculosViolaron++;
    }

    /**
     * Cambia el patrón del semáforo
     *
     * @param patron Nuevo patrón
     */
    public void cambiarPatron(Patron patron) {
        this.patron = patron;
    }

    /**
     * Verifica si el semáforo está en rojo
     *
     * @return true si está en rojo, false en caso contrario
     */
    public boolean isRojo() {
        return estado.get() == 2;
    }

    /**
     * Verifica si el semáforo está en amarillo
     *
     * @return true si está en amarillo, false en caso contrario
     */
    public boolean isAmarillo() {
        return estado.get() == 1;
    }

    /**
     * Verifica si el semáforo está en verde
     *
     * @return true si está en verde, false en caso contrario
     */
    public boolean isVerde() {
        return estado.get() == 0;
    }

    /**
     * Obtiene el estado actual del semáforo
     *
     * @return Estado actual
     */
    public Estado getEstado() {
        switch (estado.get()) {
            case 0:
                return Estado.VERDE;
            case 1:
                return Estado.AMARILLO;
            case 2:
                return Estado.ROJO;
            default:
                return Estado.VERDE;
        }
    }

    /**
     * Obtiene el patrón actual del semáforo
     *
     * @return Patrón actual
     */
    public Patron getPatron() {
        return patron;
    }

    // Getters

    public String getId() {
        return id;
    }

    public Point2D getPosicion() {
        return posicion;
    }

    public void setPosicion(Point2D posicion) {
        this.posicion = posicion;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getVehiculosRespetaron() {
        return vehiculosRespetaron;
    }

    public int getVehiculosViolaron() {
        return vehiculosViolaron;
    }

    /**
     * Obtiene el porcentaje de respeto del semáforo
     *
     * @return Porcentaje de respeto (0-100)
     */
    public double getPorcentajeRespeto() {
        int total = vehiculosRespetaron + vehiculosViolaron;
        if (total == 0) {
            return 100.0;
        }
        return (double) vehiculosRespetaron / total * 100.0;
    }

    @Override
    public String toString() {
        return String.format("Semáforo %s - Estado: %s - Patrón: %s - Respeto: %d - Violaciones: %d",
                id, getEstado(), patron, vehiculosRespetaron, vehiculosViolaron);
    }
}