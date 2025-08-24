package edu.universidad.repository;

import edu.universidad.model.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InscripcionesPersonas implements Servicios<Persona> {
    private List<Persona> listado = new ArrayList<>();

    public boolean inscribir(Persona persona) {
        Objects.requireNonNull(persona, "persona no puede ser null");
        return listado.add(persona);
    }

    public boolean eliminar(Persona persona) {
        return listado.remove(persona);
    }

    public boolean actualizar(Persona persona) {
        Objects.requireNonNull(persona, "persona no puede ser null");
        int idx = -1;
        for (int i = 0; i < listado.size(); i++) {
            if (Double.compare(listado.get(i).getID(), persona.getID()) == 0) {
                idx = i; break;
            }
        }
        if (idx >= 0) {
            listado.set(idx, persona);
            return true;
        }
        return false;
    }

    public void guardarInformacion(Persona persona) {
    }

    public void cargarDatos() {
    }

    @Override
    public String imprimirPosicion(int posicion) {
        if (posicion < 0 || posicion >= listado.size()) return "posición inválida";
        return listado.get(posicion).toString();
    }

    @Override
    public Integer cantidadActual() {
        return listado.size();
    }

    @Override
    public List<String> imprimirListado() {
        return listado.stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "InscripcionesPersonas{listado=" + listado.size() + "}";
    }

    public List<Persona> getListado() {
        return listado;
    }
}
