package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;

import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;


public class Patrulla extends Vehiculo {
    

    public Patrulla(String id, Point2D posicionInicial, SimuladorSGMMS simulador) {
        super(id, posicionInicial, 2.0,simulador); // Velocidad media
        this.prioridad = 2.0; // Prioridad media
    }
    

    @Override
    public boolean debeDetenerse(Semaforo semaforo) {
        return semaforo.isRojo();
    }
    

    public int atenderRobo() {

        return 5000;
    }
}