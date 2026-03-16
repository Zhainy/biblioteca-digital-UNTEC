<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>
        <c:choose>
            <c:when test="${modoEdicion}">Editar Libro - Biblioteca UNTEC</c:when>
            <c:otherwise>Registrar Libro - Biblioteca UNTEC</c:otherwise>
        </c:choose>
    </title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="assets/css/app.css">
</head>
<body class="app-body app-bg-main d-flex flex-column min-vh-100">

<nav class="navbar app-navbar navbar-dark shadow-sm mb-5">
    <div class="container">
        <a class="navbar-brand fw-bold" href="libros">
            <i class="bi bi-book me-2"></i>UNTEC Digital
        </a>
    </div>
</nav>

<main class="app-main">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card app-card shadow-sm p-4">
                <div class="mb-4">
                    <h3 class="fw-bold">
                        <c:choose>
                            <c:when test="${modoEdicion}">
                                <i class="bi bi-pencil-square me-2 text-primary"></i>Editar Libro
                            </c:when>
                            <c:otherwise>
                                <i class="bi bi-plus-circle me-2 text-primary"></i>Nuevo Registro
                            </c:otherwise>
                        </c:choose>
                    </h3>
                    <p class="text-muted small">
                        <c:choose>
                            <c:when test="${modoEdicion}">Modifique los datos y guarde los cambios del libro.</c:when>
                            <c:otherwise>Complete los campos para añadir un libro al catálogo manual.</c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <c:if test="${not empty mensajeError}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${mensajeError}" />
                    </div>
                </c:if>

                <form action="libros" method="post">
                    <input type="hidden" name="accion" value="${modoEdicion ? 'actualizar' : 'guardar'}">
                    <c:if test="${modoEdicion}">
                        <input type="hidden" name="id" value="${libro.id}">
                    </c:if>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Título del Libro</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white"><i class="bi bi-journal-text"></i></span>
                            <input type="text" name="titulo" class="form-control" placeholder="Ej: Java EE Masterclass" value="${libro.titulo}" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Autor</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white"><i class="bi bi-person"></i></span>
                            <input type="text" name="autor" class="form-control" placeholder="Ej: James Gosling" value="${libro.autor}" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">ISBN</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white"><i class="bi bi-upc-scan"></i></span>
                            <input type="text" name="isbn" class="form-control" placeholder="978-XXXXXXXXXX" value="${libro.isbn}" required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold">Estado Inicial</label>
                        <select name="disponible" class="form-select">
                            <option value="true" ${libro.disponible ? 'selected' : ''}>Disponible inmediatamente</option>
                            <option value="false" ${not libro.disponible ? 'selected' : ''}>No disponible (En revisión/Préstamo)</option>
                        </select>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary py-2 fw-bold">
                            <i class="bi bi-save me-2"></i>
                            <c:choose>
                                <c:when test="${modoEdicion}">Guardar Cambios</c:when>
                                <c:otherwise>Guardar en Biblioteca</c:otherwise>
                            </c:choose>
                        </button>
                        <a href="libros" class="btn btn-light py-2 text-muted">Cancelar</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>