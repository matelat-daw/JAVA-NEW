<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Contacto - ${contacto.nombreTienda}</title>
    <%@ include file="head.jsp" %>
</head>
<body>
    <div class="container py-5">
        <h1 class="mb-5 text-center text-primary">Contacto</h1>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty exito}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${exito}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <div class="row g-4 mb-5">
            <div class="col-md-6">
                <h2 class="text-primary mb-4">Información de la Tienda</h2>
                
                <div class="card mb-3">
                    <div class="card-body">
                        <h5 class="card-title text-success">🏪 Empresa</h5>
                        <p class="card-text"><c:out value="${contacto.nombreTienda}"/></p>
                    </div>
                </div>
                
                <div class="card mb-3">
                    <div class="card-body">
                        <h5 class="card-title text-success">📱 Teléfono</h5>
                        <p class="card-text"><c:out value="${contacto.telefono}"/></p>
                    </div>
                </div>
                
                <div class="card mb-3">
                    <div class="card-body">
                        <h5 class="card-title text-success">📧 Correo Electrónico</h5>
                        <p class="card-text"><c:out value="${contacto.email}"/></p>
                    </div>
                </div>
                
                <div class="card mb-3">
                    <div class="card-body">
                        <h5 class="card-title text-success">📍 Ubicación</h5>
                        <p class="card-text"><c:out value="${contacto.direccion}"/></p>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title text-success">⏰ Horario de Atención</h5>
                        <p class="card-text"><c:out value="${contacto.horario}"/></p>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <h2 class="text-primary mb-4">Envíanos un Mensaje</h2>
                <div class="card">
                    <div class="card-body">
                        <form method="POST" action="${pageContext.request.contextPath}/tienda/contacto">
                            <div class="mb-3">
                                <label for="nombre" class="form-label">Nombre:</label>
                                <input type="text" id="nombre" name="nombre" class="form-control" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="email" class="form-label">Email:</label>
                                <input type="email" id="email" name="email" class="form-control" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="asunto" class="form-label">Asunto:</label>
                                <input type="text" id="asunto" name="asunto" class="form-control" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="mensaje" class="form-label">Mensaje:</label>
                                <textarea id="mensaje" name="mensaje" class="form-control" rows="5" required></textarea>
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100">📤 Enviar Mensaje</button>
                        </form>
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