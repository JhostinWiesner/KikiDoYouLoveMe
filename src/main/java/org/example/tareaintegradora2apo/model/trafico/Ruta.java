package org.example.tareaintegradora2apo.model.trafico;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.model.map.Grafo;

import java.time.LocalDateTime;
import java.util.List;


public class Ruta {

    private String id;
    private List<Point2D> puntos;
    private LocalDateTime tiempoInicio;
    private LocalDateTime tiempoFin;
    private boolean completada;
    private Grafo grafo;


    public Ruta(String id, List<Point2D> puntos, Grafo grafo) {
        this.id = id;
        this.puntos = puntos;
        this.tiempoInicio = LocalDateTime.now();
        this.completada = false;
        this.grafo = grafo;
    }


    public void completar() {
        this.completada = true;
        this.tiempoFin = LocalDateTime.now();
    }


    public double calcularDistanciaTotal() {
        double distancia = 0;

        for (int i = 0; i < puntos.size() - 1; i++) {
            distancia += puntos.get(i).distance(puntos.get(i + 1));
        }

        return distancia;
    }



    public double calcularTiempoEstimado(double velocidad) {
        return calcularDistanciaTotal() / velocidad;
    }

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