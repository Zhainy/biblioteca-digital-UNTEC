package com.untec.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

public class CsrfTokenManager {
    private static final String TOKEN_ATTR = "csrfToken";
    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    private CsrfTokenManager() {
    }

    private static class Holder {
        private static final CsrfTokenManager INSTANCE = new CsrfTokenManager();
    }

    public static CsrfTokenManager getInstance() {
        return Holder.INSTANCE;
    }

    public String ensureToken(HttpSession session) {
        String token = (String) session.getAttribute(TOKEN_ATTR);
        if (token == null || token.isBlank()) {
            token = generateToken();
            session.setAttribute(TOKEN_ATTR, token);
        }
        return token;
    }

    public boolean isValidToken(HttpServletRequest request, HttpSession session) {
        if (session == null) {
            return false;
        }

        String expected = (String) session.getAttribute(TOKEN_ATTR);
        String received = request.getParameter(TOKEN_ATTR);

        if (expected == null || expected.isBlank() || received == null || received.isBlank()) {
            return false;
        }

        return constantTimeEquals(expected, received);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}

