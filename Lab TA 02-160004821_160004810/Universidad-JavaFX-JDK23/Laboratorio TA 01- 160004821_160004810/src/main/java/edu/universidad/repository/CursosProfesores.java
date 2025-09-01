package edu.universidad.repository;

import edu.universidad.model.CursoProfesor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CursosProfesores implements Servicios<CursoProfesor> {
    private List<CursoProfesor> listado = new ArrayList<>();

    public boolean inscribir(CursoProfesor cursoProfesor) {
        Objects.requireNonNull(cursoProfesor, "cursoProfesor no puede ser null");
        return listado.add(cursoProfesor);
    }

    public boolean eliminar(CursoProfesor cursoProfesor) {
        return listado.remove(cursoProfesor);
    }

    public boolean guardarInformacion(CursoProfesor cursoProfesor) {
        return true;
    }

    public boolean actualizar(CursoProfesor cursoProfesor) {
        Objects.requireNonNull(cursoProfesor);
        return true;
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
        return "CursosProfesores{listado=" + listado.size() + "}";
    }

    public List<CursoProfesor> getListado() {
        return listado;
    }
}
