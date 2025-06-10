package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;

import org.example.tareaintegradora2apo.model.trafico.Semaforo;

/**
 * Clase que representa una patrulla de policía en el sistema SGMMS.
 * Las patrullas tienen velocidad media y respetan los semáforos.
 */
public class Patrulla extends Vehiculo {
    
    /**
     * Constructor para la clase Patrulla
     * @param id Identificador único de la patrulla
     * @param posicionInicial Posición inicial en el mapa
     */
    public Patrulla(String id, Point2D posicionInicial) {
        super(id, posicionInicial, 2.0); // Velocidad media
        this.prioridad = 2.0; // Prioridad media
    }
    
    /**
     * Las patrullas respetan los semáforos en rojo
     * @param semaforo Semáforo a verificar
     * @return true si el semáforo está en rojo, false en caso contrario
     */
    @Override
    public boolean debeDetenerse(Semaforo semaforo) {
        return semaforo.isRojo();
    }
    
    /**
     * Método específico para atender robos
     * @return Tiempo que tarda en resolver el robo
     */
    public int atenderRobo() {
        // Lógica específica para atender robos
        return 5000; // 5 segundos para atender un robo
    }
}