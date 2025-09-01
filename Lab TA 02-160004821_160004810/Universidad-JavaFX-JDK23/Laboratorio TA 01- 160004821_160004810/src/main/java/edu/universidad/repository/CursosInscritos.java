package edu.universidad.repository;

import edu.universidad.model.*;
import edu.universidad.persistence.InscripcionDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CursosInscritos implements Servicios<Inscripcion> {
    private final List<Inscripcion> listado = new ArrayList<>();
    private final InscripcionDAO dao;
    private final List<Estudiante> catalogoEstudiantes;
    private final List<Curso> catalogoCursos;

    public CursosInscritos(InscripcionDAO dao, List<Estudiante> catalogoEstudiantes, List<Curso> catalogoCursos) {
        this.dao = dao;
        this.catalogoEstudiantes = catalogoEstudiantes;
        this.catalogoCursos = catalogoCursos;
    }

    public boolean inscribirCurso(Inscripcion inscripcion) {
        Objects.requireNonNull(inscripcion, "inscripcion no puede ser null");
        return listado.add(inscripcion);
    }

    public boolean eliminar(Inscripcion inscripcion) {
        return listado.remove(inscripcion);
    }

    public boolean actualizar(Inscripcion inscripcion) {
        Objects.requireNonNull(inscripcion, "inscripcion no puede ser null");
        Optional<Inscripcion> first = listado.stream()
                .filter(i -> i.equals(inscripcion)).findFirst();
        if (first.isPresent()) {
            int index = listado.indexOf(first.get());
            listado.set(index, inscripcion);
            return true;
        }
        return false;
    }

    public void guardarInformacion(Inscripcion inscripcion) {
        dao.insertar(inscripcion);
    }

    public void cargarDatos() {
        listado.clear();
        listado.addAll(dao.listar(catalogoEstudiantes, catalogoCursos));
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
        return "CursosInscritos{listado=" + listado.size() + "}";
    }

    public List<Inscripcion> getListado() {
        return listado;
    }
}
