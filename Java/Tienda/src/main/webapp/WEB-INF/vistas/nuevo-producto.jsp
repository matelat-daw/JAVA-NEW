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
        <h1 class="mb-4 text-primary"><c:out value="${titulo}"/></h1>
        
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <form method="POST" action="${pageContext.request.contextPath}/tienda/guardar" enctype="multipart/form-data">
                            
                            <div class="mb-3">
                                <label for="nombre" class="form-label">Nombre del Producto:</label>
                                <input type="text" id="nombre" name="nombre" class="form-control" placeholder="Ej: Monitor 4K" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="precio" class="form-label">Precio (€):</label>
                                <input type="number" id="precio" name="precio" class="form-control" placeholder="Ej: 299.99" step="0.01" min="0" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="categoria" class="form-label">Categoría:</label>
                                <select id="categoria" name="categoria" class="form-select" required>
                                    <option value="">-- Seleccionar Categoría --</option>
                                    <c:forEach var="cat" items="${categorias}">
                                        <option value="${cat}">${cat}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="descripcion" class="form-label">Descripción:</label>
                                <textarea id="descripcion" name="descripcion" class="form-control" rows="4" placeholder="Describe el producto..." required></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label for="imagen" class="form-label">Imagen del Producto:</label>
                                <input type="file" id="imagen" name="imagen" class="form-control" accept=".webp,.jpg,.jpeg,.png" required>
                                <small class="text-muted">Formatos permitidos: .webp, .jpg, .jpeg, .png</small>
                            </div>
                            
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-success flex-grow-1">✅ Agregar Producto</button>
                                <a href="${pageContext.request.contextPath}/tienda/catalogo" class="btn btn-secondary flex-grow-1">❌ Cancelar</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="footer.jsp" %>
