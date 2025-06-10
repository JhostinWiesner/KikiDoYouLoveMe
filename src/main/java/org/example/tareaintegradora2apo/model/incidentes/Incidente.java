package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;

import java.time.LocalDateTime;

/**
 * Clase abstracta que representa un incidente en el sistema SGMMS.
 */
public abstract class Incidente {
    protected String id;
    protected Point2D posicion;
    protected LocalDateTime horaInicio;
    protected LocalDateTime horaAtencion;
    protected boolean atendido;
    protected int prioridad; // 1-5, siendo 5 la más alta
    protected int tiempoAtencion; // en milisegundos
    
    /**
     * Constructor para la clase Incidente
     * @param id Identificador único del incidente
     * @param posicion Posición en el mapa donde ocurrió el incidente
     * @param prioridad Nivel de prioridad del incidente
     * @param tiempoAtencion Tiempo estimado para atender el incidente
     */
    public Incidente(String id, Point2D posicion, int prioridad, int tiempoAtencion) {
        this.id = id;
        this.posicion = posicion;
        this.horaInicio = LocalDateTime.now();
        this.atendido = false;
        this.prioridad = prioridad;
        this.tiempoAtencion = tiempoAtencion;
    }
    
    /**
     * Obtiene el tipo de incidente
     * @return Tipo de incidente como cadena
     */
    public abstract String getTipo();
    
    /**
     * Calcula el tiempo transcurrido desde que se reportó el incidente
     * @return Tiempo en segundos
     */
    public long getTiempoTranscurrido() {
        LocalDateTime ahora = LocalDateTime.now();
        return java.time.Duration.between(horaInicio, ahora).getSeconds();
    }
    
    /**
     * Calcula el tiempo de respuesta desde que se reportó hasta que se atendió
     * @return Tiempo en segundos, o -1 si aún no se ha atendido
     */
    public long getTiempoRespuesta() {
        if (horaAtencion == null) {
            return -1;
        }
        return java.time.Duration.between(horaInicio, horaAtencion).getSeconds();
    }
    
    // Getters y setters
    
    public String getId() {
        return id;
    }
    
    public Point2D getPosicion() {
        return posicion;
    }
    
    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }
    
    public boolean isAtendido() {
        return atendido;
    }
    
    public void setAtendido(boolean atendido) {
        this.atendido = atendido;
        if (atendido) {
            this.horaAtencion = LocalDateTime.now();
        }
    }
    
    public int getPrioridad() {
        return prioridad;
    }
    
    public int getTiempoAtencion() {
        return tiempoAtencion;
    }
    
    public LocalDateTime getHoraAtencion() {
        return horaAtencion;
    }
    
    @Override
    public String toString() {
        return String.format("%s (ID: %s) - Prioridad: %d - Atendido: %s", 
                getTipo(), id, prioridad, atendido ? "Sí" : "No");
    }
}