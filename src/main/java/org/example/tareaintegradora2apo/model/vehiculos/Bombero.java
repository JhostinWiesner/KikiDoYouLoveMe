package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;


public class Bombero extends Vehiculo {
    
    private boolean enEmergencia;
    

    public Bombero(String id, Point2D posicionInicial, SimuladorSGMMS simulador) {
        super(id, posicionInicial, 2.5,simulador); // Velocidad media-alta
        this.prioridad = 2.5; // Prioridad media-alta
        this.enEmergencia = false;
    }
    

    @Override
    public boolean debeDetenerse(Semaforo semaforo) {

        return !enEmergencia && semaforo.isRojo();
    }
    

    public void activarEmergencia() {
        this.enEmergencia = true;
        this.velocidad = 3.5; // Aumenta la velocidad en emergencia
    }
    

    public void desactivarEmergencia() {
        this.enEmergencia = false;
        this.velocidad = 2.5; // Vuelve a la velocidad normal
    }
    

    public int atenderIncendio() {

        return 10000;
    }
    

    public boolean isEnEmergencia() {
        return enEmergencia;
    }
}