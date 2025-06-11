package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.map.Grafo;
import org.example.tareaintegradora2apo.model.map.MapNodo;
import org.example.tareaintegradora2apo.model.trafico.Ruta;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;


import java.util.Random;



public class VehiculoCivil extends Vehiculo implements Runnable {

    private Image actualImage;
    private double probabilidadViolacion; // Probabilidad de violar un semáforo
    private Random random;

    public VehiculoCivil(String id, Point2D posicionInicial, SimuladorSGMMS simuladorSGMMS) {
        super(id, posicionInicial, 1.0 + Math.random(),simuladorSGMMS); // Velocidad aleatoria entre 1.0 y 2.0
        this.probabilidadViolacion = 0.05 + Math.random() * 0.15; // Entre 5% y 20%
        this.random = new Random();
        this.prioridad = 1.0; // Prioridad baja
    }
    

    @Override
    public boolean debeDetenerse(Semaforo semaforo) {
        if (semaforo.isRojo()) {
            // Decidir si respeta o viola el semáforo
            boolean respeta = random.nextDouble() > probabilidadViolacion;
            
            if (respeta) {
                semaforo.registrarRespeto();
            } else {
                semaforo.registrarViolacion();
            }
            
            return respeta;
        }
        return false;
    }
    

    public void asignarRutaAleatoria(Grafo grafo) {
        // Obtener nodos aleatorios de origen y destino
        MapNodo[] nodos = grafo.getNodos().toArray(new MapNodo[0]);
        if (nodos.length < 2) return;
        
        int indiceOrigen = random.nextInt(nodos.length);
        int indiceDestino;
        do {
            indiceDestino = random.nextInt(nodos.length);
        } while (indiceDestino == indiceOrigen);
        
        MapNodo origen = nodos[indiceOrigen];
        MapNodo destino = nodos[indiceDestino];
        
        // Calcular ruta
        Ruta ruta = grafo.calcularRuta(origen.getPosicion(), destino.getPosicion());
        asignarRuta(ruta);
    }
    


    public double getProbabilidadViolacion() {
        return probabilidadViolacion;
    }
    
    @Override
    protected void mover() {
        super.mover();
        
        // Si llegó al destino y está disponible, asignar nueva ruta aleatoria
        if (!enMovimiento.get() && isDisponible() && Math.random() < 0.8) {
            // 80% de probabilidad de continuar con una nueva ruta
            asignarRutaAleatoria(rutaActual.getGrafo());
        }
    }
}