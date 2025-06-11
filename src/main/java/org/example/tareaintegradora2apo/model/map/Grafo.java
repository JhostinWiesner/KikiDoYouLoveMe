package org.example.tareaintegradora2apo.model.map;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.model.trafico.Ruta;

import java.util.*;

/**
 * Clase que representa un grafo para la navegación de vehículos en el mapa.
 */
public class Grafo {

    private Map<String, MapNodo> nodos;
    private List<MapArista> aristas;

    /**
     * Constructor para la clase Grafo
     */
    public Grafo() {
        this.nodos = new HashMap<>();
        this.aristas = new ArrayList<>();
    }

    /**
     * Agrega un nodo al grafo
     * @param id Identificador único del nodo
     * @param posicion Posición del nodo en el mapa
     * @return Nodo creado
     */
    public MapNodo agregarNodo(String id, Point2D posicion,boolean tieneSemaforo) {
        MapNodo nodo = new MapNodo(id, posicion,tieneSemaforo);
        nodos.put(id, nodo);
        return nodo;
    }

    /**
     * Agrega una arista al grafo
     * @param id Identificador único de la arista
     * @param idOrigen ID del nodo origen
     * @param idDestino ID del nodo destino
     * @param peso Peso de la arista (distancia o tiempo)
     * @return Arista creada
     */
    public MapArista agregarArista(String id, String idOrigen, String idDestino, double peso) {
        MapNodo origen = nodos.get(idOrigen);
        MapNodo destino = nodos.get(idDestino);

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Los nodos origen y destino deben existir");
        }

        MapArista arista = new MapArista(id, origen, destino, peso);
        aristas.add(arista);
        origen.agregarAristaAdyacente(arista);
        destino.agregarAristaAdyacente(arista);


        return arista;
    }

    /**
     * Obtiene un nodo por su ID
     * @param id ID del nodo
     * @return Nodo o null si no existe
     */
    public MapNodo getNodo(String id) {
        return nodos.get(id);
    }

    /**
     * Obtiene todos los nodos del grafo
     * @return Colección de nodos
     */
    public Collection<MapNodo> getNodos() {
        return nodos.values();
    }

    /**
     * Obtiene todas las aristas del grafo
     * @return Lista de aristas
     */
    public List<MapArista> getAristas() {
        return aristas;
    }

    /**
     * Encuentra el nodo más cercano a una posición dada
     * @param posicion Posición en el mapa
     * @return Nodo más cercano
     */
    public MapNodo getNodoMasCercano(Point2D posicion) {
        MapNodo nodoMasCercano = null;
        double distanciaMinima = Double.MAX_VALUE;

        for (MapNodo nodo : nodos.values()) {
            double distancia = nodo.getPosicion().distance(posicion);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                nodoMasCercano = nodo;
            }
        }

        return nodoMasCercano;
    }

    /**
     * Calcula la ruta más corta entre dos posiciones usando el algoritmo de Dijkstra
     * @param inicio Posición de inicio
     * @param fin Posición de destino
     * @return Ruta calculada
     */
    public Ruta calcularRuta(Point2D inicio, Point2D fin) {
        // Encontrar los nodos más cercanos a las posiciones de inicio y fin
        MapNodo nodoInicio = getNodoMasCercano(inicio);
        MapNodo nodoFin = getNodoMasCercano(fin);

        // Implementación del algoritmo de Dijkstra
        Map<MapNodo, Double> distancias = new HashMap<>();
        Map<MapNodo, MapNodo> predecesores = new HashMap<>();
        PriorityQueue<MapNodo> cola = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));

        // Inicializar distancias
        for (MapNodo nodo : nodos.values()) {
            distancias.put(nodo, Double.MAX_VALUE);
        }
        distancias.put(nodoInicio, 0.0);

        cola.add(nodoInicio);

        while (!cola.isEmpty()) {
            MapNodo actual = cola.poll();

            if (actual.equals(nodoFin)) {
                break;
            }

            for (MapArista arista : actual.getAristasAdyacentes()) {
                MapNodo vecino = arista.getDestino();
                double nuevaDistancia = distancias.get(actual) + arista.getPeso();

                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, actual);

                    // Actualizar la cola
                    cola.remove(vecino);
                    cola.add(vecino);
                }
            }
        }

        // Reconstruir la ruta
        List<Point2D> puntos = new ArrayList<>();
        MapNodo actual = nodoFin;

        // Si no hay ruta, devolver una ruta directa
        if (!predecesores.containsKey(nodoFin)) {
            puntos.add(inicio);
            puntos.add(fin);
            return new Ruta("ruta_directa", puntos, this);
        }

        // Reconstruir la ruta desde el fin hasta el inicio
        while (actual != null) {
            puntos.add(0, actual.getPosicion());
            actual = predecesores.get(actual);
        }

        // Agregar los puntos de inicio y fin exactos
        puntos.add(0, inicio);
        puntos.add(fin);

        return new Ruta("ruta_" + System.currentTimeMillis(), puntos, this);
    }

    /**
     * Verifica si hay congestión en un nodo (más de N vehículos en la misma área)
     * @param nodo Nodo a verificar
     * @param vehiculos Lista de vehículos en el mapa
     * @param umbral Umbral de congestión
     * @return true si hay congestión, false en caso contrario
     */
    public boolean hayCongestión(MapNodo nodo, List<Point2D> vehiculos, int umbral) {
        int contador = 0;
        double radioCongestión = 20.0; // Radio en píxeles

        for (Point2D posVehiculo : vehiculos) {
            if (nodo.getPosicion().distance(posVehiculo) <= radioCongestión) {
                contador++;
                if (contador >= umbral) {
                    return true;
                }
            }
        }

        return false;
    }
}