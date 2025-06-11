package org.example.tareaintegradora2apo.model.trafico;

import javafx.geometry.Point2D;
import org.example.tareaintegradora2apo.controller.SimuladorSGMMS;
import org.example.tareaintegradora2apo.model.map.Grafo;
import org.example.tareaintegradora2apo.model.vehiculos.VehiculoCivil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Gestor especializado para vehículos civiles autónomos
 */
public class GestorVehiculos {

    private List<VehiculoCivil> vehiculosCiviles;
    private SimuladorSGMMS simulador;
    private Random random;

    // Configuración del sistema
    private final int MAX_VEHICULOS_CIVILES = 25;
    private final int MIN_VEHICULOS_CIVILES = 8;
    private final double PROBABILIDAD_NUEVO_VEHICULO = 0.15;
    private final double PROBABILIDAD_REMOVER_VEHICULO = 0.05;

    // Estadísticas
    private Map<String, Integer> contadorTipos;
    private int vehiculosGenerados;
    private int vehiculosRemovidos;

    public GestorVehiculos(SimuladorSGMMS simulador) {
        this.simulador = simulador;
        this.vehiculosCiviles = new CopyOnWriteArrayList<>();
        this.random = new Random();
        this.contadorTipos = new HashMap<>();
        this.vehiculosGenerados = 0;
        this.vehiculosRemovidos = 0;

        inicializarContadores();
    }

    private void inicializarContadores() {
        contadorTipos.put("Sedan", 0);
        contadorTipos.put("SUV", 0);
        contadorTipos.put("Camión", 0);
        contadorTipos.put("Motocicleta", 0);
    }

    /**
     * Genera un nuevo vehículo civil en un punto de entrada aleatorio
     */
    public VehiculoCivil generarVehiculoCivil() {
        if (vehiculosCiviles.size() >= MAX_VEHICULOS_CIVILES) {
            return null;
        }

        Point2D posicionEntrada = simulador.getMapa().getPuntoEntradaAleatorio();
        String id = "C" + (vehiculosGenerados + 1);

        VehiculoCivil vehiculo = new VehiculoCivil(id, posicionEntrada, simulador);
        vehiculosCiviles.add(vehiculo);


        vehiculosGenerados++;

        // Asignar ruta inicial inteligente
        vehiculo.asignarRutaInteligente(simulador.getMapa().getGrafo());
        vehiculo.iniciar();

        return vehiculo;
    }

    /**
     * Remueve vehículos que han completado muchas rutas o están inactivos
     */
    public void limpiarVehiculosInactivos() {
        if (vehiculosCiviles.size() <= MIN_VEHICULOS_CIVILES) {
            return;
        }

        List<VehiculoCivil> vehiculosARemover = new ArrayList<>();

        for (VehiculoCivil vehiculo : vehiculosCiviles) {
            boolean debeRemover = false;

            // Remover si ha completado muchas rutas
            if (vehiculo.getContadorRutas() > 8) {
                debeRemover = true;
            }

            // Remover aleatoriamente algunos vehículos
            if (random.nextDouble() < PROBABILIDAD_REMOVER_VEHICULO) {
                debeRemover = true;
            }

            // Remover vehículos que están "perdidos" (sin movimiento por mucho tiempo)
            if (!vehiculo.isEnMovimiento() && !vehiculo.isDisponible()) {
                debeRemover = true;
            }

            if (debeRemover) {
                vehiculosARemover.add(vehiculo);
            }
        }

        // Remover vehículos seleccionados
        for (VehiculoCivil vehiculo : vehiculosARemover) {
            removerVehiculo(vehiculo);
        }
    }

    /**
     * Remueve un vehículo específico del sistema
     */
    public void removerVehiculo(VehiculoCivil vehiculo) {
        vehiculo.detener();
        vehiculosCiviles.remove(vehiculo);

        vehiculosRemovidos++;
    }

    /**
     * Gestiona automáticamente la población de vehículos civiles
     */
    public void gestionarPoblacionVehiculos() {
        int vehiculosActuales = vehiculosCiviles.size();

        // Generar nuevos vehículos si hay pocos
        if (vehiculosActuales < MIN_VEHICULOS_CIVILES) {
            generarVehiculoCivil();
        } else if (vehiculosActuales < MAX_VEHICULOS_CIVILES &&
                random.nextDouble() < PROBABILIDAD_NUEVO_VEHICULO) {
            generarVehiculoCivil();
        }

        // Limpiar vehículos inactivos
        limpiarVehiculosInactivos();

        // Reasignar rutas a vehículos estancados
        reasignarRutasEstancadas();
    }

    /**
     * Reasigna rutas a vehículos que están estancados
     */
    private void reasignarRutasEstancadas() {
        for (VehiculoCivil vehiculo : vehiculosCiviles) {
            if (vehiculo.isDisponible() && !vehiculo.isEnMovimiento()) {
                // Si el vehículo lleva mucho tiempo sin moverse, darle nueva ruta
                if (random.nextDouble() < 0.3) { // 30% de probabilidad
                    vehiculo.asignarRutaInteligente(simulador.getMapa().getGrafo());
                }
            }
        }
    }

    /**
     * Reinicia vehículos que han salido del mapa
     */
    public void reiniciarVehiculosFueraDeMapa() {
        for (VehiculoCivil vehiculo : vehiculosCiviles) {
            Point2D pos = vehiculo.getPosicion();

            // Verificar si está fuera de los límites del mapa
            if (pos.getX() < 0 || pos.getX() > 2000 ||
                    pos.getY() < 0 || pos.getY() > 2000) {

                // Reiniciar en un punto de entrada aleatorio
                Point2D nuevaPosicion = simulador.getMapa().getPuntoEntradaAleatorio();
                vehiculo.reiniciarEnPuntoEntrada(nuevaPosicion);
                vehiculo.asignarRutaInteligente(simulador.getMapa().getGrafo());
            }
        }
    }

    /**
     * Obtiene estadísticas de los vehículos civiles
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total_vehiculos", vehiculosCiviles.size());
        stats.put("vehiculos_en_movimiento",
                vehiculosCiviles.stream().mapToInt(v -> v.isEnMovimiento() ? 1 : 0).sum());
        stats.put("vehiculos_generados", vehiculosGenerados);
        stats.put("vehiculos_removidos", vehiculosRemovidos);
        stats.put("tipos_vehiculos", new HashMap<>(contadorTipos));

        // Estadísticas de comportamiento
        double promedioViolaciones = vehiculosCiviles.stream()
                .mapToDouble(VehiculoCivil::getProbabilidadViolacion)
                .average().orElse(0.0);
        stats.put("promedio_probabilidad_violacion", promedioViolaciones);

        return stats;
    }

    // Getters
    public List<VehiculoCivil> getVehiculosCiviles() {
        return new ArrayList<>(vehiculosCiviles);
    }

    public int getCantidadVehiculos() {
        return vehiculosCiviles.size();
    }

    public void detenerTodos() {
        for (VehiculoCivil vehiculo : vehiculosCiviles) {
            vehiculo.detener();
        }
        vehiculosCiviles.clear();
    }
}
