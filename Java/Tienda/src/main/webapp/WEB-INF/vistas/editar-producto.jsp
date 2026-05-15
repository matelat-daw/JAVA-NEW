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
                <c:choose>
                    <c:when test="${producto != null}">
                        <div class="card">
                            <div class="card-body">
                                <form method="POST" action="${pageContext.request.contextPath}/tienda/actualizar">
                                    <input type="hidden" name="id" value="${producto.id}">
                                    
                                    <div class="mb-3">
                                        <label for="nombre" class="form-label">Nombre del Producto:</label>
                                        <input type="text" id="nombre" name="nombre" class="form-control" value="${producto.nombre}" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="precio" class="form-label">Precio (€):</label>
                                        <input type="number" id="precio" name="precio" class="form-control" value="${producto.precio}" step="0.01" min="0" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="categoria" class="form-label">Categoría:</label>
                                        <select id="categoria" name="categoria" class="form-select" required>
                                            <option value="">-- Seleccionar Categoría --</option>
                                            <c:forEach var="cat" items="${categorias}">
                                                <option value="${cat}" <c:if test="${producto.categoria == cat}">selected</c:if>>${cat}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="descripcion" class="form-label">Descripción:</label>
                                        <textarea id="descripcion" name="descripcion" class="form-control" rows="4" required>${producto.descripcion}</textarea>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="imagen" class="form-label">Nombre de la Imagen:</label>
                                        <select id="imagen" name="imagen" class="form-select" required>
                                            <option value="">-- Seleccionar Imagen --</option>
                                            <option value="combo.webp" <c:if test="${producto.imagen == 'combo.webp'}">selected</c:if>>combo.webp (Juego Teclado y Ratón)</option>
                                            <option value="mechanic.webp" <c:if test="${producto.imagen == 'mechanic.webp'}">selected</c:if>>mechanic.webp (Teclado Mecánico)</option>
                                            <option value="headset.webp" <c:if test="${producto.imagen == 'headset.webp'}">selected</c:if>>headset.webp (Auriculares con Micrófono)</option>
                                            <option value="monitor.webp" <c:if test="${producto.imagen == 'monitor.webp'}">selected</c:if>>monitor.webp (Monitor)</option>
                                            <option value="mouse.webp" <c:if test="${producto.imagen == 'mouse.webp'}">selected</c:if>>mouse.webp (Ratón)</option>
                                        </select>
                                    </div>
                                    
                                    <div class="d-flex gap-2">
                                        <button type="submit" class="btn btn-success flex-grow-1">✅ Guardar Cambios</button>
                                        <a href="${pageContext.request.contextPath}/tienda/catalogo" class="btn btn-secondary flex-grow-1">❌ Cancelar</a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-danger" role="alert">
                            <h4 class="alert-heading">⚠️ Error</h4>
                            <p>Producto no encontrado. Por favor, verifica el ID.</p>
                            <hr>
                            <a href="${pageContext.request.contextPath}/tienda/catalogo" class="btn btn-primary">Volver al catálogo</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <%@ include file="footer.jsp" %>
