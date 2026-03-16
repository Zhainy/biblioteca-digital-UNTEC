<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
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
        <a class="navbar-brand fw-bold" href="libros"><i class="bi bi-book me-2"></i>UNTEC Digital</a>
        <div class="d-flex text-white align-items-center">
            <span class="me-3 small">Bienvenido/a, <strong><c:out value="${usuarioLogueado.nombre}" /></strong></span>
            <form action="logout" method="post" class="m-0">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <button type="submit" class="btn btn-outline-light btn-sm">Salir</button>
            </form>
        </div>
    </div>
</nav>

<main class="app-main">
<div class="container">
    <c:if test="${not empty sessionScope.mensajeExito}">
            <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
                <div class="d-flex align-items-center">
                    <i class="bi bi-check-circle-fill me-2 fs-5"></i>
                    <div>
                        <c:out value="${sessionScope.mensajeExito}" />
                    </div>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <%-- Limpiamos el mensaje de la sesión para que no aparezca de nuevo al refrescar --%>
            <c:remove var="mensajeExito" scope="session" />
        </c:if>
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

                                <button type="button" class="btn btn-sm btn-outline-danger" title="Eliminar"
                                   data-libro-id="${libro.id}"
                                   data-libro-titulo="<c:out value='${libro.titulo}' />"
                                   onclick="prepararEliminar(this)">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</main>
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-danger text-white border-0">
                <h5 class="modal-title" id="deleteModalLabel"><i class="bi bi-exclamation-triangle me-2"></i>Confirmar Eliminación</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4 text-center">
                <p class="mb-1">¿Estás seguro de que deseas eliminar este libro?</p>
                <h5 id="bookTitleDisplay" class="fw-bold text-dark"></h5>
                <p class="text-muted small mt-2">Esta acción no se puede deshacer.</p>
            </div>
            <div class="modal-footer border-0 justify-content-center pb-4">
                <button type="button" class="btn btn-light px-4" data-bs-dismiss="modal">Cancelar</button>
                <form action="libros" method="post" class="m-0">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="hidden" id="confirmDeleteId" name="id" value="">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <button type="submit" class="btn btn-danger px-4">Eliminar permanentemente</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function prepararEliminar(button) {
        const id = button.dataset.libroId;
        const titulo = button.dataset.libroTitulo;

        document.getElementById('bookTitleDisplay').innerText = titulo;

        document.getElementById('confirmDeleteId').value = id;

        const myModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        myModal.show();
    }
</script>
</body>
</html>