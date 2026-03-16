package com.untec.controller;

import com.untec.dao.LibroDAOImpl;
import com.untec.model.Libro;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/libros")
public class LibroServlet extends HttpServlet {

    private LibroDAOImpl libroDAO = new LibroDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtenemos los datos desde el DAO
        List<Libro> lista = libroDAO.listarTodos();

        // 2. Los guardamos en el "request" para que el JSP los vea
        request.setAttribute("listaLibros", lista);

        // 3. Redirigimos a la vista (JSP)
        request.getRequestDispatcher("listaLibros.jsp").forward(request, response);
    }
}