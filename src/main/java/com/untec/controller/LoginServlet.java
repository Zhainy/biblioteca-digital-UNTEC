package com.untec.controller;

import com.untec.dao.UsuarioDAOImpl;
import com.untec.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirigir al formulario de login
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    private UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        // VALIDACIÓN
        Usuario user = usuarioDAO.validar(email, pass);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", user);
            response.sendRedirect("libros");
        } else {
            request.setAttribute("mensajeError", "Credenciales incorrectas en la base de datos.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}