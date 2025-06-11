package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;

/**
 * Clase que representa un camión de bomberos en el sistema SGMMS.
 * Los bomberos tienen alta velocidad en emergencias y prioridad específica para incendios.
 */
public class Bombero extends Vehiculo {
    
    private boolean enEmergencia;
    
    /**
     * Constructor para la clase Bombero
     * @param id Identificador único del camión de bomberos
     * @param posicionInicial Posición inicial en el mapa
     */
    public Bombero(String id, Point2D posicionInicial, SimuladorSGMMS simulador) {
        super(id, posicionInicial, 2.5,simulador); // Velocidad media-alta
        this.prioridad = 2.5; // Prioridad media-alta
        this.enEmergencia = false;
    }
    
    /**
     * Los bomberos pueden saltarse semáforos en emergencias
     * @param semaforo Semáforo a verificar
     * @return true si debe detenerse, false en caso contrario
     */
    @Override
    public boolean debeDetenerse(Semaforo semaforo) {
        // Si está en emergencia, no se detiene en semáforos
        return !enEmergencia && semaforo.isRojo();
    }
    
    /**
     * Activa el modo emergencia del camión de bomberos
     */
    public void activarEmergencia() {
        this.enEmergencia = true;
        this.velocidad = 3.5; // Aumenta la velocidad en emergencia
    }
    
    /**
     * Desactiva el modo emergencia del camión de bomberos
     */
    public void desactivarEmergencia() {
        this.enEmergencia = false;
        this.velocidad = 2.5; // Vuelve a la velocidad normal
    }
    
    /**
     * Método específico para atender incendios
     * @return Tiempo que tarda en resolver el incendio
     */
    public int atenderIncendio() {
        // Lógica específica para atender incendios
        return 10000; // 10 segundos para atender un incendio
    }
    
    /**
     * Verifica si el camión de bomberos está en modo emergencia
     * @return true si está en emergencia, false en caso contrario
     */
    public boolean isEnEmergencia() {
        return enEmergencia;
    }
}