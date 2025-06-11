package org.example.tareaintegradora2apo.model.trafico;

import javafx.geometry.Point2D;

import java.util.concurrent.atomic.AtomicInteger;



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


    public Semaforo(String id, Point2D posicion) {
        this(id, posicion, Patron.NORMAL);
    }

    public Semaforo(String id, Point2D posicion, Patron patron) {
        this.id = id;
        this.posicion = posicion;
        this.estado = new AtomicInteger(0); // Inicia en verde
        this.activo = false;
        this.vehiculosRespetaron = 0;
        this.vehiculosViolaron = 0;
        this.patron = patron;
    }


    public void iniciar() {
        if (hiloSemaforo == null || !hiloSemaforo.isAlive()) {
            activo = true;
            hiloSemaforo = new Thread(this);
            hiloSemaforo.setDaemon(true);
            hiloSemaforo.start();
        }
    }


    public void detener() {
        activo = false;
        if (hiloSemaforo != null) {
            hiloSemaforo.interrupt();
        }
    }

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
                        Thread.sleep(1000); // Pausaditooo
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

        }
    }


    public void registrarRespeto() {
        vehiculosRespetaron++;
    }


    public void registrarViolacion() {
        vehiculosViolaron++;
    }


    public void cambiarPatron(Patron patron) {
        this.patron = patron;
    }


    public boolean isRojo() {
        return estado.get() == 2;
    }


    public boolean isAmarillo() {
        return estado.get() == 1;
    }


    public boolean isVerde() {
        return estado.get() == 0;
    }


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