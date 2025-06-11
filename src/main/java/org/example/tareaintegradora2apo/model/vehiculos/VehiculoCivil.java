// Mejoras para VehiculoCivil.java
package org.example.tareaintegradora2apo.model.vehiculos;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.map.Grafo;
import org.example.tareaintegradora2apo.model.map.MapNodo;
import org.example.tareaintegradora2apo.model.trafico.Ruta;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase mejorada que representa un vehículo civil autónomo en el sistema SGMMS.
 */
public class VehiculoCivil extends Vehiculo implements Runnable {

    private double probabilidadViolacion;
    private Random random;
    private Point2D destinoFinal;
    private boolean enRutaLarga; // Para viajes más largos por la ciudad
    private int contadorRutas; // Contador de rutas completadas
    private long tiempoUltimaRuta; // Para controlar frecuencia de nuevas rutas

    // Comportamientos específicos por tipo
    private double factorVelocidad;
    private double factorAgresividad;

    public VehiculoCivil(String id, Point2D posicionInicial, SimuladorSGMMS simuladorSGMMS) {
        super(id, posicionInicial, 1.0 + Math.random(), simuladorSGMMS);

        this.random = new Random();
        this.contadorRutas = 0;
        this.tiempoUltimaRuta = System.currentTimeMillis();
        velocidad = 5 + random.nextDouble(1,3);

        // Probabilidad de violación basada en agresividad
        this.probabilidadViolacion = 0.02 + (factorAgresividad * 0.15); // 2% a 17%
        this.prioridad = 1.0; // Prioridad baja para vehículos civiles
    }

    @Override
    public boolean debeDetenerse(Semaforo semaforo) {
        if (semaforo.isRojo()) {
            // Factores que influyen en la decisión
            boolean respetaSemaforo = true;

            // Factor de agresividad del conductor
            if (random.nextDouble() < probabilidadViolacion) {
                respetaSemaforo = false;
            }
            // Registrar comportamiento
            if (respetaSemaforo) {
                semaforo.registrarRespeto();
            } else {
                semaforo.registrarViolacion();
            }

            return respetaSemaforo;
        }
        return false;
    }

    /**
     * Asigna una ruta inteligente basada en patrones de tráfico reales
     */
    public void asignarRutaInteligente(Grafo grafo) {
        List<MapNodo> nodos = new ArrayList<>(grafo.getNodos());
        if (nodos.size() < 2) return;

        MapNodo origen = grafo.getNodoMasCercano(this.posicion);
        MapNodo destino;

        // Decidir tipo de viaje
        double probabilidadViajeLargo = 0.3;
        boolean esViajeLargo = random.nextDouble() < probabilidadViajeLargo;

        if (esViajeLargo) {
            // Viaje largo: buscar nodo lejano
            destino = seleccionarDestinoLejano(nodos, origen);
            enRutaLarga = true;
        } else {
            // Viaje corto: buscar nodo cercano
            destino = seleccionarDestinoCercano(nodos, origen);
            enRutaLarga = false;
        }

        if (destino != null) {
            destinoFinal = destino.getPosicion();
            Ruta ruta = grafo.calcularRuta(this.posicion, destinoFinal);
            asignarRuta(ruta);
            contadorRutas++;
        }
    }

    private MapNodo seleccionarDestinoLejano(List<MapNodo> nodos, MapNodo origen) {
        MapNodo mejorDestino = null;
        double maxDistancia = 0;

        for (MapNodo nodo : nodos) {
            if (nodo.equals(origen)) continue;

            double distancia = origen.getPosicion().distance(nodo.getPosicion());
            if (distancia > maxDistancia && distancia > 200) { // Mínimo 200 unidades
                maxDistancia = distancia;
                mejorDestino = nodo;
            }
        }

        return mejorDestino != null ? mejorDestino :
                nodos.get(random.nextInt(nodos.size()));
    }

    private MapNodo seleccionarDestinoCercano(List<MapNodo> nodos, MapNodo origen) {
        List<MapNodo> nodosCercanos = new ArrayList<>();

        for (MapNodo nodo : nodos) {
            if (nodo.equals(origen)) continue;

            double distancia = origen.getPosicion().distance(nodo.getPosicion());
            if (distancia <= 300 && distancia >= 50) { // Entre 50 y 300 unidades
                nodosCercanos.add(nodo);
            }
        }

        return nodosCercanos.isEmpty() ?
                nodos.get(random.nextInt(nodos.size())) :
                nodosCercanos.get(random.nextInt(nodosCercanos.size()));
    }

    @Override
    protected void mover() {
        super.mover();

        // Si llegó al destino
        if (!enMovimiento.get() && isDisponible()) {
            long tiempoActual = System.currentTimeMillis();
            long tiempoEspera = enRutaLarga ? 3000 : 1500; // 3s para viajes largos, 1.5s para cortos

            // Esperar antes de asignar nueva ruta
            if (tiempoActual - tiempoUltimaRuta > tiempoEspera) {
                // Probabilidad de continuar viajando
                double probabilidadContinuar = calcularProbabilidadContinuar();

                if (random.nextDouble() < probabilidadContinuar) {
                    asignarRutaInteligente(rutaActual.getGrafo());
                    tiempoUltimaRuta = tiempoActual;
                } else {
                    // Vehículo "se estaciona" temporalmente
                    try {
                        Thread.sleep(5000 + random.nextInt(10000)); // 5-15 segundos
                        tiempoUltimaRuta = System.currentTimeMillis();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private double calcularProbabilidadContinuar() {
        // Factores que influyen en continuar viajando
        double probabilidadBase = 0.7;

        // Reducir probabilidad después de muchas rutas
        if (contadorRutas > 5) {
            probabilidadBase -= 0.1 * (contadorRutas - 5);
        }

        return Math.max(0.2, Math.min(0.9, probabilidadBase));
    }

    /**
     * Método para reiniciar el vehículo en un nuevo punto de entrada
     */
    public void reiniciarEnPuntoEntrada(Point2D nuevaPosicion) {
        this.posicion = nuevaPosicion;
        this.contadorRutas = 0;
        this.enRutaLarga = false;
        this.tiempoUltimaRuta = System.currentTimeMillis();
        this.disponible = true;
        this.enMovimiento.set(false);
    }

    public double getProbabilidadViolacion() {
        return probabilidadViolacion;
    }

    public int getContadorRutas() {
        return contadorRutas;
    }

    public boolean isEnRutaLarga() {
        return enRutaLarga;
    }

    public double getFactorAgresividad() {
        return factorAgresividad;
    }
}
