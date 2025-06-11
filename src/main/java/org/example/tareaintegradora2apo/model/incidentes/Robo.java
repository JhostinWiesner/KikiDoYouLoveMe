package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;


public class Robo extends Incidente {
    
    private String tipoRobo; // Comercial, residencial, vía pública
    

    public Robo(String id, Point2D posicion, String tipoRobo) {
        // Los robos tienen prioridad media-alta (3) y requieren tiempo medio de atención
        super(id, posicion, 3, 5000);
        this.tipoRobo = tipoRobo;
    }
    

    public String getTipoRobo() {
        return tipoRobo;
    }
    
    @Override
    public String getTipo() {
        return "Robo";
    }
    
    @Override
    public String toString() {
        return String.format("%s - Tipo: %s", super.toString(), tipoRobo);
    }
}