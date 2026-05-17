const API_BASE_URL = 'http://localhost:8080/api';
const IMG_BASE_URL = 'http://localhost:8080/api/imgs';

const app = document.getElementById('app');
const modalContainer = document.getElementById('modal-container');

let categoriasCache = null;

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

async function fetchCategorias() {
    if (Array.isArray(categoriasCache)) return categoriasCache;
    const res = await fetch(`${API_BASE_URL}/categorias`);
    if (!res.ok) throw new Error(`Error al cargar categorías (${res.status})`);
    const data = await res.json();
    categoriasCache = Array.isArray(data) ? data : [];
    return categoriasCache;
}

const BASE_PATH = (document.querySelector('base')?.getAttribute('href') || '/').replace(/\/$/, '');

function pathFor(route, params = {}) {
    switch (route) {
        case 'home':
            return `${BASE_PATH}/`;
        case 'catalogo':
            return `${BASE_PATH}/catalogo`;
        case 'nuevo':
            return `${BASE_PATH}/nuevo`;
        case 'resumen':
            return `${BASE_PATH}/resumen`;
        case 'contacto':
            return `${BASE_PATH}/contacto`;
        case 'detalle':
            return `${BASE_PATH}/detalle/${params.id}`;
        case 'editar':
            return `${BASE_PATH}/editar/${params.id}`;
        default:
            return `${BASE_PATH}/`;
    }
}

function routeFromLocation() {
    const pathname = window.location.pathname;
    const withoutBase = (BASE_PATH && pathname.startsWith(BASE_PATH))
        ? pathname.slice(BASE_PATH.length)
        : pathname;
    const clean = withoutBase.replace(/^\/+/, '').replace(/\/+$/, '');

    if (!clean) return { route: 'home', params: {} };

    const parts = clean.split('/');
    const route = parts[0];
    const id = parts[1];

    if (route === 'detalle' && id) return { route: 'detalle', params: { id } };
    if (route === 'editar' && id) return { route: 'editar', params: { id } };
    if (route === 'catalogo') return { route: 'catalogo', params: {} };
    if (route === 'nuevo') return { route: 'nuevo', params: {} };
    if (route === 'resumen') return { route: 'resumen', params: {} };
    if (route === 'contacto') return { route: 'contacto', params: {} };

    return { route: 'home', params: {} };
}

function navigate(route, params = {}) {
    const path = pathFor(route, params);
    window.history.pushState({ route, params }, '', path);
    render(route, params);
}

// Routes
async function render(route, params = {}) {
    app.innerHTML = '<div class="container text-center py-5"><div class="spinner-border text-primary" role="status"></div></div>';
    
    switch(route) {
        case 'home':
            renderHome();
            break;
        case 'catalogo':
            await renderCatalogo();
            break;
        case 'detalle':
            await renderDetalle(params.id);
            break;
        case 'nuevo':
            renderFormulario();
            break;
        case 'editar':
            await renderFormulario(params.id);
            break;
        case 'resumen':
            await renderResumen();
            break;
        case 'contacto':
            renderContacto();
            break;
        default:
            renderHome();
    }
}

