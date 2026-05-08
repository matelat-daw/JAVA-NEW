// Configuración de la API
const API_BASE_URL = 'http://localhost:8088/api';

// Estado de la aplicación
const AppState = {
    currentUser: null,
    token: null,
    roles: []
};

// Inicialización de la aplicación
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar componentes de Bootstrap
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Cargar usuario actual si existe
    loadCurrentUser();

    // Manejar rutas
    window.addEventListener('hashchange', route);
    route();
});

// Cargar usuario actual desde localStorage
function loadCurrentUser() {
    const storedUser = localStorage.getItem('currentUser');
    const storedToken = localStorage.getItem('authToken');
    const storedRoles = localStorage.getItem('userRoles');

    if (storedUser && storedToken) {
        AppState.currentUser = JSON.parse(storedUser);
        AppState.token = storedToken;
        AppState.roles = storedRoles ? JSON.parse(storedRoles) : [];
        updateNavigation();
    }
}

// Actualizar navegación según el estado de autenticación
function updateNavigation() {
    const navLogin = document.getElementById('nav-login');
    const navRegister = document.getElementById('nav-register');
    const navLogout = document.getElementById('nav-logout');
    const navProfile = document.getElementById('nav-profile');
    const navAdmin = document.getElementById('nav-admin');

    if (AppState.currentUser && AppState.token) {
        // Usuario autenticado
        navLogin.style.display = 'none';
        navRegister.style.display = 'none';
        navLogout.style.display = 'list-item';
        navProfile.style.display = 'list-item';
        
        // Mostrar enlace admin si tiene rol ADMIN
        if (AppState.roles.includes('ROLE_ADMIN')) {
            navAdmin.style.display = 'list-item';
        } else {
            navAdmin.style.display = 'none';
        }
    } else {
        // Usuario no autenticado
        navLogin.style.display = 'list-item';
        navRegister.style.display = 'list-item';
        navLogout.style.display = 'none';
        navProfile.style.display = 'none';
        navAdmin.style.display = 'none';
    }
}

// Manejo de rutas
function route() {
    const hash = window.location.hash || '#/login';
    const path = hash.replace('#/', '');
    const contentDiv = document.getElementById('content');

    contentDiv.innerHTML = ''; // Limpiar contenido

    switch (path) {
        case 'login':
            contentDiv.innerHTML = LoginView.render();
            LoginView.init();
            break;
        case 'register':
            contentDiv.innerHTML = RegisterView.render();
            RegisterView.init();
            break;
        case 'logout':
            handleLogout();
            window.location.hash = '#/login';
            break;
        case 'profile':
            if (!AppState.currentUser) {
                window.location.hash = '#/login';
                return;
            }
            contentDiv.innerHTML = ProfileView.render(AppState.currentUser);
            ProfileView.init();
            break;
        case 'admin':
            if (!AppState.currentUser || !AppState.roles.includes('ROLE_ADMIN')) {
                window.location.hash = '#/login';
                return;
            }
            contentDiv.innerHTML = AdminView.render();
            AdminView.init();
            break;
        default:
            contentDiv.innerHTML = LoginView.render();
            LoginView.init();
    }
}

// Manejar logout
function handleLogout() {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRoles');
    AppState.currentUser = null;
    AppState.token = null;
    AppState.roles = [];
    updateNavigation();
    window.location.hash = '#/login';
}

// Mostrar alerta
function showAlert(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.getElementById('content').prepend(alertDiv);
}

// Mostrar alerta de error
function showError(message) {
    showAlert(message, 'danger');
}

// Mostrar alerta de éxito
function showSuccess(message) {
    showAlert(message, 'success');
}

// Realizar petición HTTP
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (AppState.token) {
        headers['Authorization'] = `Bearer ${AppState.token}`;
    }

    try {
        const response = await fetch(url, {
            ...options,
            headers
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.message || `Error: ${response.status}`);
        }

        return data;
    } catch (error) {
        showError(error.message);
        throw error;
    }
}

