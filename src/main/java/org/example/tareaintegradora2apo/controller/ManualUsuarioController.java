package org.example.tareaintegradora2apo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista del Manual del Usuario.
 * Muestra información detallada sobre cómo usar el sistema SGMMS.
 */
public class ManualUsuarioController implements Initializable {

    @FXML private TextArea areaContenido;
    @FXML private TreeView<String> arbolSecciones;
    @FXML private Button btnCerrar;

    private SimuladorSGMMS simulador;
    private Stage primaryStage;

    /**
     * Establece el simulador y la ventana principal
     */
    public void setPrimaryStage(Stage primaryStage, SimuladorSGMMS simulador) {
        this.primaryStage = primaryStage;
        this.simulador = simulador;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarArbolSecciones();
        configurarEventos();
        mostrarSeccionInicial();
    }

    /**
     * Configura el árbol de secciones del manual
     */
    private void configurarArbolSecciones() {
        TreeItem<String> raiz = new TreeItem<>("Manual del Usuario SGMMS");
        raiz.setExpanded(true);

        // Sección 1: Introducción
        TreeItem<String> introduccion = new TreeItem<>("1. Introducción");
        introduccion.getChildren().addAll(
                new TreeItem<>("1.1 ¿Qué es SGMMS?"),
                new TreeItem<>("1.2 Objetivos del Sistema"),
                new TreeItem<>("1.3 Características Principales")
        );

        // Sección 2: Interfaz Principal
        TreeItem<String> interfaz = new TreeItem<>("2. Interfaz Principal");
        interfaz.getChildren().addAll(
                new TreeItem<>("2.1 Menú Principal"),
                new TreeItem<>("2.2 Navegación entre Módulos"),
                new TreeItem<>("2.3 Controles Básicos")
        );

        // Sección 3: Centro de Monitoreo
        TreeItem<String> centro = new TreeItem<>("3. Centro de Monitoreo");
        centro.getChildren().addAll(
                new TreeItem<>("3.1 Panel de Estadísticas"),
                new TreeItem<>("3.2 Gráficos en Tiempo Real"),
                new TreeItem<>("3.3 Alertas y Notificaciones")
        );

        // Sección 4: Mapa de Tráfico
        TreeItem<String> mapa = new TreeItem<>("4. Mapa de Tráfico");
        mapa.getChildren().addAll(
                new TreeItem<>("4.1 Navegación en el Mapa"),
                new TreeItem<>("4.2 Vehículos y Semáforos"),
                new TreeItem<>("4.3 Controles de Cámara")
        );

        // Sección 5: Panel de Incidentes
        TreeItem<String> incidentes = new TreeItem<>("5. Panel de Incidentes");
        incidentes.getChildren().addAll(
                new TreeItem<>("5.1 Lista de Incidentes"),
                new TreeItem<>("5.2 Asignación de Vehículos"),
                new TreeItem<>("5.3 Gestión de Emergencias")
        );

        // Sección 6: Solución de Problemas
        TreeItem<String> problemas = new TreeItem<>("6. Solución de Problemas");
        problemas.getChildren().addAll(
                new TreeItem<>("6.1 Problemas Comunes"),
                new TreeItem<>("6.2 Mensajes de Error"),
                new TreeItem<>("6.3 Contacto de Soporte")
        );

        raiz.getChildren().addAll(introduccion, interfaz, centro, mapa, incidentes, problemas);
        arbolSecciones.setRoot(raiz);
    }

