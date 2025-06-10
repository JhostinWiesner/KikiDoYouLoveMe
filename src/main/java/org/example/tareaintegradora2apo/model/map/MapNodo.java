package org.example.tareaintegradora2apo.model.map;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase interna que representa un nodo del grafo
 */
public class MapNodo {
    private String id;
    private Point2D posicion;
    private List<MapArista> aristasAdyacentes;
    private boolean tieneSemaforo;

    public MapNodo(String id, Point2D posicion) {
        this.id = id;
        this.posicion = posicion;
        this.aristasAdyacentes = new ArrayList<>();
        this.tieneSemaforo = false;
    }

    public String getId() {
        return id;
    }

    public Point2D getPosicion() {
        return posicion;
    }

    public List<MapArista> getAristasAdyacentes() {
        return aristasAdyacentes;
    }

    public void agregarAristaAdyacente(MapArista arista) {
        aristasAdyacentes.add(arista);
    }

    public boolean tieneSemaforo() {
        return tieneSemaforo;
    }

    public void setTieneSemaforo(boolean tieneSemaforo) {
        this.tieneSemaforo = tieneSemaforo;
    }
}