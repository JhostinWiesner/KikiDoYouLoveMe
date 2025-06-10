package org.example.tareaintegradora2apo.model.map;

/**
 * Clase interna que representa una arista del grafo
 */
public class MapArista {
    private String id;
    private MapNodo origen;
    private MapNodo destino;
    private double peso; // Distancia o tiempo
    private String tipoVia; // Principal, secundaria, residencial
    private double velocidadMaxima;

    public MapArista(String id, MapNodo origen, MapNodo destino, double peso, String tipoVia, double velocidadMaxima) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.tipoVia = tipoVia;
        this.velocidadMaxima = velocidadMaxima;
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

    public String getTipoVia() {
        return tipoVia;
    }

    public double getVelocidadMaxima() {
        return velocidadMaxima;
    }
}