package edu.universidad.repository;

import java.util.List;

public interface Servicios<T> {
    String imprimirPosicion(int posicion);
    Integer cantidadActual();
    List<String> imprimirListado();
}
