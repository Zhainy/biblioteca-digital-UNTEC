package com.untec.dao;

import com.untec.model.Libro;
import com.untec.utils.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements ILibroDAO {

    @Override
    public List<Libro> listarTodos() {
        List<Libro> listaLibros = new ArrayList<>();
        String sql = "SELECT * FROM libros";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId(rs.getInt("id"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setDisponible(rs.getBoolean("disponible"));
                listaLibros.add(libro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaLibros;
    }

    @Override
    public boolean insertar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, disponible) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setBoolean(4, libro.isDisponible());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, disponible = ? WHERE id = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setBoolean(4, libro.isDisponible());
            ps.setInt(5, libro.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Libro obtenerPorId(int id) {
        Libro libro = null;
        String sql = "SELECT * FROM libros WHERE id = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    libro = new Libro();
                    libro.setId(rs.getInt("id"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setIsbn(rs.getString("isbn"));
                    libro.setDisponible(rs.getBoolean("disponible"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libro;
    }
}