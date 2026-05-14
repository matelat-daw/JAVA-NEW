<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ taglib prefix="c" uri="jakarta.tags.core" %> 
<!DOCTYPE html> 
<html lang="es"> 
<head> 
    <title>Detalle del producto</title>
    <%@ include file="head.jsp" %>
</head> 
<body> 
    <div class="container py-5">
        <c:choose> 
            <c:when test="${not empty producto}">
                <div class="row g-5">
                    <!-- Sección de Imagen -->
                    <div class="col-md-6">
                        <img src="${pageContext.request.contextPath}/recursos/imgs/${producto.imagen}" 
                             alt="${producto.nombre}" 
                             class="img-fluid rounded" style="width: 100%; max-width: 500px;">
                    </div>
                    
                    <!-- Sección de Datos -->
                    <div class="col-md-6">
                        <p class="text-muted">ID: <span class="badge bg-secondary">${producto.id}</span></p>
                        <h1 class="display-5 mb-3 text-primary"><c:out value="${producto.nombre}"/></h1>
                        
                        <span class="badge bg-success mb-3 p-2" style="font-size: 1rem;"><c:out value="${producto.categoria}"/></span>
                        
                        <div class="alert alert-warning" role="alert">
                            <strong>Precio: $${producto.precio}</strong>
                        </div>
                        
                        <div class="card mb-4">
                            <div class="card-body">
                                <h5 class="card-title">Descripción</h5>
                                <p class="card-text"><c:out value="${producto.descripcion}"/></p>
                            </div>
                        </div>
                        
                        <div class="d-flex gap-2">
                            <c:url value="/tienda/editar" var="urlEditar">
                                <c:param name="id" value="${producto.id}" />
                            </c:url>
                            <a href="${urlEditar}" class="btn btn-warning btn-lg">✏️ Editar Producto</a>
                            
                            <c:url value="/tienda/catalogo" var="urlCatalogo" />
                            <a href="${urlCatalogo}" class="btn btn-outline-primary btn-lg">← Volver al Catálogo</a>
                        </div>
                    </div>
                </div>
            </c:when> 
  
            <c:otherwise>
                <div class="alert alert-danger" role="alert">
                    <h4 class="alert-heading">⚠️ Producto No Encontrado</h4> 
                    <p>No existe ningún producto con ese identificador.</p>
                    <hr>
                    <c:url value="/tienda/catalogo" var="urlCatalogo" />
                    <a href="${urlCatalogo}" class="btn btn-primary">← Volver al Catálogo</a>
                </div>
            </c:otherwise> 
        </c:choose> 
    </div>
    <%@ include file="footer.jsp" %>