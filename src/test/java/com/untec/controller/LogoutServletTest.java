package com.untec.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogoutServletTest {

    @Test
    public void rechazaLogoutConTokenCsrfInvalido() throws Exception {
        LogoutServlet servlet = new LogoutServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("csrfToken", "token-esperado");
        HttpSession session = mockSessionWithAttributes(attrs);

        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("csrfToken")).thenReturn("token-invalido");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF invalido.");
        verify(session, never()).invalidate();
    }

    @Test
    public void cierraSesionCuandoTokenCsrfEsValido() throws Exception {
        LogoutServlet servlet = new LogoutServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("csrfToken", "token-valido");
        HttpSession session = mockSessionWithAttributes(attrs);

        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("csrfToken")).thenReturn("token-valido");

        servlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("login");
    }

    private HttpSession mockSessionWithAttributes(Map<String, Object> attrs) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(anyString())).thenAnswer(invocation -> attrs.get(invocation.getArgument(0)));
        doAnswer(invocation -> {
            attrs.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(session).setAttribute(anyString(), any());
        return session;
    }
}

