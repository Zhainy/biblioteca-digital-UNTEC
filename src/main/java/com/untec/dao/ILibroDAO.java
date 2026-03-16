package com.untec.dao;

import com.untec.model.Libro;
import java.util.List;

public interface ILibroDAO {
    boolean insertar(Libro libro);
    List<Libro> listarTodos();
    boolean actualizar(Libro libro);
    boolean eliminar(int id);
    Libro obtenerPorId(int id);
}