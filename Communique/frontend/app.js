// Communique Frontend v3.0 - Moderno y con WebSockets
const app = {
    state: {
        userId: null,
        username: null,
        serverUrl: null,
        wsUrl: null,
        socket: null,
        targetUser: null,
        activeCallUserId: null,
        users: [],
        messages: {}, // userId -> [messages]
        unreadCounts: {}, // userId -> count
        isConnected: false,
        userRefreshInterval: null,
        heartbeatInterval: null,
        
        // WebRTC State
        localStream: null,
        peerConnection: null,
        pendingOffer: null,
        iceServers: {
            iceServers: [
                { urls: 'stun:stun.l.google.com:19302' },
                { urls: 'stun:stun1.l.google.com:19302' }
            ]
        }
    },

    init() {
        console.log('🚀 Iniciando Communique Frontend v3.0...');
        this.attachEventListeners();
        this.loadServerUrlFromStorage();
    },

    attachEventListeners() {
        const usernameInput = document.getElementById('username');
        const chatInput = document.getElementById('chatInput');
        
        if (usernameInput) {
            usernameInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') this.register();
            });
        }
        
        if (chatInput) {
            chatInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.sendMessage();
                }
            });
        }
    },

    loadServerUrlFromStorage() {
        try {
            const saved = localStorage.getItem('serverUrl');
            if (saved) {
                const input = document.getElementById('serverUrl');
                if (input) input.value = saved;
            }
        } catch (e) {
            console.warn('No se pudo cargar URL del storage');
        }
    },

    async register() {
        try {
            const username = document.getElementById('username').value.trim();
            const serverUrl = document.getElementById('serverUrl').value.trim();
            const errorDiv = document.getElementById('registerError');
            const successDiv = document.getElementById('registerSuccess');

            if (!username) {
                this.showError('Ingresa un usuario', errorDiv);
                return;
            }

            if (!serverUrl) {
                this.showError('Ingresa la URL del servidor', errorDiv);
                return;
            }

            errorDiv.style.display = 'none';
            successDiv.innerHTML = '⏳ Conectando...';
            successDiv.style.display = 'block';

            // Guardar configuración
            this.state.serverUrl = serverUrl;
            this.state.wsUrl = serverUrl.replace('http', 'ws') + '/ws';
            this.state.username = username;
            localStorage.setItem('serverUrl', serverUrl);

            // Registrar en servidor via REST
            const response = await fetch(`${serverUrl}/api/users/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: username,
                    hostname: window.location.hostname
                })
            });

            if (!response.ok) throw new Error(`Error ${response.status}`);

            const userData = await response.json();
            this.state.userId = userData.id;
            
            // Conectar WebSocket
            this.connectWebSocket();

            // Mostrar UI principal
            this.showMainPanel();
            this.updateUserInfo(userData);

            // Iniciar intervalos
            this.refreshOnlineUsers();
            this.state.userRefreshInterval = setInterval(() => this.refreshOnlineUsers(), 5000);
            this.state.heartbeatInterval = setInterval(() => this.sendHeartbeat(), 30000);

            successDiv.innerHTML = '✅ Conectado!';
        } catch (error) {
            console.error('Error en registro:', error);
            this.showError('Error: ' + error.message, document.getElementById('registerError'));
            document.getElementById('registerSuccess').style.display = 'none';
        }
    },

    connectWebSocket() {
        try {
            this.state.socket = new WebSocket(this.state.wsUrl);

            this.state.socket.onopen = () => {
                this.state.isConnected = true;
                this.updateConnectionStatus('online');
                
                // Registrar sesión en WS
                this.state.socket.send(JSON.stringify({
                    type: 'register',
                    userId: this.state.userId
                }));
            };

            this.state.socket.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    this.handleWebSocketMessage(data);
                } catch (e) {
                    console.error('Error procesando mensaje WS:', e);
                }
            };

            this.state.socket.onclose = () => {
                this.state.isConnected = false;
                this.updateConnectionStatus('offline');
                // Reintentar conexión en 5s
                setTimeout(() => this.connectWebSocket(), 5000);
            };

        } catch (error) {
            console.error('Error conectando WebSocket:', error);
        }
    },

    handleWebSocketMessage(data) {
        switch (data.type) {
            case 'chat-message':
                this.onReceiveMessage(data.sourceUserId, data['chat-message']);
                break;
            case 'users-online':
                this.updateUsersList(data.users);
                break;
            case 'offer':
                this.handleIncomingOffer(data.sourceUserId, data.offer);
                break;
            case 'answer':
                this.handleIncomingAnswer(data.answer);
                break;
            case 'ice-candidate':
                this.handleIncomingIceCandidate(data.candidate);
                break;
            case 'hangup':
                this.onRemoteHangup();
                break;
        }
    },

    // --- Lógica WebRTC ---

    async startCall() {
        if (!this.state.targetUser) return;
        
        console.log('📞 Iniciando llamada a:', this.state.targetUser.username);
        this.state.activeCallUserId = this.state.targetUser.id;
        
        try {
            // 1. Obtener audio local con restricciones compatibles
            const constraints = { 
                audio: {
                    echoCancellation: true,
                    noiseSuppression: true,
                    autoGainControl: true
                } 
            };
            this.state.localStream = await navigator.mediaDevices.getUserMedia(constraints);
            console.log('🎙️ Micrófono accedido correctamente');
            
            // 2. Crear PeerConnection
            this.createPeerConnection(this.state.activeCallUserId);
            
            // 3. Añadir tracks
            this.state.localStream.getTracks().forEach(track => {
                this.state.peerConnection.addTrack(track, this.state.localStream);
            });
            
            // 4. Crear Oferta
            const offer = await this.state.peerConnection.createOffer();
            await this.state.peerConnection.setLocalDescription(offer);
            
            // 5. Enviar Oferta
            this.state.socket.send(JSON.stringify({
                type: 'offer',
                targetUserId: this.state.activeCallUserId,
                offer: offer
            }));
            
            this.updateCallUI('calling');
        } catch (e) {
            console.error('Error al iniciar llamada:', e);
            this.showMicError(e);
            this.state.activeCallUserId = null;
        }
    },

    showMicError(err) {
        let msg = 'No se puede acceder al micrófono.';
        if (err.name === 'NotAllowedError') msg = 'Permiso denegado. Por favor, activa el micrófono en los ajustes del navegador.';
        if (err.name === 'NotFoundError') msg = 'No se encontró ningún micrófono conectado.';
        if (window.location.protocol !== 'https:' && window.location.hostname !== 'localhost') {
            msg += '\n\n⚠️ Estás en una conexión no segura (HTTP). Los navegadores bloquean el micrófono por seguridad.';
        }
        alert('❌ Error de Micrófono: ' + msg);
    },

    async handleIncomingOffer(sourceUserId, offer) {
        console.log('📞 Oferta recibida de:', sourceUserId);
        this.state.pendingOffer = { sourceUserId, offer };
        this.state.activeCallUserId = sourceUserId;
        
        const caller = this.state.users.find(u => u.id === sourceUserId) || { username: 'Usuario Desconocido' };
        document.getElementById('callerName').innerText = caller.username;
        document.getElementById('incomingCallModal').style.display = 'flex';
    },

    async acceptCall() {
        document.getElementById('incomingCallModal').style.display = 'none';
        if (!this.state.pendingOffer) return;
        
        const { sourceUserId, offer } = this.state.pendingOffer;
        
        try {
            const constraints = { audio: true };
            this.state.localStream = await navigator.mediaDevices.getUserMedia(constraints);
            
            this.createPeerConnection(sourceUserId);
            
            this.state.localStream.getTracks().forEach(track => {
                this.state.peerConnection.addTrack(track, this.state.localStream);
            });
            
            await this.state.peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
            const answer = await this.state.peerConnection.createAnswer();
            await this.state.peerConnection.setLocalDescription(answer);
            
            this.state.socket.send(JSON.stringify({
                type: 'answer',
                targetUserId: sourceUserId,
                answer: answer
            }));
            
            this.updateCallUI('active');
        } catch (e) {
            console.error('Error al aceptar llamada:', e);
            this.showMicError(e);
            this.rejectCall();
        }
    },

    rejectCall() {
        console.log('✖️ Llamada rechazada/cortada');
        document.getElementById('incomingCallModal').style.display = 'none';
        
        if (this.state.activeCallUserId && this.state.socket) {
            this.state.socket.send(JSON.stringify({
                type: 'hangup',
                targetUserId: this.state.activeCallUserId
            }));
        }
        
        this.cleanupCall();
        this.updateCallUI('idle');
    },

    async handleIncomingAnswer(answer) {
        console.log('✅ Respuesta recibida');
        if (this.state.peerConnection) {
            try {
                await this.state.peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
                this.updateCallUI('active');
            } catch (e) {
                console.error('Error al establecer descripción remota:', e);
            }
        }
    },

    async handleIncomingIceCandidate(candidate) {
        if (this.state.peerConnection && candidate) {
            try {
                await this.state.peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
            } catch (e) {
                console.warn('Error añadiendo ICE candidate (ignorable):', e);
            }
        }
    },

    createPeerConnection(targetUserId) {
        if (this.state.peerConnection) this.state.peerConnection.close();
        
        this.state.peerConnection = new RTCPeerConnection(this.state.iceServers);
        
        this.state.peerConnection.onicecandidate = (event) => {
            if (event.candidate && this.state.socket) {
                this.state.socket.send(JSON.stringify({
                    type: 'ice-candidate',
                    targetUserId: targetUserId,
                    candidate: event.candidate
                }));
            }
        };
        
        this.state.peerConnection.ontrack = (event) => {
            console.log('🎵 Recibiendo audio remoto');
            const remoteAudio = document.getElementById('remoteAudio');
            if (remoteAudio) {
                remoteAudio.srcObject = event.streams[0];
                // Asegurar que el audio se reproduce
                remoteAudio.play().catch(e => console.warn('Auto-play bloqueado:', e));
            }
        };
        
        this.state.peerConnection.onconnectionstatechange = () => {
            console.log('Connection State:', this.state.peerConnection.connectionState);
            if (['disconnected', 'failed', 'closed'].includes(this.state.peerConnection.connectionState)) {
                // No colgar inmediatamente, dar un margen por si es micro-corte
                setTimeout(() => {
                    if (['disconnected', 'failed'].includes(this.state.peerConnection.connectionState)) {
                        this.onRemoteHangup();
                    }
                }, 3000);
            }
        };
    },

    endCall() {
        console.log('🛑 Finalizando llamada localmente');
        if (this.state.activeCallUserId && this.state.socket) {
            this.state.socket.send(JSON.stringify({
                type: 'hangup',
                targetUserId: this.state.activeCallUserId
            }));
        }
        this.cleanupCall();
        this.updateCallUI('idle');
    },

    onRemoteHangup() {
        console.log('📞 Llamada terminada por el otro usuario');
        this.cleanupCall();
        this.updateCallUI('idle');
    },

    cleanupCall() {
        if (this.state.localStream) {
            this.state.localStream.getTracks().forEach(track => track.stop());
            this.state.localStream = null;
        }
        if (this.state.peerConnection) {
            this.state.peerConnection.close();
            this.state.peerConnection = null;
        }
        const remoteAudio = document.getElementById('remoteAudio');
        if (remoteAudio) remoteAudio.srcObject = null;
        this.state.pendingOffer = null;
        this.state.activeCallUserId = null;
    },

    updateCallUI(status) {
        const callBtn = document.getElementById('callBtn');
        const hangupBtn = document.getElementById('hangupBtn');
        const statusArea = document.getElementById('callStatus');
        const statusText = document.getElementById('callStatusText');
        
        switch (status) {
            case 'calling':
                callBtn.style.display = 'none';
                hangupBtn.style.display = 'flex';
                statusArea.style.display = 'flex';
                statusText.innerText = 'Llamando...';
                break;
            case 'active':
                callBtn.style.display = 'none';
                hangupBtn.style.display = 'flex';
                statusArea.style.display = 'flex';
                statusText.innerText = 'En llamada';
                break;
            case 'idle':
                callBtn.style.display = 'flex';
                hangupBtn.style.display = 'none';
                statusArea.style.display = 'none';
                break;
        }
    },

    async refreshOnlineUsers() {
        try {
            const response = await fetch(`${this.state.serverUrl}/api/users`);
            if (response.ok) {
                const users = await response.json();
                this.updateUsersList(users);
            }
        } catch (e) {
            console.warn('Error refrescando usuarios:', e);
        }
    },

    updateUsersList(users) {
        this.state.users = users.filter(u => u.id !== this.state.userId);
        const usersList = document.getElementById('usersList');
        const onlineCount = document.getElementById('onlineCount');
        
        onlineCount.innerText = this.state.users.length;
        
        if (this.state.users.length === 0) {
            usersList.innerHTML = '<div class="empty-users">No hay nadie más en línea</div>';
            return;
        }

        usersList.innerHTML = this.state.users.map(user => {
            const unreadCount = this.state.unreadCounts[user.id] || 0;
            const badge = unreadCount > 0 ? `<span class="msg-badge">${unreadCount}</span>` : '';
            
            return `
                <div class="user-item ${this.state.targetUser?.id === user.id ? 'active' : ''}" 
                     onclick="app.selectUser('${user.id}')">
                    <div class="avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="info">
                        <h5>${user.username} ${badge}</h5>
                        <p>${user.hostname || 'Desconocido'}</p>
                    </div>
                </div>
            `;
        }).join('');
    },

    async selectUser(userId) {
        const user = this.state.users.find(u => u.id === userId);
        if (!user) return;

        this.state.targetUser = user;
        
        // Limpiar contador al seleccionar
        this.state.unreadCounts[userId] = 0;
        
        // Actualizar UI
        document.getElementById('noChatSelected').style.display = 'none';
        document.getElementById('chatContainer').style.display = 'flex';
        document.getElementById('targetUserName').innerText = user.username;
        document.getElementById('chatInput').disabled = false;
        document.getElementById('sendBtn').disabled = false;

        // Resaltar en lista
        this.updateUsersList(this.state.users);

        // Cargar mensajes previos via REST
        await this.loadConversation(userId);
    },

    async loadConversation(userId) {
        try {
            const response = await fetch(`${this.state.serverUrl}/api/messages/conversation/${this.state.userId}/${userId}`);
            if (response.ok) {
                const messages = await response.json();
                this.state.messages[userId] = messages;
                this.renderMessages();
            }
        } catch (e) {
            console.error('Error cargando conversación:', e);
        }
    },

    renderMessages() {
        const container = document.getElementById('chatMessages');
        const messages = this.state.messages[this.state.targetUser.id] || [];
        
        container.innerHTML = messages.map(msg => {
            const isSent = msg.fromUserId === this.state.userId;
            const time = new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            
            return `
                <div class="message ${isSent ? 'sent' : 'received'}">
                    <div class="text">${this.escapeHtml(msg.text)}</div>
                    <span class="message-time">${time}</span>
                </div>
            `;
        }).join('');
        
        container.scrollTop = container.scrollHeight;
    },

    async sendMessage() {
        const input = document.getElementById('chatInput');
        const text = input.value.trim();
        if (!text || !this.state.targetUser) return;

        input.value = '';

        try {
            // 1. Enviar via REST para persistencia
            const response = await fetch(`${this.state.serverUrl}/api/messages/send`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    fromUserId: this.state.userId,
                    toUserId: this.state.targetUser.id,
                    text: text
                })
            });

            if (response.ok) {
                const msg = await response.json();
                
                // 2. Notificar via WebSocket para entrega inmediata
                if (this.state.isConnected) {
                    this.state.socket.send(JSON.stringify({
                        type: 'chat-message',
                        targetUserId: this.state.targetUser.id,
                        message: msg
                    }));
                }

                // 3. Actualizar localmente
                if (!this.state.messages[this.state.targetUser.id]) {
                    this.state.messages[this.state.targetUser.id] = [];
                }
                this.state.messages[this.state.targetUser.id].push(msg);
                this.renderMessages();
            }
        } catch (e) {
            console.error('Error enviando mensaje:', e);
        }
    },

    onReceiveMessage(fromUserId, message) {
        // Guardar mensaje
        if (!this.state.messages[fromUserId]) {
            this.state.messages[fromUserId] = [];
        }
        this.state.messages[fromUserId].push(message);

        // Si es el chat abierto, renderizar
        if (this.state.targetUser?.id === fromUserId) {
            this.renderMessages();
        } else {
            // Incrementar contador de no leídos
            this.state.unreadCounts[fromUserId] = (this.state.unreadCounts[fromUserId] || 0) + 1;
            this.updateUsersList(this.state.users);
            
            // Sonido o vibración ligera
            if (navigator.vibrate) navigator.vibrate(100);
            console.log('Nuevo mensaje de:', fromUserId);
        }
    },

    sendHeartbeat() {
        if (this.state.userId) {
            fetch(`${this.state.serverUrl}/api/users/${this.state.userId}/heartbeat`, { method: 'POST' })
                .catch(() => console.warn('Heartbeat fallido'));
        }
    },

    updateUserInfo(user) {
        document.getElementById('currentUser').innerText = user.username;
        document.getElementById('userIp').innerText = user.hostname;
    },

    updateConnectionStatus(status) {
        const el = document.getElementById('connectionStatus');
        if (status === 'online') {
            el.innerText = 'En línea';
            el.className = 'status-badge online';
        } else {
            el.innerText = 'Desconectado';
            el.className = 'status-badge offline';
        }
    },

    showMainPanel() {
        document.getElementById('registerPanel').classList.remove('active');
        document.getElementById('mainPanel').classList.add('active');
    },

    showError(msg, el) {
        el.innerText = msg;
        el.style.display = 'block';
    },

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    disconnect() {
        if (this.state.socket) this.state.socket.close();
        clearInterval(this.state.userRefreshInterval);
        clearInterval(this.state.heartbeatInterval);
        location.reload();
    }
};

document.addEventListener('DOMContentLoaded', () => app.init());