// Vistas
const LoginView = {
    render: function() {
        return `
            <div class="card mt-5">
                <div class="card-header">
                    <h3 class="mb-0">Login</h3>
                </div>
                <div class="card-body">
                    <form id="login-form">
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" id="username" name="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Login</button>
                    </form>
                    <div class="mt-3 text-center">
                        <p class="mb-0">Don't have an account? <a href="#/register">Register here</a></p>
                    </div>
                </div>
            </div>
        `;
    },

    init: function() {
        const form = document.getElementById('login-form');
        if (form) {
            form.addEventListener('submit', async function(e) {
                e.preventDefault();
                
                const username = document.getElementById('username').value;
                const password = document.getElementById('password').value;

                try {
                    const response = await apiRequest('/auth/signin', {
                        method: 'POST',
                        body: JSON.stringify({ username, password })
                    });

                    // Guardar datos del usuario
                    AppState.token = response.jwt;
                    AppState.currentUser = {
                        id: response.id,
                        username: response.username,
                        email: response.email
                    };
                    AppState.roles = response.roles;

                    // Guardar en localStorage
                    localStorage.setItem('currentUser', JSON.stringify(AppState.currentUser));
                    localStorage.setItem('authToken', AppState.token);
                    localStorage.setItem('userRoles', JSON.stringify(AppState.roles));

                    updateNavigation();
                    showSuccess('Login successful!');
                    
                    // Redirigir a profile
                    window.location.hash = '#/profile';
                } catch (error) {
                    showError('Invalid username or password');
                }
            });
        }
    }
};

const RegisterView = {
    render: function() {
        return `
            <div class="card mt-5">
                <div class="card-header">
                    <h3 class="mb-0">Register</h3>
                </div>
                <div class="card-body">
                    <form id="register-form">
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" id="username" name="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Confirm Password</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Register</button>
                    </form>
                    <div class="mt-3 text-center">
                        <p class="mb-0">Already have an account? <a href="#/login">Login here</a></p>
                    </div>
                </div>
            </div>
        `;
    },

    init: function() {
        const form = document.getElementById('register-form');
        if (form) {
            form.addEventListener('submit', async function(e) {
                e.preventDefault();
                
                const username = document.getElementById('username').value;
                const email = document.getElementById('email').value;
                const password = document.getElementById('password').value;
                const confirmPassword = document.getElementById('confirmPassword').value;

                if (password !== confirmPassword) {
                    showError('Passwords do not match');
                    return;
                }

                try {
                    const response = await apiRequest('/auth/signup', {
                        method: 'POST',
                        body: JSON.stringify({ username, email, password })
                    });

                    showSuccess('User registered successfully! Please login.');
                    setTimeout(() => {
                        window.location.hash = '#/login';
                    }, 2000);
                } catch (error) {
                    // No mostrar error si ya existe, solo mostrar mensaje
                    if (error.message.includes('already')) {
                        showError(error.message);
                    }
                }
            });
        }
    }
};

const ProfileView = {
    render: function(user) {
        return `
            <div class="card mt-5">
                <div class="card-header">
                    <h3 class="mb-0">Profile</h3>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <strong>ID:</strong> ${user.id}
                    </div>
                    <div class="mb-3">
                        <strong>Username:</strong> ${user.username}
                    </div>
                    <div class="mb-3">
                        <strong>Email:</strong> ${user.email}
                    </div>
                    <div class="mb-3">
                        <strong>Roles:</strong>
                        <ul class="list-group list-group-flush">
                            ${AppState.roles.map(role => `<li class="list-group-item">${role}</li>`).join('')}
                        </ul>
                    </div>
                    <div class="d-flex gap-2">
                        <button class="btn btn-secondary" onclick="window.location.hash='#/profile/edit'">Edit Profile</button>
                        <button class="btn btn-danger" onclick="handleLogout()">Logout</button>
                    </div>
                </div>
            </div>
        `;
    },

    init: function() {
        // Inicialización del perfil
    }
};

const AdminView = {
    render: function() {
        return `
            <div class="card mt-5">
                <div class="card-header">
                    <h3 class="mb-0">Admin Panel</h3>
                </div>
                <div class="card-body">
                    <p>Welcome, Admin!</p>
                    <div class="alert alert-info">
                        This is the admin panel. Only users with ADMIN role can access this page.
                    </div>
                    <button class="btn btn-danger" onclick="handleLogout()">Logout</button>
                </div>
            </div>
        `;
    },

    init: function() {
        // Inicialización del panel de administrador
    }
};