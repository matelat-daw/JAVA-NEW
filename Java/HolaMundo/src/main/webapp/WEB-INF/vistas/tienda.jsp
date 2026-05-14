<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ taglib prefix="c" uri="jakarta.tags.core" %> 
<!DOCTYPE html> 
<html lang="es"> 
<head> 
    <title>${nombreTienda}</title>
    <%@ include file="head.jsp" %>
</head> 
<body> 
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <h1 class="mb-4 text-center text-primary"><c:out value="${nombreTienda}"/></h1> 
                <p class="text-center lead mb-5"><c:out value="${mensaje}"/></p> 
                <p class="text-center text-muted mb-4">Esta página no está escrita con out.println().</p> 
                <p class="text-center text-muted mb-5">El Servlet prepara los datos y el JSP los muestra.</p> 
  
                <c:url value="/tienda/catalogo" var="urlCatalogo" /> 
                <c:url value="/tienda/resumen" var="urlResumen" /> 
                <c:url value="/tienda/contacto" var="urlContacto" />
                <c:url value="/hola" var="urlSaludo" />
                
                <div class="d-grid gap-3">
                    <a class="btn btn-primary btn-lg" href="${urlCatalogo}">📦 Ver Catálogo</a> 
                    <a class="btn btn-success btn-lg" href="${urlResumen}">📊 Ver Resumen</a> 
                    <a class="btn btn-danger btn-lg" href="${urlContacto}">📧 Contacto</a>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="footer.jsp" %>