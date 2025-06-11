package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;

import java.time.LocalDateTime;


public abstract class Incidente {
    protected String id;
    protected Point2D posicion;
    protected LocalDateTime horaInicio;
    protected LocalDateTime horaAtencion;
    protected boolean atendido;
    protected int prioridad;
    protected int tiempoAtencion; // en milisegundos
    

    public Incidente(String id, Point2D posicion, int prioridad, int tiempoAtencion) {
        this.id = id;
        this.posicion = posicion;
        this.horaInicio = LocalDateTime.now();
        this.atendido = false;
        this.prioridad = prioridad;
        this.tiempoAtencion = tiempoAtencion;
    }
    

    public abstract String getTipo();
    

    public long getTiempoTranscurrido() {
        LocalDateTime ahora = LocalDateTime.now();
        return java.time.Duration.between(horaInicio, ahora).getSeconds();
    }
    

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