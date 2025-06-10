package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.model.vehiculos.Vehiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un accidente de tráfico en el sistema SGMMS.
 */
public class Accidente extends Incidente {
    
    private List<Vehiculo> vehiculosInvolucrados;
    private boolean semaforo; // true si fue causado por violación de semáforo
    private boolean excesVelocidad; // true si fue causado por exceso de velocidad
    private boolean congestion; // true si fue causado por congestión
    
    /**
     * Constructor para la clase Accidente
     * @param id Identificador único del accidente
     * @param posicion Posición en el mapa donde ocurrió el accidente
     * @param semaforo Indica si fue causado por violación de semáforo
     * @param excesVelocidad Indica si fue causado por exceso de velocidad
     * @param congestion Indica si fue causado por congestión
     */
    public Accidente(String id, Point2D posicion, boolean semaforo, boolean excesVelocidad, boolean congestion) {
        // Los accidentes tienen prioridad alta (4) y requieren más tiempo de atención
        super(id, posicion, 4, 8000);
        this.vehiculosInvolucrados = new ArrayList<>();
        this.semaforo = semaforo;
        this.excesVelocidad = excesVelocidad;
        this.congestion = congestion;
    }
    
    /**
     * Agrega un vehículo involucrado en el accidente
     * @param vehiculo Vehículo involucrado
     */
    public void agregarVehiculoInvolucrado(Vehiculo vehiculo) {
        vehiculosInvolucrados.add(vehiculo);
    }
    
    /**
     * Obtiene la lista de vehículos involucrados en el accidente
     * @return Lista de vehículos
     */
    public List<Vehiculo> getVehiculosInvolucrados() {
        return vehiculosInvolucrados;
    }
    
    /**
     * Verifica si el accidente fue causado por violación de semáforo
     * @return true si fue causado por violación de semáforo
     */
    public boolean isSemaforo() {
        return semaforo;
    }
    
    /**
     * Verifica si el accidente fue causado por exceso de velocidad
     * @return true si fue causado por exceso de velocidad
     */
    public boolean isExcesVelocidad() {
        return excesVelocidad;
    }
    
    /**
     * Verifica si el accidente fue causado por congestión
     * @return true si fue causado por congestión
     */
    public boolean isCongestion() {
        return congestion;
    }
    
    /**
     * Obtiene la causa del accidente como texto
     * @return Descripción de la causa
     */
    public String getCausa() {
        if (semaforo) {
            return "Violación de semáforo";
        } else if (excesVelocidad) {
            return "Exceso de velocidad";
        } else if (congestion) {
            return "Congestión de tráfico";
        } else {
            return "Causa desconocida";
        }
    }
    
    @Override
    public String getTipo() {
        return "Accidente";
    }
    
    @Override
    public String toString() {
        return String.format("%s - Causa: %s - Vehículos: %d", 
                super.toString(), getCausa(), vehiculosInvolucrados.size());
    }
}