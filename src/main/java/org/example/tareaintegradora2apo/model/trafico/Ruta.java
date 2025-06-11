package org.example.tareaintegradora2apo.model.trafico;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.model.map.Grafo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase que representa una ruta en el sistema SGMMS.
 */
public class Ruta {

    private String id;
    private List<Point2D> puntos;
    private LocalDateTime tiempoInicio;
    private LocalDateTime tiempoFin;
    private boolean completada;
    private Grafo grafo; // Referencia al grafo que generó esta ruta

    /**
     * Constructor para la clase Ruta con referencia al grafo
     * @param id Identificador único de la ruta
     * @param puntos Lista de puntos que conforman la ruta
     * @param grafo Grafo que generó esta ruta
     */
    public Ruta(String id, List<Point2D> puntos, Grafo grafo) {
        this.id = id;
        this.puntos = puntos;
        this.tiempoInicio = LocalDateTime.now();
        this.completada = false;
        this.grafo = grafo;
    }

    /**
     * Marca la ruta como completada
     */
    public void completar() {
        this.completada = true;
        this.tiempoFin = LocalDateTime.now();
    }

    /**
     * Calcula la distancia total de la ruta
     * @return Distancia en píxeles
     */
    public double calcularDistanciaTotal() {
        double distancia = 0;

        for (int i = 0; i < puntos.size() - 1; i++) {
            distancia += puntos.get(i).distance(puntos.get(i + 1));
        }

        return distancia;
    }

    /**
     * Calcula el tiempo estimado para completar la ruta
     * @param velocidad Velocidad del vehículo
     * @return Tiempo estimado en segundos
     */
    public double calcularTiempoEstimado(double velocidad) {
        return calcularDistanciaTotal() / velocidad;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public List<Point2D> getPuntos() {
        return puntos;
    }

    public LocalDateTime getTiempoInicio() {
        return tiempoInicio;
    }

    public LocalDateTime getTiempoFin() {
        return tiempoFin;
    }

    public boolean isCompletada() {
        return completada;
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public void setGrafo(Grafo grafo) {
        this.grafo = grafo;
    }
}