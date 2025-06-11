package org.example.tareaintegradora2apo.model.trafico;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GestorTrafico {
    
    private List<Semaforo> semaforos;
    private boolean sistemaActivo;

    public GestorTrafico() {
        this.semaforos = new CopyOnWriteArrayList<>();
        this.sistemaActivo = false;
    }

    public void configurarSemaforos(List<Semaforo> semaforos) {
        this.semaforos.clear();
        this.semaforos.addAll(semaforos);
    }

    public void iniciarSemaforos() {
        if (sistemaActivo) {
            return;
        }
        
        sistemaActivo = true;
        
        for (Semaforo semaforo : semaforos) {
            semaforo.iniciar();
        }
    }

    public void detenerSemaforos() {
        if (!sistemaActivo) {
            return;
        }
        
        sistemaActivo = false;
        
        for (Semaforo semaforo : semaforos) {
            semaforo.detener();
        }
    }

    public void agregarSemaforo(Semaforo semaforo) {
        semaforos.add(semaforo);
        
        if (sistemaActivo) {
            semaforo.iniciar();
        }
    }

    public void removerSemaforo(Semaforo semaforo) {
        semaforo.detener();
        semaforos.remove(semaforo);
    }

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
    

    public List<Semaforo> getSemaforos() {
        return new ArrayList<>(semaforos);
    }

    public boolean isSistemaActivo() {
        return sistemaActivo;
    }

    public int getSemaforosEnRojo() {
        int contador = 0;
        for (Semaforo semaforo : semaforos) {
            if (semaforo.isRojo()) {
                contador++;
            }
        }
        return contador;
    }

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