// Views
function renderHome() {
    app.innerHTML = `
        <div class="container py-3 py-md-5">
            <div class="row justify-content-center">
                <div class="col-md-10 col-lg-8">
                    <div class="text-center mb-4">
                        <h1 class="mb-3 text-primary display-4 fw-bold">Tienda Tech</h1>
                        <p class="lead mb-4">Bienvenido a nuestra tienda de tecnología moderna.</p>
                        <div class="card bg-light border-0 p-4 mb-5 shadow-sm">
                            <p class="mb-2 text-muted">Frontend SPA (Vanilla JS + Bootstrap 5)</p>
                            <p class="mb-0 text-muted">Backend API (Spring Boot 4.0.6 + MariaDB)</p>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-12 col-md-4">
                            <a class="btn btn-primary btn-lg w-100 py-3" onclick="navigate('catalogo')">📦 Ver Catálogo</a>
                        </div>
                        <div class="col-12 col-md-4">
                            <a class="btn btn-success btn-lg w-100 py-3" onclick="navigate('resumen')">📊 Ver Resumen</a>
                        </div>
                        <div class="col-12 col-md-4">
                            <a class="btn btn-danger btn-lg w-100 py-3" onclick="navigate('contacto')">📧 Contacto</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function renderCatalogo() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        const products = await response.json();
        
        let html = `
            <div class="container py-5">
                <h1 class="mb-4 text-primary">📦 Catálogo de Productos</h1>
                <div class="mb-4 text-center text-md-start">
                    <button class="btn btn-success" onclick="navigate('nuevo')">➕ Agregar Nuevo Producto</button>
                </div>
                <div class="mobile-cards">
                    ${products.map(p => `
                        <div class="card mb-3 shadow-sm border-0">
                            <div class="row g-0">
                                <div class="col-4">
                                    <img src="${IMG_BASE_URL}/${p.imagen}" class="img-fluid rounded-start h-100" style="object-fit: cover;">
                                </div>
                                <div class="col-8">
                                    <div class="card-body py-2">
                                        <h5 class="card-title mb-1 text-truncate">${p.nombre}</h5>
                                        <p class="card-text mb-1">
                                            <span class="badge bg-info">${p.categoria}</span>
                                            <strong class="text-success ms-2">$${p.precio.toFixed(2)}</strong>
                                        </p>
                                        <div class="d-flex gap-1">
                                            <button class="btn btn-sm btn-primary py-1" onclick="navigate('detalle', {id: ${p.id}})">👁️</button>
                                            <button class="btn btn-sm btn-warning py-1" onclick="navigate('editar', {id: ${p.id}})">✏️</button>
                                            <button class="btn btn-sm btn-danger py-1" onclick="confirmarEliminacion(${p.id}, '${p.nombre}')">🗑️</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `).join('')}
                </div>
                <div class="table-responsive desktop-table">
                    <table class="table table-hover align-middle border shadow-sm">
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
                            ${products.map(p => `
                                <tr>
                                    <td><img src="${IMG_BASE_URL}/${p.imagen}" class="img-thumbnail" style="width: 70px; height: 70px; object-fit: cover;"></td>
                                    <td><span class="badge bg-secondary">${p.id}</span></td>
                                    <td><strong>${p.nombre}</strong></td>
                                    <td><span class="badge bg-info">${p.categoria}</span></td>
                                    <td><strong class="text-success">$${p.precio.toFixed(2)}</strong></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-primary" onclick="navigate('detalle', {id: ${p.id}})">👁️ Ver</button>
                                            <button class="btn btn-warning" onclick="navigate('editar', {id: ${p.id}})">✏️ Editar</button>
                                            <button class="btn btn-danger" onclick="confirmarEliminacion(${p.id}, '${p.nombre}')">🗑️ Eliminar</button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
        app.innerHTML = html;
    } catch (error) {
        app.innerHTML = '<div class="alert alert-danger">Error al cargar el catálogo</div>';
    }
}

async function renderDetalle(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/products/${id}`);
        const p = await response.json();
        
        app.innerHTML = `
            <div class="container py-3 py-md-5">
                <div class="row justify-content-center">
                    <div class="col-12 col-lg-10">
                        <nav aria-label="breadcrumb" class="mb-4">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#" onclick="navigate('catalogo')">Catálogo</a></li>
                                <li class="breadcrumb-item active">${p.nombre}</li>
                            </ol>
                        </nav>
                        <div class="card shadow-sm border-0">
                            <div class="card-body p-3 p-md-5">
                                <div class="row align-items-center">
                                    <div class="col-md-6 mb-4 mb-md-0">
                                        <div class="position-relative">
                                            <img src="${IMG_BASE_URL}/${p.imagen}" class="img-fluid rounded shadow-sm" style="width: 100%; height: auto; object-fit: cover;">
                                            <span class="badge bg-info position-absolute top-0 end-0 m-3 p-2 px-3">${p.categoria}</span>
                                        </div>
                                    </div>
                                    <div class="col-md-6 ps-md-5">
                                        <h1 class="mb-2 text-primary fw-bold">${p.nombre}</h1>
                                        <p class="text-muted mb-4 small">ID Producto: ${p.id}</p>
                                        <div class="mb-4">
                                            <h2 class="text-success display-5 fw-bold">$${p.precio.toFixed(2)}</h2>
                                        </div>
                                        <hr class="my-4">
                                        <div class="mb-4">
                                            <h5 class="fw-bold text-secondary text-uppercase small mb-3">Descripción</h5>
                                            <p>${p.descripcion}</p>
                                        </div>
                                        <div class="row g-2 mt-auto">
                                            <div class="col-12 col-sm-6">
                                                <button class="btn btn-warning btn-lg w-100" onclick="navigate('editar', {id: ${p.id}})">✏️ Editar</button>
                                            </div>
                                            <div class="col-12 col-sm-6">
                                                <button class="btn btn-outline-primary btn-lg w-100" onclick="navigate('catalogo')">← Catálogo</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        app.innerHTML = '<div class="alert alert-danger">Error al cargar el producto</div>';
    }
}

async function renderFormulario(id = null) {
    let p = { nombre: '', precio: 0, categoria: '', descripcion: '', imagen: '' };
    const isEdit = id !== null;
    
    if (isEdit) {
        try {
            const response = await fetch(`${API_BASE_URL}/products/${id}`);
            p = await response.json();
        } catch (error) {
            app.innerHTML = '<div class="alert alert-danger">Error al cargar datos</div>';
            return;
        }
    }

    let categorias = [];
    try {
        categorias = await fetchCategorias();
    } catch (e) {
        categorias = [];
    }

    const opcionesCategorias = [
        `<option value="" disabled ${!p.categoria ? 'selected' : ''}>Selecciona una categoría</option>`,
        ...categorias.map((c) => {
            const selected = p.categoria === c ? 'selected' : '';
            return `<option value="${escapeHtml(c)}" ${selected}>${escapeHtml(c)}</option>`;
        })
    ].join('');

    app.innerHTML = `
        <div class="container py-3 py-md-5">
            <div class="row justify-content-center">
                <div class="col-12 col-md-10 col-lg-8">
                    <div class="d-flex align-items-center mb-4">
                        <button class="btn btn-outline-secondary me-3 d-none d-md-inline-block" onclick="navigate('catalogo')">←</button>
                        <h1 class="mb-0 text-primary">${isEdit ? '✏️ Editar Producto' : '➕ Nuevo Producto'}</h1>
                    </div>
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-3 p-md-4">
                            <form id="productForm">
                                <input type="hidden" name="id" value="${id || ''}">
                                <div class="row">
                                    <div class="col-12 col-md-8">
                                        <div class="mb-3">
                                            <label class="form-label">Nombre del Producto:</label>
                                            <input type="text" class="form-control" name="nombre" value="${p.nombre}" required>
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <div class="mb-3">
                                            <label class="form-label">Precio ($):</label>
                                            <input type="number" class="form-control" name="precio" value="${p.precio}" step="0.01" required>
                                        </div>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Categoría:</label>
                                    <select class="form-select" name="categoria" required>
                                        ${opcionesCategorias}
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Descripción:</label>
                                    <textarea class="form-control" name="descripcion" rows="4" required>${p.descripcion}</textarea>
                                </div>
                                <div class="mb-4">
                                    <label class="form-label">Imagen del Producto:</label>
                                    ${isEdit && p.imagen ? `<div class="mb-2"><img src="${IMG_BASE_URL}/${p.imagen}" class="img-thumbnail" style="max-height: 100px;"></div>` : ''}
                                    <input type="file" class="form-control" name="imagen" accept="image/*" ${isEdit ? '' : 'required'}>
                                </div>
                                <div class="row g-2">
                                    <div class="col-12 col-md-6 order-md-2">
                                        <button type="submit" class="btn btn-success btn-lg w-100">✅ Guardar</button>
                                    </div>
                                    <div class="col-12 col-md-6 order-md-1">
                                        <button type="button" class="btn btn-outline-secondary btn-lg w-100" onclick="navigate('catalogo')">Cancelar</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.getElementById('productForm').onsubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const productData = {
            nombre: formData.get('nombre'),
            precio: parseFloat(formData.get('precio')),
            categoria: formData.get('categoria'),
            descripcion: formData.get('descripcion')
        };
        
        try {
            let response;
            if (isEdit) {
                response = await fetch(`${API_BASE_URL}/products/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(productData)
                });
            } else {
                response = await fetch(`${API_BASE_URL}/products`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(productData)
                });
            }
            
            const savedProduct = await response.json();
            const fileInput = formData.get('imagen');
            
            if (fileInput && fileInput.size > 0) {
                const imgFormData = new FormData();
                imgFormData.append('imagen', fileInput);
                await fetch(`${API_BASE_URL}/products/${savedProduct.id}/image`, {
                    method: 'POST',
                    body: imgFormData
                });
            }
            
            showSuccessModal(isEdit ? 'Actualizado' : 'Creado', savedProduct.nombre);
            navigate('catalogo');
        } catch (error) {
            alert('Error al guardar el producto');
        }
    };
}

async function renderResumen() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        const products = await response.json();
        
        const total = products.length;
        const categorias = [...new Set(products.map(p => p.categoria))];
        const avg = total > 0 ? products.reduce((acc, p) => acc + p.precio, 0) / total : 0;
        const max = total > 0 ? Math.max(...products.map(p => p.precio)) : 0;
        const min = total > 0 ? Math.min(...products.map(p => p.precio)) : 0;

        app.innerHTML = `
            <div class="container py-5">
                <h1 class="mb-5 text-center text-primary">📊 Resumen de la Tienda</h1>
                <div class="row g-4">
                    <div class="col-md-6 col-lg-4"><div class="card text-white bg-info"><div class="card-body text-center py-4"><h5>Total Productos</h5><p class="display-6 fw-bold">${total}</p></div></div></div>
                    <div class="col-md-6 col-lg-4"><div class="card text-white bg-success"><div class="card-body text-center py-4"><h5>Categorías</h5><p class="display-6 fw-bold">${categorias.length}</p></div></div></div>
                    <div class="col-md-6 col-lg-4"><div class="card text-white bg-warning"><div class="card-body text-center py-4"><h5>Precio Promedio</h5><p class="display-6 fw-bold">$${avg.toFixed(2)}</p></div></div></div>
                    <div class="col-md-6 col-lg-4"><div class="card text-white bg-danger"><div class="card-body text-center py-4"><h5>Más Caro</h5><p class="display-6 fw-bold">$${max.toFixed(2)}</p></div></div></div>
                    <div class="col-md-6 col-lg-4"><div class="card text-white bg-secondary"><div class="card-body text-center py-4"><h5>Más Barato</h5><p class="display-6 fw-bold">$${min.toFixed(2)}</p></div></div></div>
                </div>
            </div>
        `;
    } catch (error) {
        app.innerHTML = '<div class="alert alert-danger">Error al cargar resumen</div>';
    }
}

function renderContacto() {
    app.innerHTML = `
        <div class="container py-5">
            <h1 class="mb-5 text-center text-primary">📧 Contacto</h1>
            <div class="row g-4">
                <div class="col-md-6">
                    <div class="card p-4 shadow-sm">
                        <h5>Tienda Tech SPA</h5>
                        <p>Calle Principal 123, SPA City</p>
                        <p>Tel: +34 900 000 000</p>
                        <p>Email: spa@tiendatech.com</p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card p-4 shadow-sm">
                        <form onsubmit="event.preventDefault(); alert('Mensaje enviado!'); navigate('home');">
                            <div class="mb-3"><label class="form-label">Nombre</label><input type="text" class="form-control" required></div>
                            <div class="mb-3"><label class="form-label">Email</label><input type="email" class="form-control" required></div>
                            <div class="mb-3"><label class="form-label">Mensaje</label><textarea class="form-control" rows="3" required></textarea></div>
                            <button type="submit" class="btn btn-primary w-100">Enviar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Helpers
function confirmarEliminacion(id, nombre) {
    const modalHtml = `
        <div class="modal fade" id="deleteModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white"><h5>Confirmar Eliminación</h5><button class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body text-center py-4">
                        <p>¿Estás seguro de eliminar?</p>
                        <h4 class="fw-bold">${nombre}</h4>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-danger" onclick="deleteProduct(${id})">Eliminar</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    modalContainer.innerHTML = modalHtml;
    const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
    modal.show();
}

async function deleteProduct(id) {
    try {
        await fetch(`${API_BASE_URL}/products/${id}`, { method: 'DELETE' });
        bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
        await renderCatalogo();
    } catch (error) {
        alert('Error al eliminar');
    }
}

function showSuccessModal(accion, nombre) {
    const modalHtml = `
        <div class="modal fade" id="successModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-success">
                    <div class="modal-header bg-success text-white"><h5>✅ Éxito</h5><button class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body text-center py-4">
                        <p>Producto <strong>${nombre}</strong> ${accion} correctamente.</p>
                    </div>
                    <div class="modal-footer"><button class="btn btn-success" data-bs-dismiss="modal">Cerrar</button></div>
                </div>
            </div>
        </div>
    `;
    modalContainer.innerHTML = modalHtml;
    const modal = new bootstrap.Modal(document.getElementById('successModal'));
    modal.show();
}

window.addEventListener('popstate', () => {
    const { route, params } = routeFromLocation();
    render(route, params);
});

window.addEventListener('load', () => {
    const { route, params } = routeFromLocation();
    render(route, params);
});
