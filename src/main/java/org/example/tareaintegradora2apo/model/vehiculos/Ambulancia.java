package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;

/**
 * Clase que representa una ambulancia en el sistema SGMMS.
 * Las ambulancias tienen alta velocidad y pueden saltarse semáforos en emergencias.
 */
public class Ambulancia extends Vehiculo {
    
    private boolean enEmergencia;
    
    /**
     * Constructor para la clase Ambulancia
     * @param id Identificador único de la ambulancia
     * @param posicionInicial Posición inicial en el mapa
     */
    public Ambulancia(String id, Point2D posicionInicial, SimuladorSGMMS simulador) {
        super(id, posicionInicial, 3.0,simulador); // Velocidad alta
        this.prioridad = 3.0; // Prioridad alta
        this.enEmergencia = false;
    }
    
    /**
     * Las ambulancias pueden saltarse semáforos en emergencias
     * @param semaforo Semáforo a verificar
     * @return true si debe detenerse, false en caso contrario
     */
    @Override
    public boolean debeDetenerse(Semaforo semaforo) {
        // Si está en emergencia, no se detiene en semáforos
        return !enEmergencia && semaforo.isRojo();
    }
    
    /**
     * Activa el modo emergencia de la ambulancia
     */
    public void activarEmergencia() {
        this.enEmergencia = true;
        this.velocidad = 4.0; // Aumenta la velocidad en emergencia
    }
    
    /**
     * Desactiva el modo emergencia de la ambulancia
     */
    public void desactivarEmergencia() {
        this.enEmergencia = false;
        this.velocidad = 3.0; // Vuelve a la velocidad normal
    }
    
    /**
     * Método específico para atender accidentes
     * @return Tiempo que tarda en resolver el accidente
     */
    public int atenderAccidente() {
        // Lógica específica para atender accidentes
        return 8000; // 8 segundos para atender un accidente
    }
    
    /**
     * Verifica si la ambulancia está en modo emergencia
     * @return true si está en emergencia, false en caso contrario
     */
    public boolean isEnEmergencia() {
        return enEmergencia;
    }
}