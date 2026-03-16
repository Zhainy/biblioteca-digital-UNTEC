package com.untec.filter;

import com.untec.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthFilterTest {

	@Test
	public void redirigeALoginCuandoNoHaySesion() throws Exception {
		AuthFilter filter = new AuthFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getSession(false)).thenReturn(null);
		when(request.getContextPath()).thenReturn("/BibliotecaDigitalUNTEC");

		filter.doFilter(request, response, chain);

		verify(response).sendRedirect("/BibliotecaDigitalUNTEC/login");
		verify(chain, never()).doFilter(any(), any());
	}

	@Test
	public void continuaCuandoUsuarioEstaAutenticadoYAseguraToken() throws Exception {
		AuthFilter filter = new AuthFilter();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		Map<String, Object> attrs = new HashMap<>();
		Usuario usuario = new Usuario();
		usuario.setEmail("admin@untec.com");
		attrs.put("usuarioLogueado", usuario);

		HttpSession session = mockSessionWithAttributes(attrs);
		when(request.getSession(false)).thenReturn(session);

		filter.doFilter(request, response, chain);

		verify(chain).doFilter(request, response);
		verify(response, never()).sendRedirect(anyString());
		assertNotNull(attrs.get("csrfToken"));
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

