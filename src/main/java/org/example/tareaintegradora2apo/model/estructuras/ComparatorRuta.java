package org.example.tareaintegradora2apo.model.estructuras;

import java.util.Comparator;
import org.example.tareaintegradora2apo.model.trafico.Ruta;

/**
 * Comparador para ordenar rutas por tiempo de inicio.
 */
public class ComparatorRuta implements Comparator<Ruta> {
    
    @Override
    public int compare(Ruta r1, Ruta r2) {
        // Ordenar por tiempo de inicio (de menor a mayor)
        return r1.getTiempoInicio().compareTo(r2.getTiempoInicio());
    }
}