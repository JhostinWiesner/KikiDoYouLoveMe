package org.example.tareaintegradora2apo.model.map;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa el mapa de la ciudad en el sistema SGMMS.
 */
public class Mapa {

    private Image imagenMapa;
    private Grafo grafo;
    private List<Semaforo> semaforos;
    private Map<String, List<Point2D>> zonas; // Residencial, comercial, vías principales
    private List<Point2D> puntosEntrada;
    private List<Point2D> puntosSalida;
    private Map<String, Point2D> edificiosServicio;
    /**
     * Constructor para la clase Mapa
     *
     * @param rutaImagen Ruta de la imagen del mapa
     */
    public Mapa(String rutaImagen) {
        this.imagenMapa = new Image(getClass().getResource("/images/palmira_map.jpg").toExternalForm());
        //this.imagenMapa = null;
        this.grafo = new Grafo();
        this.semaforos = new ArrayList<>();
        this.zonas = new HashMap<>();
        this.puntosEntrada = new ArrayList<>();
        this.puntosSalida = new ArrayList<>();
        this.edificiosServicio = new HashMap<>();

        // Inicializar zonas
        zonas.put("residencial", new ArrayList<>());
        zonas.put("comercial", new ArrayList<>());
        zonas.put("vias_principales", new ArrayList<>());
    }

    /**
     * Inicializa el grafo del mapa con nodos y aristas
     */
    public void inicializarGrafo() {
        // Este método se implementará con las coordenadas específicas del mapa de Palmira
        // Aquí se definirán los nodos (intersecciones) y aristas (calles) del grafo

        // Ejemplo de inicialización básica
        // Crear nodos en intersecciones clave
        MapNodo n1 = grafo.agregarNodo("n1", new Point2D(100, 100));
        MapNodo n2 = grafo.agregarNodo("n2", new Point2D(200, 100));
        MapNodo n3 = grafo.agregarNodo("n3", new Point2D(100, 200));
        MapNodo n4 = grafo.agregarNodo("n4", new Point2D(200, 200));

        // Crear aristas (calles) entre nodos
        grafo.agregarArista("a1", "n1", "n2", 100, "principal", 60);
        grafo.agregarArista("a2", "n1", "n3", 100, "secundaria", 40);
        grafo.agregarArista("a3", "n2", "n4", 100, "principal", 60);
        grafo.agregarArista("a4", "n3", "n4", 100, "secundaria", 40);

        // Marcar nodos con semáforos
        n1.setTieneSemaforo(true);
        n4.setTieneSemaforo(true);

        // Agregar semáforos en intersecciones clave
        semaforos.add(new Semaforo("s1", n1.getPosicion()));
        semaforos.add(new Semaforo("s2", n4.getPosicion()));

        // Definir puntos de entrada y salida
        puntosEntrada.add(new Point2D(50, 100));
        puntosEntrada.add(new Point2D(100, 50));
        puntosSalida.add(new Point2D(250, 200));
        puntosSalida.add(new Point2D(200, 250));

        // Definir zonas
        List<Point2D> zonaResidencial = zonas.get("residencial");
        zonaResidencial.add(new Point2D(50, 50));
        zonaResidencial.add(new Point2D(150, 50));
        zonaResidencial.add(new Point2D(150, 150));
        zonaResidencial.add(new Point2D(50, 150));

        List<Point2D> zonaComercial = zonas.get("comercial");
        zonaComercial.add(new Point2D(150, 150));
        zonaComercial.add(new Point2D(250, 150));
        zonaComercial.add(new Point2D(250, 250));
        zonaComercial.add(new Point2D(150, 250));
    }

