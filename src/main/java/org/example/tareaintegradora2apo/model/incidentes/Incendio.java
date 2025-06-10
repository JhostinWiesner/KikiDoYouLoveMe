package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;


/**
 * Clase que representa un incendio en el sistema SGMMS.
 */
public class Incendio extends Incidente {
    
    private String tipoIncendio; // Estructural, vehicular, forestal
    private int intensidad; // 1-5, siendo 5 la más alta
    
    /**
     * Constructor para la clase Incendio
     * @param id Identificador único del incendio
     * @param posicion Posición en el mapa donde ocurrió el incendio
     * @param tipoIncendio Tipo de incendio (estructural, vehicular, forestal)
     * @param intensidad Intensidad del incendio (1-5)
     */
    public Incendio(String id, Point2D posicion, String tipoIncendio, int intensidad) {
        // Los incendios tienen prioridad máxima (5) y requieren más tiempo de atención
        super(id, posicion, 5, 10000);
        this.tipoIncendio = tipoIncendio;
        this.intensidad = intensidad;
    }
    
    /**
     * Obtiene el tipo de incendio
     * @return Tipo de incendio
     */
    public String getTipoIncendio() {
        return tipoIncendio;
    }
    
    /**
     * Obtiene la intensidad del incendio
     * @return Intensidad del incendio
     */
    public int getIntensidad() {
        return intensidad;
    }
    
    @Override
    public String getTipo() {
        return "Incendio";
    }
    
    @Override
    public String toString() {
        return String.format("%s - Tipo: %s - Intensidad: %d", 
                super.toString(), tipoIncendio, intensidad);
    }
}