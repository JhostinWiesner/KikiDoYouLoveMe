package org.example.tareaintegradora2apo.model.trafico;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase que gestiona el sistema de tráfico y semáforos en el simulador SGMMS.
 */
public class GestorTrafico {
    
    private List<Semaforo> semaforos;
    private boolean sistemaActivo;
    
    /**
     * Constructor para la clase GestorTrafico
     */
    public GestorTrafico() {
        this.semaforos = new CopyOnWriteArrayList<>();
        this.sistemaActivo = false;
    }
    
    /**
     * Configura los semáforos del sistema
     * @param semaforos Lista de semáforos a gestionar
     */
    public void configurarSemaforos(List<Semaforo> semaforos) {
        this.semaforos.clear();
        this.semaforos.addAll(semaforos);
    }
    
    /**
     * Inicia todos los semáforos del sistema
     */
    public void iniciarSemaforos() {
        if (sistemaActivo) {
            return;
        }
        
        sistemaActivo = true;
        
        for (Semaforo semaforo : semaforos) {
            semaforo.iniciar();
        }
    }
    
    /**
     * Detiene todos los semáforos del sistema
     */
    public void detenerSemaforos() {
        if (!sistemaActivo) {
            return;
        }
        
        sistemaActivo = false;
        
        for (Semaforo semaforo : semaforos) {
            semaforo.detener();
        }
    }
    
    /**
     * Agrega un semáforo al sistema
     * @param semaforo Semáforo a agregar
     */
    public void agregarSemaforo(Semaforo semaforo) {
        semaforos.add(semaforo);
        
        if (sistemaActivo) {
            semaforo.iniciar();
        }
    }
    
    /**
     * Remueve un semáforo del sistema
     * @param semaforo Semáforo a remover
     */
    public void removerSemaforo(Semaforo semaforo) {
        semaforo.detener();
        semaforos.remove(semaforo);
    }
    
    /**
     * Obtiene las estadísticas de tráfico
     * @return Estadísticas como mapa
     */
    public java.util.Map<String, Object> obtenerEstadisticasTrafico() {
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
        
        int totalRespeto = 0;
        int totalViolaciones = 0;
        int semaforosActivos = 0;
        
        for (Semaforo semaforo : semaforos) {
            totalRespeto += semaforo.getVehiculosRespetaron();
            totalViolaciones += semaforo.getVehiculosViolaron();
            
            if (semaforo.isActivo()) {
                semaforosActivos++;
            }
        }
        
        estadisticas.put("total_respeto", totalRespeto);
        estadisticas.put("total_violaciones", totalViolaciones);
        estadisticas.put("semaforos_activos", semaforosActivos);
        estadisticas.put("total_semaforos", semaforos.size());
        
        // Calcular porcentaje de respeto
        int totalInteracciones = totalRespeto + totalViolaciones;
        double porcentajeRespeto = totalInteracciones > 0 ? 
            (double) totalRespeto / totalInteracciones * 100.0 : 100.0;
        
        estadisticas.put("porcentaje_respeto", porcentajeRespeto);
        
        return estadisticas;
    }
    
    /**
     * Obtiene todos los semáforos del sistema
     * @return Lista de semáforos
     */
    public List<Semaforo> getSemaforos() {
        return new ArrayList<>(semaforos);
    }
    
    /**
     * Verifica si el sistema de tráfico está activo
     * @return true si está activo, false en caso contrario
     */
    public boolean isSistemaActivo() {
        return sistemaActivo;
    }
    
    /**
     * Obtiene el número de semáforos en rojo
     * @return Número de semáforos en rojo
     */
    public int getSemaforosEnRojo() {
        int contador = 0;
        for (Semaforo semaforo : semaforos) {
            if (semaforo.isRojo()) {
                contador++;
            }
        }
        return contador;
    }
    
    /**
     * Obtiene el número de semáforos en verde
     * @return Número de semáforos en verde
     */
    public int getSemaforosEnVerde() {
        int contador = 0;
        for (Semaforo semaforo : semaforos) {
            if (!semaforo.isRojo() && !semaforo.isAmarillo()) {
                contador++;
            }
        }
        return contador;
    }
}