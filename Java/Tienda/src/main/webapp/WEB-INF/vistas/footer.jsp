</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Mostrar modal de éxito si existe
    document.addEventListener('DOMContentLoaded', function() {
        const modalExito = document.getElementById('modalExito');
        if (modalExito) {
            console.log('Modal encontrado, mostrando...');
            const modal = new bootstrap.Modal(modalExito);
            modal.show();
        }
    });
</script>
</html>
</script>
</html>
