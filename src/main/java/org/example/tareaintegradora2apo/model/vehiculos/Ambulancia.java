package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;


public class Ambulancia extends Vehiculo {
    
    private boolean enEmergencia;
    

    public Ambulancia(String id, Point2D posicionInicial, SimuladorSGMMS simulador) {
        super(id, posicionInicial, 3.0,simulador); // Velocidad alta
        this.prioridad = 3.0; // Prioridad alta
        this.enEmergencia = false;
    }
    

    @Override
    public boolean debeDetenerse(Semaforo semaforo) {
        // Si está en emergencia, no se detiene en semáforos
        return !enEmergencia && semaforo.isRojo();
    }
    

    public void activarEmergencia() {
        this.enEmergencia = true;
        this.velocidad = 4.0; // Aumenta la velocidad en emergencia
    }
    

    public void desactivarEmergencia() {
        this.enEmergencia = false;
        this.velocidad = 3.0; // Vuelve a la velocidad normal
    }
    

    public int atenderAccidente() {
        // Lógica específica para atender accidentes
        return 8000; // 8 segundos para atender un accidente
    }
    

    public boolean isEnEmergencia() {
        return enEmergencia;
    }
}