    /**
     * Configura los eventos de la interfaz
     */
    private void configurarEventos() {
        // Evento de selección en el árbol
        arbolSecciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mostrarContenidoSeccion(newSelection.getValue());
                    }
                });

        // Evento del botón cerrar
        btnCerrar.setOnAction(e -> cerrarVentana());
    }

    /**
     * Muestra la sección inicial del manual
     */
    private void mostrarSeccionInicial() {
        mostrarContenidoSeccion("Manual del Usuario SGMMS");
    }

    /**
     * Muestra el contenido de una sección específica
     * @param seccion Nombre de la sección
     */
    private void mostrarContenidoSeccion(String seccion) {
        String contenido = obtenerContenidoSeccion(seccion);
        areaContenido.setText(contenido);
    }

    /**
     * Obtiene el contenido de una sección específica
     * @param seccion Nombre de la sección
     * @return Contenido de la sección
     */
    private String obtenerContenidoSeccion(String seccion) {
        switch (seccion) {
            case "Manual del Usuario SGMMS":
                return """
                    ═══════════════════════════════════════════════════════════════
                                    MANUAL DEL USUARIO - SGMMS
                           Sistema de Gestión y Monitoreo de Movilidad y Seguridad
                    ═══════════════════════════════════════════════════════════════
                    
                    Bienvenido al Sistema de Gestión y Monitoreo de Movilidad y Seguridad (SGMMS).
                    
                    Este manual le ayudará a comprender y utilizar todas las funcionalidades
                    del sistema de manera eficiente.
                    
                    Versión: 2.0
                    Ciudad: Palmira
                    Desarrollado para: Gestión de tráfico y emergencias urbanas
                    
                    Seleccione una sección del menú lateral para obtener información detallada.
                    """;

            case "1. Introducción":
            case "1.1 ¿Qué es SGMMS?":
                return """
                    ¿QUÉ ES SGMMS?
                    
                    El Sistema de Gestión y Monitoreo de Movilidad y Seguridad (SGMMS) es una
                    aplicación integral diseñada para simular y gestionar el tráfico urbano y
                    los servicios de emergencia en la ciudad de Palmira.
                    
                    CARACTERÍSTICAS PRINCIPALES:
                    • Simulación en tiempo real del tráfico vehicular
                    • Gestión de incidentes y emergencias
                    • Monitoreo de vehículos de servicio (policía, bomberos, ambulancias)
                    • Control de semáforos inteligentes
                    • Sistema de puntuación y estadísticas
                    • Interfaz gráfica intuitiva
                    
                    BENEFICIOS:
                    • Mejora la eficiencia en la respuesta a emergencias
                    • Optimiza el flujo de tráfico
                    • Proporciona datos estadísticos valiosos
                    • Facilita la toma de decisiones
                    """;

            case "1.2 Objetivos del Sistema":
                return """
                    OBJETIVOS DEL SISTEMA SGMMS
                    
                    OBJETIVO PRINCIPAL:
                    Proporcionar una herramienta integral para la gestión eficiente del tráfico
                    urbano y la coordinación de servicios de emergencia.
                    
                    OBJETIVOS ESPECÍFICOS:
                    
                    1. GESTIÓN DE TRÁFICO:
                       • Monitorear el flujo vehicular en tiempo real
                       • Controlar semáforos de manera inteligente
                       • Identificar y resolver congestiones
                    
                    2. GESTIÓN DE EMERGENCIAS:
                       • Detectar incidentes automáticamente
                       • Asignar vehículos de emergencia eficientemente
                       • Minimizar tiempos de respuesta
                    
                    3. ANÁLISIS Y ESTADÍSTICAS:
                       • Generar reportes de rendimiento
                       • Analizar patrones de tráfico
                       • Evaluar efectividad de respuestas
                    
                    4. INTERFAZ USUARIO:
                       • Proporcionar visualización clara y comprensible
                       • Facilitar la interacción del operador
                       • Mostrar información relevante en tiempo real
                    """;

            case "2. Interfaz Principal":
            case "2.1 Menú Principal":
                return """
                    MENÚ PRINCIPAL
                    
                    El menú principal es el punto de entrada al sistema SGMMS. Desde aquí
                    puede acceder a todos los módulos del sistema.
                    
                    OPCIONES DISPONIBLES:
                    
                    📊 CENTRO DE MONITOREO
                    • Acceso al panel principal de estadísticas
                    • Visualización de gráficos en tiempo real
                    • Resumen general del sistema
                    
                    🗺️ MAPA DE TRÁFICO
                    • Vista del mapa interactivo de la ciudad
                    • Visualización de vehículos y semáforos
                    • Control de cámara y navegación
                    
                    🚨 PANEL DE INCIDENTES
                    • Gestión de emergencias activas
                    • Asignación de vehículos de servicio
                    • Seguimiento de incidentes
                    
                    📖 MANUAL DEL USUARIO
                    • Documentación completa del sistema
                    • Guías de uso paso a paso
                    • Solución de problemas comunes
                    
                    🚪 SALIR
                    • Cierra la aplicación de manera segura
                    • Guarda el estado actual del sistema
                    """;

            case "3. Centro de Monitoreo":
            case "3.1 Panel de Estadísticas":
                return """
                    CENTRO DE MONITOREO - PANEL DE ESTADÍSTICAS
                    
                    El Centro de Monitoreo proporciona una vista general del estado del sistema
                    con estadísticas en tiempo real y gráficos informativos.
                    
                    SECCIONES PRINCIPALES:
                    
                    1. ESTADÍSTICAS GENERALES:
                       • Total de vehículos activos
                       • Incidentes pendientes y atendidos
                       • Puntuación actual del sistema
                       • Tiempo promedio de respuesta
                    
                    2. GRÁFICOS EN TIEMPO REAL:
                       • Gráfico de incidentes por hora
                       • Distribución de tipos de incidentes
                       • Rendimiento de vehículos de servicio
                       • Eficiencia de semáforos
                    
                    3. ALERTAS Y NOTIFICACIONES:
                       • Incidentes de alta prioridad
                       • Vehículos fuera de servicio
                       • Congestiones de tráfico
                       • Alertas del sistema
                    
                    CONTROLES DISPONIBLES:
                    • Botón de actualización manual
                    • Filtros de tiempo para gráficos
                    • Exportación de datos
                    • Configuración de alertas
                    """;

            case "4. Mapa de Tráfico":
            case "4.1 Navegación en el Mapa":
                return """
                    MAPA DE TRÁFICO - NAVEGACIÓN
                    
                    El mapa de tráfico muestra una vista en tiempo real de la ciudad con todos
                    los elementos del sistema visibles e interactivos.
                    
                    CONTROLES DE NAVEGACIÓN:
                    
                    TECLADO:
                    • W, A, S, D: Mover la cámara
                    • Flechas: Navegación alternativa
                    • + / -: Zoom in/out
                    • Espacio: Centrar vista
                    
                    MOUSE:
                    • Clic izquierdo: Seleccionar elementos
                    • Rueda: Zoom
                    • Arrastrar: Mover vista (si está habilitado)
                    
                    ELEMENTOS VISIBLES:
                    
                    🚔 VEHÍCULOS DE SERVICIO:
                    • Patrullas (azul)
                    • Ambulancias (blanco/rojo)
                    • Bomberos (rojo)
                    
                    🚗 VEHÍCULOS CIVILES:
                    • Sedanes (azul claro)
                    • SUVs (verde claro)
                    • Camiones (marrón)
                    • Motocicletas (amarillo)
                    
                    🚦 SEMÁFOROS:
                    • Verde: Paso libre
                    • Amarillo: Precaución
                    • Rojo: Alto
                    
                    ⚠️ INCIDENTES:
                    • Accidentes (triángulo amarillo)
                    • Robos (símbolo de dinero)
                    • Incendios (llama roja)
                    """;

            case "5. Panel de Incidentes":
            case "5.1 Lista de Incidentes":
                return """
                    PANEL DE INCIDENTES - GESTIÓN
                    
                    El Panel de Incidentes permite gestionar todas las emergencias activas
                    en el sistema de manera eficiente.
                    
                    TABLA DE INCIDENTES:
                    
                    COLUMNAS DISPONIBLES:
                    • ID: Identificador único del incidente
                    • Tipo: Accidente, Robo, Incendio
                    • Prioridad: Nivel de urgencia (1-5)
                    • Hora Inicio: Momento de creación
                    • Estado: Pendiente/Atendido
                    • Tiempo: Tiempo transcurrido
                    
                    ACCIONES DISPONIBLES:
                    
                    🚗 ENVIAR VEHÍCULO:
                    • Crea y asigna un vehículo apropiado
                    • Calcula la ruta más eficiente
                    • Actualiza el estado del incidente
                    
                    ✅ MARCAR COMO ATENDIDO:
                    • Finaliza el incidente
                    • Calcula puntuación obtenida
                    • Libera vehículos asignados
                    
                    🔄 ACTUALIZAR:
                    • Refresca la lista de incidentes
                    • Actualiza estadísticas
                    • Sincroniza con el simulador
                    
                    DETALLES DEL INCIDENTE:
                    • Información completa del incidente seleccionado
                    • Ubicación exacta
                    • Vehículos asignados
                    • Tiempo de respuesta
                    """;

            case "6. Solución de Problemas":
            case "6.1 Problemas Comunes":
                return """
                    SOLUCIÓN DE PROBLEMAS COMUNES
                    
                    PROBLEMA: La aplicación no inicia
                    SOLUCIÓN:
                    • Verificar que Java 11+ esté instalado
                    • Comprobar que JavaFX esté configurado correctamente
                    • Revisar los archivos FXML en la carpeta resources
                    
                    PROBLEMA: Los vehículos no se mueven
                    SOLUCIÓN:
                    • Verificar que la simulación esté iniciada
                    • Comprobar que hay rutas disponibles en el grafo
                    • Revisar la consola para errores de hilos
                    
                    PROBLEMA: Los semáforos no cambian
                    SOLUCIÓN:
                    • Verificar que los hilos de semáforos estén activos
                    • Comprobar que no hay excepciones en los hilos
                    • Reiniciar la simulación si es necesario
                    
                    PROBLEMA: No se crean incidentes
                    SOLUCIÓN:
                    • Verificar la configuración del generador de incidentes
                    • Comprobar que el simulador esté en ejecución
                    • Revisar los parámetros de probabilidad
                    
                    PROBLEMA: Errores de memoria
                    SOLUCIÓN:
                    • Aumentar la memoria heap de Java (-Xmx)
                    • Cerrar ventanas no utilizadas
                    • Reiniciar la aplicación periódicamente
                    
                    PROBLEMA: Interfaz no responde
                    SOLUCIÓN:
                    • Verificar que no hay bucles infinitos en hilos
                    • Comprobar el uso de Platform.runLater()
                    • Revisar la sincronización de datos
                    """;

            default:
                return """
                    SECCIÓN EN DESARROLLO
                    
                    Esta sección del manual está siendo desarrollada.
                    
                    Para obtener ayuda adicional, consulte:
                    • La documentación en línea
                    • Los comentarios en el código fuente
                    • El equipo de desarrollo
                    
                    Gracias por su paciencia.
                    """;
        }
    }

    /**
     * Cierra la ventana del manual
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}