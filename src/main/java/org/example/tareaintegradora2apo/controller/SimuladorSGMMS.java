package org.example.tareaintegradora2apo.controller;

import javafx.geometry.Point2D;

import org.example.tareaintegradora2apo.model.map.Grafo;
import org.example.tareaintegradora2apo.model.map.MapNodo;
import org.example.tareaintegradora2apo.model.map.Mapa;
import org.example.tareaintegradora2apo.model.sistema.GestorPuntuacion;
import org.example.tareaintegradora2apo.model.trafico.GestorTrafico;
import org.example.tareaintegradora2apo.model.trafico.Ruta;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;
import org.example.tareaintegradora2apo.model.estructuras.*;
import org.example.tareaintegradora2apo.model.incidentes.*;
import org.example.tareaintegradora2apo.model.vehiculos.*;
//import org.example.tareaintegradora2apo.model.vehiculos.;
import org.example.tareaintegradora2apo.model.incidentes.Incendio;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class SimuladorSGMMS {

    // Estructuras de datos principales
    private BST<Incidente> bstIncidentes;
    private BST<Ruta> bstRutas;
    private Mapa mapa;
    private GestorTrafico gestorTrafico;
    private GestorPuntuacion gestorPuntuacion;

    // Listas de entidades del simulador
    private List<Vehiculo> vehiculos;
    private List<Incidente> incidentesActivos;
    private List<Observer> observadores;
    private List<Vehiculo> vehiculosDisponibles;  // Lista de vehículos predefinidos


    // Generadores de entidades
    private ScheduledExecutorService generadorIncidentes;
    private ScheduledExecutorService generadorVehiculos;


    // Estado del simulador
    private boolean simulacionActiva;
    private long tiempoInicio;

    // Contadores para IDs únicos
    private int contadorVehiculos;
    private int contadorIncidentes;


    private List<VehiculoCivil> vehiculosCiviles;
    private Map<String, Point2D> edificiosServicio; // Edificios de donde salen los vehículos de emergencia

    /**
     * Interfaz Observer para notificar cambios en el simulador
     */
    public interface Observer {
        void onIncidenteCreado(Incidente incidente);

        void onIncidenteAtendido(Incidente incidente);

        void onVehiculoCreado(Vehiculo vehiculo);

        void onVehiculoAsignado(Vehiculo vehiculo, Incidente incidente);

        void onPuntuacionActualizada(int puntuacion);

        void onEstadisticasActualizadas(Map<String, Object> estadisticas);

        void onVehiculoDisponible(Vehiculo vehiculo);
    }


    public SimuladorSGMMS() throws IOException {
        // Inicializar estructuras de datos con sus comparadores específicos
        this.bstIncidentes = new BST<>(new ComparatorIncidente());
        this.bstRutas = new BST<>(new ComparatorRuta());

        // Inicializar componentes del sistema
        this.mapa = new Mapa("/resources/images/palmira_map.jgg");
        //this.mapa = new Mapa(null);
        this.gestorTrafico = new GestorTrafico();
        this.gestorPuntuacion = new GestorPuntuacion();

        // Inicializar listas
        this.vehiculos = new CopyOnWriteArrayList<>();
        this.vehiculosCiviles = new CopyOnWriteArrayList<>();
        this.incidentesActivos = new CopyOnWriteArrayList<>();
        this.observadores = new ArrayList<>();
        this.edificiosServicio = new HashMap<>();
        this.vehiculosDisponibles = new ArrayList<>();

        // Inicializar estado
        this.simulacionActiva = false;
        this.contadorVehiculos = 0;
        this.contadorIncidentes = 0;

        // Configurar el mapa
        mapa.inicializarGrafoPalmira();

        // Configurar el gestor de tráfico con los semáforos del mapa
        gestorTrafico.configurarSemaforos(mapa.getSemaforos());
        // Configurar edificios de servicio
        configurarEdificiosServicio();
        for (int i = 0; i < 4; i++) {
            Vehiculo ambulancia = new Ambulancia("A" + (++contadorVehiculos), edificiosServicio.get("hospital"), this);
            vehiculosDisponibles.add(ambulancia);
             vehiculos.add(ambulancia);
        }

// Crear 4 patrullas
        for (int i = 0; i < 4; i++) {
            Vehiculo patrulla = new Patrulla("P" + (++contadorVehiculos), edificiosServicio.get("estacion_policia"), this);
            vehiculosDisponibles.add(patrulla);
            vehiculos.add(patrulla);
        }

// Crear 2 bomberos
        for (int i = 0; i < 2; i++) {
            Vehiculo bombero = new Bombero("B" + (++contadorVehiculos), edificiosServicio.get("estacion_bomberos"), this);
            vehiculosDisponibles.add(bombero);
             vehiculos.add(bombero);
        }

    }

    private void configurarEdificiosServicio() {
        // Estos valores deben ajustarse según las coordenadas reales del mapa
        edificiosServicio.put("estacion_policia", new Point2D(1486, 1218));
        edificiosServicio.put("hospital", new Point2D(250, 1547));
        edificiosServicio.put("estacion_bomberos", new Point2D(844, 347));
    }


    public void iniciarSimulacion() {
        if (simulacionActiva) {
            return;
        }

        simulacionActiva = true;
        tiempoInicio = System.currentTimeMillis();

        // Iniciar semáforos
        gestorTrafico.iniciarSemaforos();

        // Generar vehículos iniciales
        generarVehiculosIniciales();

        // Configurar generadores automáticos
        configurarGeneradores();

        notificarObservadores(obs -> obs.onEstadisticasActualizadas(obtenerEstadisticas()));
    }


    public void detenerSimulacion() {
        if (!simulacionActiva) {
            return;
        }

        simulacionActiva = false;

        // Detener generadores
        if (generadorIncidentes != null) {
            generadorIncidentes.shutdown();
        }
        if (generadorVehiculos != null) {
            generadorVehiculos.shutdown();
        }

        // Detener vehículos
        for (Vehiculo vehiculo : vehiculos) {
            vehiculo.detener();
        }

        //Detener vehiculos civiles
        for (VehiculoCivil vehiculo : vehiculosCiviles) {
            vehiculo.detener();
        }

        // Detener semáforos
        gestorTrafico.detenerSemaforos();
    }


    private void generarVehiculosIniciales() {
        // Ya no generamos vehículos de servicio aquí, solo vehículos civiles
        for (int i = 0; i < 10; i++) {
            generarVehiculoCivil();
        }

        for (Vehiculo vehiculo : vehiculos) {
            notificarVehiculoDisponible(vehiculo);
        }
    }


    private void generarVehiculoCivil() {
        Point2D posicion = mapa.getPuntoEntradaAleatorio();
        String[] tipos = {"Sedan", "SUV", "Camión", "Motocicleta"};
        String tipo = tipos[new Random().nextInt(tipos.length)];

        VehiculoCivil vehiculo = new VehiculoCivil("C" + (++contadorVehiculos), posicion, this);
        vehiculosCiviles.add(vehiculo);

        // Asignar ruta aleatoria
        vehiculo.asignarRutaAleatoria(mapa.getGrafo());

        // Iniciar movimiento
        vehiculo.iniciar();
    }

    private void generarVehiculoAleatorio() {
        if (!simulacionActiva || vehiculosCiviles.size() >= 30) {
            return;
        }

        generarVehiculoCivil();
    }

    private void configurarGeneradores() {
        // Generador de incidentes
        generadorIncidentes = Executors.newScheduledThreadPool(1);
        generadorIncidentes.scheduleAtFixedRate(this::generarIncidenteAleatorio, 5, 10, TimeUnit.SECONDS);

        // Generador de vehículos adicionales
        generadorVehiculos = Executors.newScheduledThreadPool(1);
        generadorVehiculos.scheduleAtFixedRate(this::generarVehiculoAleatorio, 15, 20, TimeUnit.SECONDS);

        // Verificador de accidentes por lógica
        ScheduledExecutorService verificadorAccidentes = Executors.newScheduledThreadPool(1);
        verificadorAccidentes.scheduleAtFixedRate(this::verificarAccidentes, 2, 3, TimeUnit.SECONDS);

        // AÑADIR: Procesador de vehículos que terminaron su trabajo
        ScheduledExecutorService procesadorVehiculos = Executors.newScheduledThreadPool(1);
        procesadorVehiculos.scheduleAtFixedRate(this::procesarVehiculosQueTerminaron, 1, 2, TimeUnit.SECONDS);
    }


    private void generarIncidenteAleatorio() {
        if (!simulacionActiva) {
            return;
        }

        Random random = new Random();
        Incidente incidente;

        // Generar Robo
        if (random.nextInt(2) == 0 && !mapa.getNodosIncidentes().isEmpty()) {
            Point2D posicionRobo = mapa.getNodosIncidentes().get(random.nextInt(mapa.getNodosIncidentes().size()));
            incidente = new Robo("R" + (++contadorIncidentes), posicionRobo, "robo");

            // Generar Incendio
        } else if (!mapa.getNodosIncidentes().isEmpty()) {
            Point2D posicionIncendio = mapa.getNodosIncidentes().get(random.nextInt(mapa.getNodosIncidentes().size()));
            incidente = new Incendio("I" + (++contadorIncidentes), posicionIncendio, "estructural", random.nextInt(5) + 1);

            // Si no se genera ni incendio ni robo, no hace nada
        } else {
            return;
        }

        // Agregar el incidente al sistema
        agregarIncidente(incidente);
    }


    private void verificarAccidentes() {
        if (!simulacionActiva) {
            return;
        }

        // Verificar violaciones de semáforo
        verificarAccidentesPorSemaforo();

        // Verificar congestión
        verificarAccidentesPorCongestion();

        // Verificar exceso de velocidad
        verificarAccidentesPorVelocidad();
    }


    private void verificarAccidentesPorSemaforo() {
        Random random = new Random();

        for (Semaforo semaforo : mapa.getSemaforos()) {
            if (semaforo.isRojo()) {
                // Verificar si hay vehículos cerca del semáforo
                List<Vehiculo> vehiculosCerca = new ArrayList<>();

                for (Vehiculo vehiculo : vehiculos) {
                    if (vehiculo.getPosicion().distance(semaforo.getPosicion()) < 30) {
                        vehiculosCerca.add(vehiculo);
                    }
                }

                // Si hay múltiples vehículos cerca y uno no respeta el semáforo
                if (vehiculosCerca.size() >= 2 && random.nextDouble() < 0.1) {
                    Vehiculo vehiculoInfractor = vehiculosCerca.get(random.nextInt(vehiculosCerca.size()));

                    if (!vehiculoInfractor.debeDetenerse(semaforo)) {
                        // Crear accidente por violación de semáforo
                        Accidente accidente = new Accidente(
                                "AC" + (++contadorIncidentes),
                                semaforo.getPosicion(),
                                true, false, false
                        );

                        accidente.agregarVehiculoInvolucrado(vehiculoInfractor);
                        semaforo.registrarViolacion();

                        agregarIncidente(accidente);
                    } else {
                        semaforo.registrarRespeto();
                    }
                }
            }
        }
    }


    private void verificarAccidentesPorCongestion() {
        Random random = new Random();
        List<Point2D> posicionesVehiculos = new ArrayList<>();

        for (Vehiculo vehiculo : vehiculos) {
            posicionesVehiculos.add(vehiculo.getPosicion());
        }

        for (MapNodo nodo : mapa.getGrafo().getNodos()) {
            if (mapa.getGrafo().hayCongestión(nodo, posicionesVehiculos, 4)) {
                if (random.nextDouble() < 0.05) {
                    Accidente accidente = new Accidente(
                            "AC" + (++contadorIncidentes),
                            nodo.getPosicion(),
                            false, false, true
                    );

                    agregarIncidente(accidente);
                }
            }
        }
    }


    private void verificarAccidentesPorVelocidad() {
        Random random = new Random();

        for (Vehiculo vehiculo : vehiculos) {
            // Verificar si el vehículo está en una zona con límite de velocidad
            if (mapa.puntoEnZona(vehiculo.getPosicion(), "residencial")) {
                double limiteVelocidad = 2.0; // Límite en zona residencial

                if (vehiculo.getVelocidad() > limiteVelocidad && random.nextDouble() < 0.03) {
                    Accidente accidente = new Accidente(
                            "AC" + (++contadorIncidentes),
                            vehiculo.getPosicion(),
                            false, true, false
                    );

                    accidente.agregarVehiculoInvolucrado(vehiculo);
                    agregarIncidente(accidente);
                }
            }
        }
    }


    public void agregarIncidente(Incidente incidente) {
        incidentesActivos.add(incidente);
        bstIncidentes.insert(incidente);


        notificarObservadores(obs -> obs.onIncidenteCreado(incidente));
    }


public List<VehiculoCivil> getVehiculosCiviles() {
        return new ArrayList<>(vehiculosCiviles);
    }

    public void procesarIncidentesAtendidos() {
        try {
            // Crear una lista temporal de incidentes a eliminar
            List<Incidente> incidentesAEliminar = new ArrayList<>();

            // Identificar incidentes atendidos
            for (Incidente incidente : incidentesActivos) {
                if (incidente.isAtendido()) {
                    incidentesAEliminar.add(incidente);
                }
            }

            // Eliminar incidentes atendidos de la lista principal
            for (Incidente incidente : incidentesAEliminar) {
                incidentesActivos.remove(incidente);

                // Notificar a observadores
                notificarObservadores(obs -> {
                    try {
                        obs.onIncidenteAtendido(incidente);
                    } catch (Exception e) {
                        System.err.println("Error notificando incidente atendido: " + e.getMessage());
                    }
                });
            }

            // Limpiar la lista temporal
            incidentesAEliminar.clear();

        } catch (Exception e) {
            System.err.println("Error procesando incidentes atendidos: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void agregarObservador(Observer observer) {
        observadores.add(observer);
    }


    public void removerObservador(Observer observer) {
        observadores.remove(observer);
    }


    private void notificarObservadores(java.util.function.Consumer<Observer> accion) {
        for (Observer observer : observadores) {
            try {
                accion.accept(observer);
            } catch (Exception e) {
                System.err.println("Error notificando observador: " + e.getMessage());
            }
        }
    }


    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();

        // Contadores de vehículos
        long patrullas = vehiculos.stream().filter(v -> v instanceof Patrulla).count();
        long ambulancias = vehiculos.stream().filter(v -> v instanceof Ambulancia).count();
        long bomberos = vehiculos.stream().filter(v -> v instanceof Bombero).count();

        estadisticas.put("patrullas", patrullas);
        estadisticas.put("ambulancias", ambulancias);
        estadisticas.put("bomberos", bomberos);
        estadisticas.put("vehiculos_disponibles", vehiculos.stream().filter(Vehiculo::isDisponible).count());

        // Contadores de incidentes
        long accidentes = incidentesActivos.stream().filter(i -> i instanceof Accidente).count();
        long robos = incidentesActivos.stream().filter(i -> i instanceof Robo).count();
        long incendios = incidentesActivos.stream().filter(i -> i instanceof Incendio).count();

        estadisticas.put("accidentes", accidentes);
        estadisticas.put("robos", robos);
        estadisticas.put("incendios", incendios);
        estadisticas.put("incidentes_totales", incidentesActivos.size());

        // Estadísticas de semáforos
        int totalRespeto = 0;
        int totalViolaciones = 0;

        for (Semaforo semaforo : mapa.getSemaforos()) {
            totalRespeto += semaforo.getVehiculosRespetaron();
            totalViolaciones += semaforo.getVehiculosViolaron();
        }

        estadisticas.put("semaforos_respetados", totalRespeto);
        estadisticas.put("semaforos_violados", totalViolaciones);

        // Puntuación y tiempo
        estadisticas.put("puntuacion", gestorPuntuacion.getPuntuacionTotal());
        estadisticas.put("tiempo_simulacion", (System.currentTimeMillis() - tiempoInicio) / 1000);

        return estadisticas;
    }

    public Vehiculo getVehiculoQueAtendio(Incidente incidente) {
        for (Vehiculo v : vehiculos) {
            if (v.getIncidenteAsignado() == incidente) {
                return v;
            }
        }
        return null;
    }


    public void crearYAsignarVehiculoAIncidente(Incidente incidente) {
        // Usar el método privado existente
        //asignarVehiculoAIncidente(incidente);
    }

    public void notificarVehiculoDisponible(Vehiculo vehiculo) {
        try {
            vehiculo.setDisponible(true);
            if (!vehiculosDisponibles.contains(vehiculo)) {
                vehiculosDisponibles.add(vehiculo);
            }


            // Añadir el vehículo a la lista de vehículos disponibles
            if (!vehiculos.contains(vehiculo)) { // Verificar que no esté ya en la lista
                vehiculos.add(vehiculo);
            }

            // Notificar a los observadores que el vehículo está disponible
            for (Observer obs : observadores) {
                try {

                    obs.onVehiculoDisponible(vehiculo);
                } catch (Exception e) {
                    System.err.println("Error notificando vehículo disponible a un observador: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error notificando vehículo disponible: " + e.getMessage());
        }
    }

    // Añadir en SimuladorSGMMS - método para procesar vehículos que terminaron su trabajo
    public void procesarVehiculosQueTerminaron() {
        try {
            for (Vehiculo vehiculo : vehiculos) {
                if (vehiculo.getIncidenteAsignado() != null &&
                        vehiculo.getIncidenteAsignado().isAtendido() &&
                        !vehiculo.isDisponible()) {

                    // Liberar vehículo si su incidente ya fue atendido
                    vehiculo.setDisponible(true);
                    vehiculo.setIncidenteAsignado(null);
                    notificarVehiculoDisponible(vehiculo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando vehículos que terminaron: " + e.getMessage());
        }
    }






    // Getters

    public List<Vehiculo> getVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    public List<Incidente> getIncidentesActivos() {
        return new ArrayList<>(incidentesActivos);
    }

    public Mapa getMapa() {
        return mapa;
    }

    public GestorPuntuacion getGestorPuntuacion() {
        return gestorPuntuacion;
    }

    public boolean isSimulacionActiva() {
        return simulacionActiva;
    }

    public BST<Incidente> getBstIncidentes() {
        return bstIncidentes;
    }

    public BST<Ruta> getBstRutas() {
        return bstRutas;
    }

    public List<Vehiculo> getVehiculosDisponibles() {
        return vehiculosDisponibles;
    }
}