package com.untec.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsrfTokenManagerTest {

    @Test
    public void ensureTokenGeneraYReutilizaTokenEnSesion() {
        CsrfTokenManager manager = CsrfTokenManager.getInstance();
        HttpSession session = mockSessionWithAttributes(new HashMap<>());

        String token1 = manager.ensureToken(session);
        String token2 = manager.ensureToken(session);

        assertNotNull(token1);
        assertFalse(token1.isBlank());
        assertEquals(token1, token2);
    }

    @Test
    public void isValidTokenRetornaTrueCuandoCoincide() {
        CsrfTokenManager manager = CsrfTokenManager.getInstance();
        Map<String, Object> attrs = new HashMap<>();
        HttpSession session = mockSessionWithAttributes(attrs);
        String token = manager.ensureToken(session);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("csrfToken")).thenReturn(token);

        assertTrue(manager.isValidToken(request, session));
    }

    @Test
    public void isValidTokenRetornaFalseCuandoNoCoincide() {
        CsrfTokenManager manager = CsrfTokenManager.getInstance();
        Map<String, Object> attrs = new HashMap<>();
        HttpSession session = mockSessionWithAttributes(attrs);
        manager.ensureToken(session);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("csrfToken")).thenReturn("token-invalido");

        assertFalse(manager.isValidToken(request, session));
    }

    @Test
    public void isValidTokenRetornaFalseSinSesion() {
        CsrfTokenManager manager = CsrfTokenManager.getInstance();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("csrfToken")).thenReturn("abc");

        assertFalse(manager.isValidToken(request, null));
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

