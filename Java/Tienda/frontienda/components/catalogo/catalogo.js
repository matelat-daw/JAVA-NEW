import { escapeHtml, escapeJsString, loadTemplate, renderTemplate } from '../shared/template.js';

export async function catalogoHtml({ products, imgBaseUrl }) {
    const [tpl, cardTpl, rowTpl] = await Promise.all([
        loadTemplate(new URL('./catalogo.html', import.meta.url)),
        loadTemplate(new URL('./product-card.html', import.meta.url)),
        loadTemplate(new URL('./product-row.html', import.meta.url))
    ]);

    const productsMobile = products.map((p) => {
        return renderTemplate(cardTpl, {
            id: escapeHtml(p.id),
            nombre: escapeHtml(p.nombre),
            nombreJs: escapeJsString(p.nombre),
            categoria: escapeHtml(p.categoria),
            precio: Number(p.precio).toFixed(2),
            imagenSrc: `${imgBaseUrl}/${escapeHtml(p.imagen)}`
        });
    }).join('');

    const productsTable = products.map((p) => {
        return renderTemplate(rowTpl, {
            id: escapeHtml(p.id),
            nombre: escapeHtml(p.nombre),
            nombreJs: escapeJsString(p.nombre),
            categoria: escapeHtml(p.categoria),
            precio: Number(p.precio).toFixed(2),
            imagenSrc: `${imgBaseUrl}/${escapeHtml(p.imagen)}`
        });
    }).join('');

    return renderTemplate(tpl, { productsMobile, productsTable });
}
