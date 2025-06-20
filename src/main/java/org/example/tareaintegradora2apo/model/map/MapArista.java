package org.example.tareaintegradora2apo.model.map;

/**
 * Clase interna que representa una arista del grafo
 */
public class MapArista {
    private String id;
    private MapNodo origen;
    private MapNodo destino;
    private double peso; // Distancia o tiempo


    public MapArista(String id, MapNodo origen, MapNodo destino, double peso) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public String getId() {
        return id;
    }

    public MapNodo getOrigen() {
        return origen;
    }

    public MapNodo getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

}