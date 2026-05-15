<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Resumen - Tienda</title>
    <%@ include file="head.jsp" %>
</head>
<body>
    <div class="container py-5">
        <h1 class="mb-5 text-center text-primary">Resumen de la Tienda</h1>
        
        <div class="row g-4 mb-5">
            <div class="col-md-6">
                <div class="card text-white bg-info">
                    <div class="card-body text-center py-5">
                        <h5 class="card-title">Total de Productos</h5>
                        <p class="card-text" style="font-size: 2.5rem; font-weight: bold;"><c:out value="${resumen.totalProductos}"/></p>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="card text-white bg-success">
                    <div class="card-body text-center py-5">
                        <h5 class="card-title">Categorías Disponibles</h5>
                        <p class="card-text" style="font-size: 2.5rem; font-weight: bold;"><c:out value="${resumen.totalCategorias}"/></p>
                    </div>
                </div>
            </div>
            
            <div class="col-12">
                <div class="card text-white bg-danger">
                    <div class="card-body text-center py-5">
                        <h5 class="card-title">Producto Más Caro</h5>
                        <p class="card-text" style="font-size: 2rem; font-weight: bold;"><c:out value="${resumen.productoMasCaro}"/></p>
                        <p class="card-text" style="font-size: 1.5rem;"><c:out value="Precio: "/>${resumen.precioProductoMasCaro} €</p>
                    </div>
                </div>
            </div>
        </div>
        
        <nav class="mt-4">
            <c:url value="/tienda" var="urlInicio" /> 
            <a href="${urlInicio}" class="btn btn-outline-primary">← Volver al inicio</a>
        </nav>
    </div>
    <%@ include file="footer.jsp" %>