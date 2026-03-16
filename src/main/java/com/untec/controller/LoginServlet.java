package com.untec.controller;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recibir datos del formulario
        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        // 2. Simulación de validación
        if ("admin@untec.com".equals(email) && "1234".equals(pass)) {
            // 3. Crear sesión de usuario
            HttpSession session = request.getSession();
            Usuario usuario = new Usuario(1, "Nicole", email, pass);
            session.setAttribute("usuarioLogueado", usuario);

            // Redirigir al listado de libros si es exitoso
            response.sendRedirect("libros");
        } else {
            // Si falla, volver al login con un mensaje de error
            request.setAttribute("mensajeError", "Credenciales incorrectas");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}