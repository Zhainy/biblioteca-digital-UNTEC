<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Catálogo - Biblioteca UNTEC</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/app.css">
</head>
<body class="app-body app-bg-main d-flex flex-column min-vh-100">

<nav class="navbar app-navbar navbar-dark shadow-sm mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="#"><i class="bi bi-book me-2"></i>UNTEC Digital</a>
        <div class="d-flex text-white align-items-center">
            <span class="me-3 small">Bienvenido/a, <strong><c:out value="${usuarioLogueado.nombre}" /></strong></span>
            <a href="logout" class="btn btn-outline-light btn-sm">Salir</a>
        </div>
    </div>
</nav>

<main class="app-main">
<div class="container">
    <div class="row mb-4">
        <div class="col">
            <h3 class="fw-semibold">Catálogo de Libros</h3>
            <p class="text-muted">Gestione los préstamos y devoluciones online.</p>
        </div>
        <div class="col text-end">
            <a href="libros?accion=nuevo" class="btn btn-success">
                <i class="bi bi-plus-lg me-1"></i> Nuevo Libro
            </a>
        </div>
    </div>

    <div class="card app-card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light text-secondary">
                    <tr>
                        <th class="ps-4">Título</th>
                        <th>Autor</th>
                        <th>ISBN</th>
                        <th>Estado</th>
                        <th class="text-center">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${listaLibros}" var="libro">
                        <tr>
                            <td class="ps-4 fw-medium"><c:out value="${libro.titulo}" /></td>
                            <td><c:out value="${libro.autor}" /></td>
                            <td><code class="text-dark"><c:out value="${libro.isbn}" /></code></td>
                            <td>
                                <c:choose>
                                    <c:when test="${libro.disponible}">
                                        <span class="badge bg-success-subtle text-success border border-success status-badge">Disponible</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger-subtle text-danger border border-danger status-badge">Prestado</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <a href="libros?accion=detalle&id=${libro.id}" class="btn btn-sm btn-outline-primary" title="Ver detalle">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="libros?accion=editar&id=${libro.id}" class="btn btn-sm btn-outline-dark" title="Editar">
                                    <i class="bi bi-pencil"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>