<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle de Libro - Biblioteca UNTEC</title>
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
        <div class="col-md-7">
            <div class="card app-card shadow-sm p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h3 class="fw-bold mb-0"><i class="bi bi-journal-bookmark me-2 text-primary"></i>Detalle del Libro</h3>
                    <span class="badge ${libro.disponible ? 'bg-success' : 'bg-danger'}">
                        ${libro.disponible ? 'Disponible' : 'Prestado'}
                    </span>
                </div>

                <dl class="row mb-0">
                    <dt class="col-sm-3">ID</dt>
                    <dd class="col-sm-9"><c:out value="${libro.id}" /></dd>

                    <dt class="col-sm-3">Titulo</dt>
                    <dd class="col-sm-9"><c:out value="${libro.titulo}" /></dd>

                    <dt class="col-sm-3">Autor</dt>
                    <dd class="col-sm-9"><c:out value="${libro.autor}" /></dd>

                    <dt class="col-sm-3">ISBN</dt>
                    <dd class="col-sm-9"><c:out value="${libro.isbn}" /></dd>
                </dl>

                <div class="d-flex gap-2 mt-4">
                    <a href="libros?accion=editar&id=${libro.id}" class="btn btn-primary">
                        <i class="bi bi-pencil me-1"></i>Editar
                    </a>
                    <a href="libros" class="btn btn-light text-muted">Volver al listado</a>
                </div>
            </div>
        </div>
    </div>
</div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
