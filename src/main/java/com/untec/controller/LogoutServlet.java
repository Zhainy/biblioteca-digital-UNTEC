package com.untec.controller;

import com.untec.utils.CsrfTokenManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (!CsrfTokenManager.getInstance().isValidToken(request, session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF invalido.");
            return;
        }

        if (session != null) {
            session.invalidate(); // Destruye la sesión
        }
        response.sendRedirect("login"); // Redirige al login
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("libros");
    }
}