package org.example.tareaintegradora2apo.model.estructuras;

import org.example.tareaintegradora2apo.model.incidentes.Incidente;

import java.util.Comparator;


public class ComparatorIncidente implements Comparator<Incidente> {
    
    @Override
    public int compare(Incidente i1, Incidente i2) {
        // Ordenar por prioridad (de mayor a menor)
        return Integer.compare(i2.getPrioridad(), i1.getPrioridad());
    }
}