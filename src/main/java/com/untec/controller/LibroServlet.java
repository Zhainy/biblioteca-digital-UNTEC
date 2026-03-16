package com.untec.controller;

import com.untec.dao.ILibroDAO;
import com.untec.dao.LibroDAOImpl;
import com.untec.model.Libro;
import com.untec.utils.CsrfTokenManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/libros")
public class LibroServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LibroServlet.class.getName());
    private static final int MAX_TITULO = 150;
    private static final int MAX_AUTOR = 120;

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
        if (accion == null || accion.isBlank()) {
            accion = "guardar";
        }

        if (esAccionMutadora(accion)
                && !CsrfTokenManager.getInstance().isValidToken(request, request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF invalido.");
            return;
        }

        if ("guardar".equals(accion)) {
            guardarLibro(request, response);
            return;
        }

        if ("actualizar".equals(accion)) {
            actualizarLibro(request, response);
            return;
        }

        if ("eliminar".equals(accion)) {
            eliminarLibro(request, response);
            return;
        }

        response.sendRedirect("libros");
    }

    private boolean esAccionMutadora(String accion) {
        return "guardar".equals(accion) || "actualizar".equals(accion) || "eliminar".equals(accion);
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
        String errorValidacion = validarLibro(nuevoLibro, request.getParameter("disponible"));
        if (errorValidacion != null) {
            request.setAttribute("mensajeError", errorValidacion);
            request.setAttribute("modoEdicion", false);
            request.setAttribute("libro", nuevoLibro);
            request.getRequestDispatcher("nuevoLibro.jsp").forward(request, response);
            return;
        }

        boolean insertado = libroDAO.insertar(nuevoLibro);

        if (insertado) {
            request.getSession().setAttribute("mensajeExito", "¡Libro '" + nuevoLibro.getTitulo() + "' registrado con éxito!");
            response.sendRedirect("libros");
        } else {
            request.setAttribute("mensajeError", "Error: No se pudo registrar el libro.");
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
        String errorValidacion = validarLibro(libro, request.getParameter("disponible"));
        if (errorValidacion != null) {
            libro.setId(id);
            request.setAttribute("mensajeError", errorValidacion);
            request.setAttribute("modoEdicion", true);
            request.setAttribute("libro", libro);
            request.getRequestDispatcher("nuevoLibro.jsp").forward(request, response);
            return;
        }

        libro.setId(id);
        boolean actualizado = libroDAO.actualizar(libro);

        if (actualizado) {
            // Mensaje de éxito para la edición
            request.getSession().setAttribute("mensajeExito", "¡Cambios guardados correctamente en '" + libro.getTitulo() + "'!");
            response.sendRedirect("libros");
        } else {
            request.setAttribute("mensajeError", "Error: No se pudo actualizar el libro.");
            request.setAttribute("modoEdicion", true);
            request.setAttribute("libro", libro);
            request.getRequestDispatcher("nuevoLibro.jsp").forward(request, response);
        }
    }

    private void eliminarLibro(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id != null) {
            boolean eliminado = libroDAO.eliminar(id);
            if (eliminado) {
                request.getSession().setAttribute("mensajeExito", "Libro eliminado correctamente.");
            }
        }
        response.sendRedirect("libros");
    }
    private Libro mapearLibroDesdeRequest(HttpServletRequest request) {
        Libro libro = new Libro();
        libro.setTitulo(limpiarTexto(request.getParameter("titulo")));
        libro.setAutor(limpiarTexto(request.getParameter("autor")));
        libro.setIsbn(limpiarTexto(request.getParameter("isbn")));
        libro.setDisponible(Boolean.parseBoolean(request.getParameter("disponible")));
        return libro;
    }

    private String validarLibro(Libro libro, String estadoRaw) {
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            return "El titulo es obligatorio.";
        }
        if (libro.getTitulo().length() > MAX_TITULO) {
            return "El titulo no puede superar los " + MAX_TITULO + " caracteres.";
        }

        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            return "El autor es obligatorio.";
        }
        if (libro.getAutor().length() > MAX_AUTOR) {
            return "El autor no puede superar los " + MAX_AUTOR + " caracteres.";
        }

        if (!isbnValido(libro.getIsbn())) {
            return "El ISBN debe contener 10 o 13 digitos (puede incluir guiones).";
        }

        if (!"true".equalsIgnoreCase(estadoRaw) && !"false".equalsIgnoreCase(estadoRaw)) {
            LOGGER.warning("Valor invalido para 'disponible' recibido en formulario de libro.");
            return "El estado del libro es invalido.";
        }

        return null;
    }

    private boolean isbnValido(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }
        String normalizado = isbn.replaceAll("[-\\s]", "");
        return normalizado.matches("\\d{10}|\\d{13}");
    }

    private String limpiarTexto(String valor) {
        return valor == null ? null : valor.trim();
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