    /**
     * Inicializa el grafo del mapa basado en la imagen proporcionada de Palmira
     */
    public void inicializarGrafoPalmira() {
        // Limpiar datos existentes
        grafo = new Grafo();
        semaforos.clear();
        puntosEntrada.clear();
        puntosSalida.clear();

        // Inicializar zonas si no existen
        if (!zonas.containsKey("residencial")) {
            zonas.put("residencial", new ArrayList<>());
        }
        if (!zonas.containsKey("comercial")) {
            zonas.put("comercial", new ArrayList<>());
        }
        if (!zonas.containsKey("vias_principales")) {
            zonas.put("vias_principales", new ArrayList<>());
        }
        if (!zonas.containsKey("industrial")) {
            zonas.put("industrial", new ArrayList<>());
        }

        // Crear nodos en intersecciones principales (grid 10x10)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double x = 50 + i * 100;
                double y = 50 + j * 100;
                grafo.agregarNodo("n_" + i + "_" + j, new Point2D(x, y));
            }
        }

        // Crear aristas (calles) entre nodos adyacentes
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // Aristas horizontales
                if (i < 9) {
                    String tipoVia = (i % 3 == 0) ? "principal" : "secundaria";
                    double velocidad = (i % 3 == 0) ? 60 : 40;
                    grafo.agregarArista("a_h_" + i + "_" + j, "n_" + i + "_" + j, "n_" + (i + 1) + "_" + j, 100, tipoVia, velocidad);
                }

                // Aristas verticales
                if (j < 9) {
                    String tipoVia = (j % 3 == 0) ? "principal" : "secundaria";
                    double velocidad = (j % 3 == 0) ? 60 : 40;
                    grafo.agregarArista("a_v_" + i + "_" + j, "n_" + i + "_" + j, "n_" + i + "_" + (j + 1), 100, tipoVia, velocidad);
                }
            }
        }

        // Definir puntos de entrada y salida
        puntosEntrada.add(new Point2D(0, 150));    // Entrada oeste
        puntosEntrada.add(new Point2D(150, 0));    // Entrada norte
        puntosEntrada.add(new Point2D(950, 150));  // Entrada este
        puntosEntrada.add(new Point2D(150, 950));  // Entrada sur

        puntosSalida.add(new Point2D(0, 850));     // Salida oeste
        puntosSalida.add(new Point2D(850, 0));     // Salida norte
        puntosSalida.add(new Point2D(950, 850));   // Salida este
        puntosSalida.add(new Point2D(850, 950));   // Salida sur

        // Definir zonas

        // Zona residencial (esquina superior izquierda)
        List<Point2D> zonaResidencial = zonas.get("residencial");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                zonaResidencial.add(new Point2D(100 + i * 100, 100 + j * 100));
            }
        }

        // Zona comercial (centro del mapa)
        List<Point2D> zonaComercial = zonas.get("comercial");
        for (int i = 4; i < 7; i++) {
            for (int j = 4; j < 7; j++) {
                zonaComercial.add(new Point2D(50 + i * 100, 50 + j * 100));
            }
        }

        // Zona industrial (esquina inferior derecha)
        List<Point2D> zonaIndustrial = zonas.get("industrial");
        for (int i = 7; i < 10; i++) {
            for (int j = 7; j < 10; j++) {
                zonaIndustrial.add(new Point2D(50 + i * 100, 50 + j * 100));
            }
        }

        // Vías principales (cada 3 líneas)
        List<Point2D> viasPrincipales = zonas.get("vias_principales");
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                // Vías horizontales principales
                for (int j = 0; j < 10; j++) {
                    viasPrincipales.add(new Point2D(50 + i * 100, 50 + j * 100));
                }
            }
        }
        for (int j = 0; j < 10; j++) {
            if (j % 3 == 0) {
                // Vías verticales principales
                for (int i = 0; i < 10; i++) {
                    viasPrincipales.add(new Point2D(50 + i * 100, 50 + j * 100));
                }
            }
        }

        // Crear semáforos en intersecciones principales (evitando edificios)
        crearSemaforosEnIntersecciones();

        // Definir edificios de servicio
        definirEdificiosServicio();

        System.out.println("Mapa de Palmira inicializado:");
        System.out.println("- Nodos: " + grafo.getNodos().size());
        System.out.println("- Aristas: " + grafo.getAristas().size());
        System.out.println("- Semáforos: " + semaforos.size());
        System.out.println("- Puntos de entrada: " + puntosEntrada.size());
        System.out.println("- Puntos de salida: " + puntosSalida.size());
    }

    /**
     * Crea semáforos en intersecciones principales, evitando edificios
     */
    private void crearSemaforosEnIntersecciones() {
        int contadorSemaforos = 1;

        // Crear semáforos en intersecciones de vías principales
        for (int i = 0; i < 10; i += 3) {
            for (int j = 0; j < 10; j += 3) {
                Point2D posicion = new Point2D(50 + i * 100, 50 + j * 100);

                // Verificar que no esté en zona de edificios
                if (!estaEnZonaEdificios(posicion)) {
                    // Determinar patrón según ubicación
                    Semaforo.Patron patron = determinarPatronSemaforo(i, j);

                    Semaforo semaforo = new Semaforo("S" + contadorSemaforos++, posicion, patron);
                    semaforos.add(semaforo);
                    semaforo.iniciar();
                }
            }
        }

        // Agregar algunos semáforos adicionales en puntos estratégicos
        agregarSemaforosEstrategicos(contadorSemaforos);
    }

    /**
     * Verifica si una posición está en zona de edificios
     */
    private boolean estaEnZonaEdificios(Point2D posicion) {
        // Definir áreas donde están los edificios de servicio
        // Hospital (zona comercial central)
        if (posicion.getX() >= 400 && posicion.getX() <= 600 &&
                posicion.getY() >= 400 && posicion.getY() <= 600) {
            return true;
        }

        // Estación de policía (zona residencial)
        if (posicion.getX() >= 100 && posicion.getX() <= 300 &&
                posicion.getY() >= 100 && posicion.getY() <= 300) {
            return true;
        }

        // Estación de bomberos (zona industrial)
        if (posicion.getX() >= 700 && posicion.getX() <= 900 &&
                posicion.getY() >= 700 && posicion.getY() <= 900) {
            return true;
        }

        return false;
    }

    /**
     * Determina el patrón de semáforo según la ubicación
     */
    private Semaforo.Patron determinarPatronSemaforo(int i, int j) {
        // Intersecciones centrales: patrón normal
        if (i >= 3 && i <= 6 && j >= 3 && j <= 6) {
            return Semaforo.Patron.NORMAL;
        }

        // Intersecciones en vías principales externas: patrón rápido
        if (i == 0 || i == 9 || j == 0 || j == 9) {
            return Semaforo.Patron.RAPIDO;
        }

        // Intersecciones en zonas residenciales: patrón lento
        if ((i <= 3 && j <= 3) || (i >= 6 && j >= 6)) {
            return Semaforo.Patron.LENTO;
        }

        return Semaforo.Patron.NORMAL;
    }

    /**
     * Agrega semáforos en puntos estratégicos
     */
    private void agregarSemaforosEstrategicos(int contadorInicial) {
        int contador = contadorInicial;

        // Semáforos cerca de puntos de entrada/salida
        Point2D[] posicionesEstrategicas = {
                new Point2D(150, 150),  // Cerca de entrada norte-oeste
                new Point2D(850, 150),  // Cerca de entrada norte-este
                new Point2D(150, 850),  // Cerca de entrada sur-oeste
                new Point2D(850, 850),  // Cerca de entrada sur-este
                new Point2D(450, 250),  // Acceso a zona comercial norte
                new Point2D(550, 750),  // Acceso a zona comercial sur
        };

        for (Point2D pos : posicionesEstrategicas) {
            if (!estaEnZonaEdificios(pos)) {
                Semaforo semaforo = new Semaforo("S" + contador++, pos, Semaforo.Patron.NORMAL);
                semaforos.add(semaforo);
                semaforo.iniciar();
            }
        }
    }

    /**
     * Define las ubicaciones de los edificios de servicio
     */
    private void definirEdificiosServicio() {
        // Hospital en zona comercial central
        edificiosServicio.put("hospital", new Point2D(500, 500));

        // Estación de policía en zona residencial
        edificiosServicio.put("estacion_policia", new Point2D(200, 200));

        // Estación de bomberos en zona industrial
        edificiosServicio.put("estacion_bomberos", new Point2D(800, 800));

        // Edificios adicionales
        edificiosServicio.put("municipalidad", new Point2D(450, 450));
        edificiosServicio.put("centro_emergencias", new Point2D(550, 550));

        System.out.println("Edificios de servicio definidos:");
        for (Map.Entry<String, Point2D> entry : edificiosServicio.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " +
                    String.format("(%.0f, %.0f)", entry.getValue().getX(), entry.getValue().getY()));
        }
    }

    /**
     * Obtiene un punto de entrada aleatorio
     */
    public Point2D obtenerPuntoEntradaAleatorio() {
        if (puntosEntrada.isEmpty()) {
            return new Point2D(50, 50); // Punto por defecto
        }
        int indice = (int) (Math.random() * puntosEntrada.size());
        return puntosEntrada.get(indice);
    }

    /**
     * Obtiene un punto de salida aleatorio
     */
    public Point2D obtenerPuntoSalidaAleatorio() {
        if (puntosSalida.isEmpty()) {
            return new Point2D(950, 950); // Punto por defecto
        }
        int indice = (int) (Math.random() * puntosSalida.size());
        return puntosSalida.get(indice);
    }

    /**
     * Obtiene una posición aleatoria en una zona específica
     */
    public Point2D obtenerPosicionEnZona(String nombreZona) {
        List<Point2D> zona = zonas.get(nombreZona);
        if (zona == null || zona.isEmpty()) {
            return new Point2D(500, 500); // Centro del mapa por defecto
        }

        int indice = (int) (Math.random() * zona.size());
        Point2D puntoBase = zona.get(indice);

        // Agregar algo de variación aleatoria
        double offsetX = (Math.random() - 0.5) * 80; // ±40 píxeles
        double offsetY = (Math.random() - 0.5) * 80;

        return new Point2D(
                Math.max(0, Math.min(1000, puntoBase.getX() + offsetX)),
                Math.max(0, Math.min(1000, puntoBase.getY() + offsetY))
        );
    }

    /**
     * Obtiene un punto de entrada aleatorio para generar vehículos
     *
     * @return Punto de entrada aleatorio
     */
    public Point2D getPuntoEntradaAleatorio() {
        int indice = (int) (Math.random() * puntosEntrada.size());
        return puntosEntrada.get(indice);
    }

    /**
     * Obtiene un punto aleatorio en una zona específica
     *
     * @param tipoZona Tipo de zona (residencial, comercial, vias_principales)
     * @return Punto aleatorio en la zona
     */
    public Point2D getPuntoAleatorioEnZona(String tipoZona) {
        List<Point2D> zona = zonas.get(tipoZona);
        if (zona == null || zona.isEmpty()) {
            return new Point2D(0, 0);
        }

        int indice = (int) (Math.random() * zona.size());
        return zona.get(indice);
    }

    /**
     * Verifica si un punto está dentro de una zona específica
     *
     * @param punto    Punto a verificar
     * @param tipoZona Tipo de zona
     * @return true si el punto está en la zona, false en caso contrario
     */
    public boolean puntoEnZona(Point2D punto, String tipoZona) {
        List<Point2D> zona = zonas.get(tipoZona);
        if (zona == null || zona.isEmpty()) {
            return false;
        }

        // Implementación simple: verificar si el punto está cerca de algún punto de la zona
        for (Point2D p : zona) {
            if (p.distance(punto) < 50) {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtiene el mapa de edificios de servicio
     * @return Mapa con las ubicaciones de los edificios de servicio
     */
    public Map<String, Point2D> getEdificiosServicio() {
        return edificiosServicio;
    }

    // Getters

    public Image getImagenMapa() {
        return imagenMapa;
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public List<Semaforo> getSemaforos() {
        return semaforos;
    }

    public Map<String, List<Point2D>> getZonas() {
        return zonas;
    }

    public List<Point2D> getPuntosEntrada() {
        return puntosEntrada;
    }

    public List<Point2D> getPuntosSalida() {
        return puntosSalida;
    }
}