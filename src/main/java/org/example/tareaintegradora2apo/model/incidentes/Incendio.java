package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;



public class Incendio extends Incidente {
    
    private String tipoIncendio;
    private int intensidad;
    

    public Incendio(String id, Point2D posicion, String tipoIncendio, int intensidad) {

        super(id, posicion, 5, 10000);
        this.tipoIncendio = tipoIncendio;
        this.intensidad = intensidad;
    }
    

    public String getTipoIncendio() {
        return tipoIncendio;
    }
    

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