<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ taglib prefix="c" uri="jakarta.tags.core" %> 
<!DOCTYPE html> 
<html lang="es"> 
<head> 
    <title>${titulo}</title>
    <%@ include file="head.jsp" %>
</head> 
<body> 
    <div class="container py-5">
        <h1 class="mb-2 text-primary"><c:out value="${titulo}"/></h1> 
        <p class="text-muted mb-4">Total disponible: <span class="badge bg-info">${totalProductos}</span> productos</p> 
  
        <div class="mb-4">
            <c:url value="/tienda/nuevo" var="urlNuevo" />
            <a href="${urlNuevo}" class="btn btn-success">➕ Agregar Nuevo Producto</a>
        </div>

        <div class="table-responsive">
            <table class="table table-hover table-striped align-middle"> 
                <thead class="table-success">
                    <tr> 
                        <th>Imagen</th>
                        <th>ID</th> 
                        <th>Nombre</th> 
                        <th>Categoría</th> 
                        <th>Precio</th> 
                        <th>Acciones</th> 
                    </tr> 
                </thead>
                <tbody>
                    <c:forEach var="p" items="${catalogo}"> 
                        <tr> 
                            <td>
                                <img src="${pageContext.request.contextPath}/recursos/imgs/${p.imagen}" 
                                     alt="${p.nombre}" 
                                     class="img-thumbnail" style="width: 80px; height: 80px; object-fit: cover;">
                            </td>
                            <td><span class="badge bg-secondary">${p.id}</span></td> 
                            <td><strong><c:out value="${p.nombre}"/></strong></td> 
                            <td><span class="badge bg-info"><c:out value="${p.categoria}"/></span></td> 
                            <td><strong class="text-success">$${p.precio}</strong></td> 
                            <td> 
                                <c:url value="/tienda/producto" var="urlProducto"> 
                                    <c:param name="id" value="${p.id}" /> 
                                </c:url> 
                                <a href="${urlProducto}" class="btn btn-sm btn-primary">👁️ Ver</a>
                                
                                <c:url value="/tienda/editar" var="urlEditar">
                                    <c:param name="id" value="${p.id}" />
                                </c:url>
                                <a href="${urlEditar}" class="btn btn-sm btn-warning">✏️ Editar</a>
                                
                                <form method="POST" action="${pageContext.request.contextPath}/tienda/eliminar" style="display: inline;" onsubmit="return confirm('¿Eliminar ${p.nombre}?')">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-sm btn-danger">🗑️ Eliminar</button>
                                </form>
                            </td> 
                        </tr> 
                    </c:forEach> 
                </tbody>
            </table> 
        </div>
  
        <nav class="mt-4">
            <c:url value="/tienda" var="urlInicio" /> 
            <a href="${urlInicio}" class="btn btn-outline-primary">← Volver al inicio</a> 
        </nav>
    </div>

    <!-- Modal de éxito al agregar producto -->
    <c:if test="${tipo == 'exito'}">
        <div class="modal fade" id="modalExito" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="modalExitoLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-success">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title" id="modalExitoLabel">✅ Producto Agregado Exitosamente</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="alert alert-success" role="alert">
                            <strong>${mensaje}</strong>
                        </div>
                        <div class="card mb-3">
                            <div class="card-body">
                                <p class="card-text"><strong>Nombre:</strong> <span class="text-primary">${nombreProducto}</span></p>
                                <p class="card-text"><strong>Precio:</strong> <span class="text-success">$${precioProducto}</span></p>
                            </div>
                        </div>
                        <p class="text-muted text-center">El producto ha sido agregado al catálogo y aparecerá en la tabla de productos.</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                var modalEl = document.getElementById('modalExito');
                if (modalEl) {
                    var myModal = new bootstrap.Modal(modalEl, {
                        backdrop: 'static',
                        keyboard: false
                    });
                    
                    // Limpiar el sombreado si el modal se cierra por cualquier motivo
                    modalEl.addEventListener('hidden.bs.modal', function () {
                        document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());
                        document.body.classList.remove('modal-open');
                        document.body.style.overflow = '';
                        document.body.style.paddingRight = '';
                    });

                    myModal.show();
                }
            });
        </script>
    </c:if>

    <!-- Alerta de error si algo falló -->
    <c:if test="${tipo == 'error'}">
        <div class="modal fade" id="modalError" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="modalErrorLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-danger">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="modalErrorLabel">❌ Error al Procesar</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="alert alert-danger" role="alert">
                            <p class="mb-0"><strong>${mensaje}</strong></p>
                        </div>
                        <p class="text-muted text-center mt-3">Hubo un problema al intentar guardar el producto. Por favor, revisa los datos e inténtalo de nuevo.</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-danger" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                var modalEl = document.getElementById('modalError');
                if (modalEl) {
                    var myModal = new bootstrap.Modal(modalEl, {
                        backdrop: 'static',
                        keyboard: false
                    });

                    // Limpiar el sombreado si el modal se cierra por cualquier motivo
                    modalEl.addEventListener('hidden.bs.modal', function () {
                        document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());
                        document.body.classList.remove('modal-open');
                        document.body.style.overflow = '';
                        document.body.style.paddingRight = '';
                    });

                    myModal.show();
                }
            });
        </script>
    </c:if>

    <%@ include file="footer.jsp" %>