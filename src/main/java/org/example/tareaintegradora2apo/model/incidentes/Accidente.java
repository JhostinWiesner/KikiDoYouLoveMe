package org.example.tareaintegradora2apo.model.incidentes;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.model.vehiculos.Vehiculo;

import java.util.ArrayList;
import java.util.List;


public class Accidente extends Incidente {
    
    private List<Vehiculo> vehiculosInvolucrados;
    private boolean semaforo;
    private boolean excesVelocidad;
    private boolean congestion;
    

    public Accidente(String id, Point2D posicion, boolean semaforo, boolean excesVelocidad, boolean congestion) {
        // Los accidentes tienen prioridad alta (4) y requieren más tiempo de atención
        super(id, posicion, 4, 8000);
        this.vehiculosInvolucrados = new ArrayList<>();
        this.semaforo = semaforo;
        this.excesVelocidad = excesVelocidad;
        this.congestion = congestion;
    }
    

    public void agregarVehiculoInvolucrado(Vehiculo vehiculo) {
        vehiculosInvolucrados.add(vehiculo);
    }
    


    public List<Vehiculo> getVehiculosInvolucrados() {
        return vehiculosInvolucrados;
    }
    

    public boolean isSemaforo() {
        return semaforo;
    }
    

    public boolean isExcesVelocidad() {
        return excesVelocidad;
    }
    

    public boolean isCongestion() {
        return congestion;
    }
    

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