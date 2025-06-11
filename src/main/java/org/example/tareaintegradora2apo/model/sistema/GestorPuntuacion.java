package org.example.tareaintegradora2apo.model.sistema;

import org.example.tareaintegradora2apo.model.incidentes.Incidente;
import org.example.tareaintegradora2apo.model.incidentes.Accidente;
import org.example.tareaintegradora2apo.model.incidentes.Robo;
import org.example.tareaintegradora2apo.model.incidentes.Incendio;


public class GestorPuntuacion {
    
    private int puntuacionTotal;
    private int incidentesAtendidos;
    private int incidentesNoAtendidos;
    private long tiempoPromedioRespuesta;
    

    private static final int PUNTOS_BASE_ACCIDENTE = 100;
    private static final int PUNTOS_BASE_ROBO = 75;
    private static final int PUNTOS_BASE_INCENDIO = 150;
    
    private static final int BONUS_TIEMPO_RAPIDO = 50;
    private static final int PENALTY_TIEMPO_LENTO = -25;
    private static final int PENALTY_NO_ATENDIDO = -100;
    
    private static final long TIEMPO_RAPIDO_LIMITE = 30; // 30 segundos
    private static final long TIEMPO_LENTO_LIMITE = 120; // 2 minutos
    

    public GestorPuntuacion() {
        this.puntuacionTotal = 0;
        this.incidentesAtendidos = 0;
        this.incidentesNoAtendidos = 0;
        this.tiempoPromedioRespuesta = 0;
    }
    

    public int calcularPuntos(Incidente incidente) {
        int puntosBase = obtenerPuntosBase(incidente);
        int bonusTiempo = calcularBonusTiempo(incidente);
        int bonusPrioridad = calcularBonusPrioridad(incidente);
        
        return puntosBase + bonusTiempo + bonusPrioridad;
    }
    

    private int obtenerPuntosBase(Incidente incidente) {
        if (incidente instanceof Accidente) {
            return PUNTOS_BASE_ACCIDENTE;
        } else if (incidente instanceof Robo) {
            return PUNTOS_BASE_ROBO;
        } else if (incidente instanceof Incendio) {
            return PUNTOS_BASE_INCENDIO;
        }
        return 0;
    }
    

    private int calcularBonusTiempo(Incidente incidente) {
        long tiempoRespuesta = incidente.getTiempoRespuesta();
        
        if (tiempoRespuesta <= TIEMPO_RAPIDO_LIMITE) {
            return BONUS_TIEMPO_RAPIDO;
        } else if (tiempoRespuesta >= TIEMPO_LENTO_LIMITE) {
            return PENALTY_TIEMPO_LENTO;
        }
        
        return 0;
    }
    

    private int calcularBonusPrioridad(Incidente incidente) {
        return incidente.getPrioridad() * 10;
    }
    

    public void agregarPuntos(int puntos) {
        this.puntuacionTotal += puntos;
        this.incidentesAtendidos++;
    }
    

    public void penalizarIncidenteNoAtendido(Incidente incidente) {
        this.puntuacionTotal += PENALTY_NO_ATENDIDO;
        this.incidentesNoAtendidos++;
    }
    

    public double calcularEficiencia() {
        int totalIncidentes = incidentesAtendidos + incidentesNoAtendidos;
        if (totalIncidentes == 0) {
            return 100.0;
        }
        
        return (double) incidentesAtendidos / totalIncidentes * 100.0;
    }
    

    public String calcularRanking() {
        if (puntuacionTotal >= 2000) {
            return "Excelente";
        } else if (puntuacionTotal >= 1500) {
            return "Muy Bueno";
        } else if (puntuacionTotal >= 1000) {
            return "Bueno";
        } else if (puntuacionTotal >= 500) {
            return "Regular";
        } else {
            return "Deficiente";
        }
    }
    

    public void reiniciar() {
        this.puntuacionTotal = 0;
        this.incidentesAtendidos = 0;
        this.incidentesNoAtendidos = 0;
        this.tiempoPromedioRespuesta = 0;
    }
    
    // Getters
    
    public int getPuntuacionTotal() {
        return puntuacionTotal;
    }
    
    public int getIncidentesAtendidos() {
        return incidentesAtendidos;
    }
    
    public int getIncidentesNoAtendidos() {
        return incidentesNoAtendidos;
    }
    
    public long getTiempoPromedioRespuesta() {
        return tiempoPromedioRespuesta;
    }
    

    public String obtenerResumen() {
        return String.format(
            "Puntuación: %d | Atendidos: %d | No Atendidos: %d | Eficiencia: %.1f%% | Ranking: %s",
            puntuacionTotal, incidentesAtendidos, incidentesNoAtendidos, 
            calcularEficiencia(), calcularRanking()
        );
    }
}