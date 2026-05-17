import { spinnerHtml } from '../components/shared/spinner.js';
import { homeHtml } from '../components/home/home.js';
import { catalogoHtml } from '../components/catalogo/catalogo.js';
import { detalleHtml } from '../components/detalle/detalle.js';
import { productoFormHtml } from '../components/producto-form/form.js';
import { resumenHtml } from '../components/resumen/resumen.js';
import { contactoHtml } from '../components/contacto/contacto.js';
import { deleteModalHtml, successModalHtml } from '../components/shared/modals.js';
import { escapeHtml } from '../components/shared/template.js';

const API_BASE_URL = 'http://localhost:8080/api';
const IMG_BASE_URL = 'http://localhost:8080/api/imgs';

const app = document.getElementById('app');
const modalContainer = document.getElementById('modal-container');

let categoriasCache = null;

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
            if (params.page && Number(params.page) > 1) {
                return `${BASE_PATH}/catalogo?page=${encodeURIComponent(String(params.page))}`;
            }
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
    if (route === 'catalogo') {
        const page = Number(new URLSearchParams(window.location.search).get('page')) || 1;
        return { route: 'catalogo', params: { page } };
    }
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
    app.innerHTML = await spinnerHtml();
    
    switch(route) {
        case 'home':
            await renderHome();
            break;
        case 'catalogo':
            await renderCatalogo(params.page);
            break;
        case 'detalle':
            await renderDetalle(params.id);
            break;
        case 'nuevo':
            await renderFormulario();
            break;
        case 'editar':
            await renderFormulario(params.id);
            break;
        case 'resumen':
            await renderResumen();
            break;
        case 'contacto':
            await renderContacto();
            break;
        default:
            await renderHome();
    }
}

// Views
async function renderHome() {
    app.innerHTML = await homeHtml();
}

async function renderCatalogo(page = 1) {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        const products = await response.json();
        app.innerHTML = await catalogoHtml({ products, imgBaseUrl: IMG_BASE_URL, page, pageSize: 8 });
    } catch (error) {
        app.innerHTML = '<div class="alert alert-danger">Error al cargar el catálogo</div>';
    }
}

async function renderDetalle(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/products/${id}`);
        const p = await response.json();
        app.innerHTML = await detalleHtml({ producto: p, imgBaseUrl: IMG_BASE_URL });
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

    app.innerHTML = await productoFormHtml({
        producto: p,
        isEdit,
        id,
        opcionesCategoriasHtml: opcionesCategorias,
        imgBaseUrl: IMG_BASE_URL
    });

    document.getElementById('productForm').onsubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const imagenActual = (formData.get('imagenActual') || '').toString();
        const productData = {
            nombre: formData.get('nombre'),
            precio: parseFloat(formData.get('precio')),
            categoria: formData.get('categoria'),
            descripcion: formData.get('descripcion')
        };

        if (isEdit && imagenActual.trim()) {
            productData.imagen = imagenActual.trim();
        }
        
        try {
            let response;
            if (isEdit) {
                response = await fetch(`${API_BASE_URL}/products/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(productData)
                });
            } else {
                const fileInputCreate = formData.get('imagen');
                if (!fileInputCreate || !fileInputCreate.size) {
                    alert('Selecciona una imagen para crear el producto');
                    return;
                }
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

        app.innerHTML = await resumenHtml({
            total,
            categoriasCount: categorias.length,
            avg,
            max,
            min
        });
    } catch (error) {
        app.innerHTML = '<div class="alert alert-danger">Error al cargar resumen</div>';
    }
}

async function renderContacto() {
    app.innerHTML = await contactoHtml();
}

// Helpers
async function confirmarEliminacion(id, nombre) {
    const modalHtml = await deleteModalHtml({ id, nombre });
    modalContainer.innerHTML = modalHtml;
    const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
    modal.show();
}

async function deleteProduct(id) {
    try {
        await fetch(`${API_BASE_URL}/products/${id}`, { method: 'DELETE' });
        bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
        const { params } = routeFromLocation();
        await renderCatalogo(params.page);
    } catch (error) {
        alert('Error al eliminar');
    }
}

async function showSuccessModal(accion, nombre) {
    const modalHtml = await successModalHtml({ accion, nombre });
    modalContainer.innerHTML = modalHtml;
    const modal = new bootstrap.Modal(document.getElementById('successModal'));
    modal.show();
}

window.navigate = navigate;
window.confirmarEliminacion = confirmarEliminacion;
window.deleteProduct = deleteProduct;

window.addEventListener('popstate', () => {
    const { route, params } = routeFromLocation();
    render(route, params);
});

window.addEventListener('load', () => {
    const { route, params } = routeFromLocation();
    render(route, params);
});
