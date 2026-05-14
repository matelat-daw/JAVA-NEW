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
    <%@ include file="footer.jsp" %>