package com.untec.controller;

import com.untec.dao.ILibroDAO;
import com.untec.dao.LibroDAOImpl;
import com.untec.model.Libro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/libros")
public class LibroServlet extends HttpServlet {

    private final ILibroDAO libroDAO = new LibroDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!usuarioAutenticado(request, response)) {
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.isBlank()) {
            accion = "listar";
        }

        switch (accion) {
            case "nuevo":
                request.setAttribute("modoEdicion", false);
                request.getRequestDispatcher("nuevoLibro.jsp").forward(request, response);
                break;
            case "detalle":
                mostrarDetalle(request, response);
                break;
            case "editar":
                mostrarFormularioEdicion(request, response);
                break;
            case "listar":
            default:
                listarLibros(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!usuarioAutenticado(request, response)) {
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.isBlank() || "guardar".equals(accion)) {
            guardarLibro(request, response);
            return;
        }

        if ("actualizar".equals(accion)) {
            actualizarLibro(request, response);
            return;
        }

        response.sendRedirect("libros");
    }

    private void listarLibros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Libro> lista = libroDAO.listarTodos();
        request.setAttribute("listaLibros", lista);
        request.getRequestDispatcher("listaLibros.jsp").forward(request, response);
    }

    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect("libros");
            return;
        }

        Libro libro = libroDAO.obtenerPorId(id);
        if (libro == null) {
            response.sendRedirect("libros");
            return;
        }

        request.setAttribute("libro", libro);
        request.getRequestDispatcher("detalleLibro.jsp").forward(request, response);
    }

    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect("libros");
            return;
        }

        Libro libro = libroDAO.obtenerPorId(id);
        if (libro == null) {
            response.sendRedirect("libros");
            return;
        }

        request.setAttribute("modoEdicion", true);
        request.setAttribute("libro", libro);
        request.getRequestDispatcher("nuevoLibro.jsp").forward(request, response);
    }

    private void guardarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Libro nuevoLibro = mapearLibroDesdeRequest(request);
        boolean insertado = libroDAO.insertar(nuevoLibro);

        if (insertado) {
            response.sendRedirect("libros");
        } else {
            request.setAttribute("mensajeError", "No se pudo registrar el libro.");
            request.setAttribute("modoEdicion", false);
            request.setAttribute("libro", nuevoLibro);
            request.getRequestDispatcher("nuevoLibro.jsp").forward(request, response);
        }
    }

    private void actualizarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect("libros");
            return;
        }

        Libro libro = mapearLibroDesdeRequest(request);
        libro.setId(id);
        boolean actualizado = libroDAO.actualizar(libro);

        if (actualizado) {
            response.sendRedirect("libros?accion=detalle&id=" + id);
        } else {
            request.setAttribute("mensajeError", "No se pudo actualizar el libro.");
            request.setAttribute("modoEdicion", true);
            request.setAttribute("libro", libro);
            request.getRequestDispatcher("nuevoLibro.jsp").forward(request, response);
        }
    }

    private Libro mapearLibroDesdeRequest(HttpServletRequest request) {
        Libro libro = new Libro();
        libro.setTitulo(request.getParameter("titulo"));
        libro.setAutor(request.getParameter("autor"));
        libro.setIsbn(request.getParameter("isbn"));
        libro.setDisponible(Boolean.parseBoolean(request.getParameter("disponible")));
        return libro;
    }

    private Integer parseId(String rawId) {
        try {
            return Integer.parseInt(rawId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean usuarioAutenticado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            return true;
        }

        response.sendRedirect("index.jsp");
        return false;
    }
}