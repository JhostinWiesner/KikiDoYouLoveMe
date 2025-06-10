package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;

/**
 * Clase que representa un robo en el sistema SGMMS.
 */
public class Robo extends Incidente {
    
    private String tipoRobo; // Comercial, residencial, vía pública
    
    /**
     * Constructor para la clase Robo
     * @param id Identificador único del robo
     * @param posicion Posición en el mapa donde ocurrió el robo
     * @param tipoRobo Tipo de robo (comercial, residencial, vía pública)
     */
    public Robo(String id, Point2D posicion, String tipoRobo) {
        // Los robos tienen prioridad media-alta (3) y requieren tiempo medio de atención
        super(id, posicion, 3, 5000);
        this.tipoRobo = tipoRobo;
    }
    
    /**
     * Obtiene el tipo de robo
     * @return Tipo de robo
     */
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