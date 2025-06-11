package org.example.tareaintegradora2apo.model.map;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.example.tareaintegradora2apo.model.trafico.Semaforo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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
    private List<Point2D> nodosIncidentes;

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
        this.nodosIncidentes = new ArrayList<>();

        // Inicializar zonas
        zonas.put("residencial", new ArrayList<>());
        zonas.put("comercial", new ArrayList<>());
        zonas.put("vias_principales", new ArrayList<>());
    }


    /**
     * Inicializa el grafo del mapa basado en la imagen proporcionada de Palmira
     */
    public void inicializarGrafoPalmira() throws IOException {
        // Limpiar datos existentes
        grafo = new Grafo();
        semaforos.clear();
        puntosEntrada.clear();
        puntosSalida.clear();
        cargarNodos("map/nodos.csv");
        cargarAristas("map/aristas.csv");
        // Crear semáforos en intersecciones principales (evitando edificios)
        //crearSemaforosEnIntersecciones();

        // Definir edificios de servicio
        definirEdificiosServicio();


    }

    public void cargarNodos(String pathNodos) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+pathNodos)))) {
            String linea = reader.readLine(); // Encabezado
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();//la limpie para quitar espaciones en blanco

                if (linea.isEmpty()) {
                    continue; // Saltar líneas vacías
                }

                String[] partes = linea.split(";");
                // Verificar que la línea tenga el número correcto de partes
                if (partes.length < 6) {
                    System.out.println("Advertencia: Línea con datos incompletos: " + linea);
                    continue; // Si la línea está incompleta, la saltamos
                }


                String id = partes[0].trim();
                double x = Double.parseDouble(partes[1].trim());
                double y = Double.parseDouble(partes[2].trim());
                boolean tieneSemaforo = Boolean.parseBoolean(partes[3].trim());
                boolean puntosClave = Boolean.parseBoolean(partes[4].trim());
                boolean esEdificio = Boolean.parseBoolean(partes[5].trim());
                if (puntosClave){
                    puntosEntrada.add(new Point2D(x, y));
                    puntosSalida.add(new Point2D(x, y));
                }
                //este metodo lo que hace es coger el nodo recien añadido y si es un edificio lo añade a la lista
               // MapNodo nodo = grafo.agregarNodo(id,new Point2D(x,y),tieneSemaforo);
                grafo.agregarNodo(id, new Point2D(x, y), tieneSemaforo);
                if (esEdificio){
                    nodosIncidentes.add(new Point2D(x, y));
                }

                if (tieneSemaforo) {

                    Semaforo.Patron  patron = determinarPatronSemaforoAleatorio();
                    Semaforo semaforo = new Semaforo("S" + id, new Point2D(x, y), patron);  // O el patrón que prefieras
                    semaforos.add(semaforo);
                    semaforo.iniciar();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public void cargarAristas(String pathAristas) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+pathAristas)))) {
            String linea = reader.readLine(); // Encabezado
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                String id = partes[0].trim();
                String idOrigen = partes[1].trim();
                String idDestino = partes[2].trim();
                double peso = Double.parseDouble(partes[3].trim());
                grafo.agregarArista(id, idOrigen, idDestino, peso);

            }
        }catch (IOException e){
            e.printStackTrace();
        }
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
    private Semaforo.Patron determinarPatronSemaforoAleatorio() {
        // Obtener todos los valores del enum Patron
        Semaforo.Patron[] patrones = Semaforo.Patron.values();

        // Seleccionar un valor aleatorio
        Random random = new Random();
        return patrones[random.nextInt(patrones.length)];  // Selecciona aleatoriamente un patrón
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

    public List<Point2D> getNodosIncidentes() {
        return nodosIncidentes;
    }
}