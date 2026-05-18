import { LitElement } from 'lit';

export class DemoChat extends LitElement {

    _stripHtml(html) {
        const div = document.createElement('div');
        div.innerHTML = html;
        return div.textContent || div.innerText || '';
    }

    connectedCallback() {
        super.connectedCallback();
        const MAX_RECONNECT_ATTEMPTS = 30;
        const RECONNECT_INTERVAL = 10000;

        const chatBot = document.getElementsByTagName('chat-bot')[0];
        const that = this;

        let socket = null;
        let reconnectAttempts = 0;
        let reconnectTimer = null;

        function clearMessages() {
            const lastMessage = chatBot.messages.length > 0 ? chatBot.messages[chatBot.messages.length - 1] : null;
            const keepLastMessage = lastMessage &&
                lastMessage.sender &&
                lastMessage.sender.name === 'System' &&
                lastMessage.message &&
                lastMessage.message.includes('Reconnected');

            if (keepLastMessage) {
                while (chatBot.messages.length > 1) {
                    chatBot.messages.shift();
                }
                const bubbles = chatBot.shadowRoot.querySelectorAll('chat-bubble');
                for (let i = 0; i < bubbles.length - 1; i++) {
                    bubbles[i].remove();
                }
            } else {
                while (chatBot.messages.length > 0) {
                    chatBot.messages.pop();
                }
                const bubbles = chatBot.shadowRoot.querySelectorAll('chat-bubble');
                bubbles.forEach(bubble => bubble.remove());
            }
        }

        function createWebSocket() {
            const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
            const ws = new WebSocket(protocol + '://' + window.location.host + '/customer-support-agent');

            ws.onopen = function () {
                const isReconnection = reconnectAttempts > 0;
                clearMessages();
                if (isReconnection) {
                    chatBot.sendMessage('✅ Reconnected', {
                        right: false,
                        sender: { name: 'System' }
                    });
                    reconnectAttempts = 0;
                    if (reconnectTimer) {
                        clearTimeout(reconnectTimer);
                        reconnectTimer = null;
                    }
                }
            };

            ws.onmessage = function (event) {
                chatBot.hideLastLoading();
                let lastMessage;
                if (chatBot.messages.length > 0) {
                    lastMessage = chatBot.messages[chatBot.messages.length - 1];
                }
                if (lastMessage && lastMessage.sender.name === 'Bot' && !lastMessage.loading) {
                    if (!lastMessage.msg) {
                        lastMessage.msg = '';
                    }
                    lastMessage.msg += event.data;
                    const bubbles = chatBot.shadowRoot.querySelectorAll('chat-bubble');
                    const bubble = bubbles.item(bubbles.length - 1);
                    if (lastMessage.message) {
                        bubble.innerHTML = that._stripHtml(lastMessage.message) + lastMessage.msg;
                    } else {
                        bubble.innerHTML = lastMessage.msg;
                    }
                    chatBot.body.scrollTo({ top: chatBot.body.scrollHeight, behavior: 'smooth' });
                } else {
                    chatBot.sendMessage(event.data, {
                        right: false,
                        sender: { name: 'Bot' }
                    });
                }
            };

            ws.onclose = function () {
                handleDisconnection();
            };

            ws.onerror = function (error) {
                console.error('WebSocket error:', error);
            };

            return ws;
        }

        function handleDisconnection() {
            clearMessages();
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                if (reconnectAttempts === 0) {
                    chatBot.sendMessage('⚠️ Connection lost - Reconnecting...', {
                        right: false,
                        sender: { name: 'System' }
                    });
                }
                reconnectAttempts++;
                reconnectTimer = setTimeout(function () {
                    socket = createWebSocket();
                }, RECONNECT_INTERVAL);
            } else {
                chatBot.sendMessage('☠️ Connection lost - Please refresh your browser', {
                    right: false,
                    sender: { name: 'System' }
                });
            }
        }

        socket = createWebSocket();

        chatBot.addEventListener('sent', function (e) {
            if (e.detail.message.sender.name !== 'Bot' && e.detail.message.sender.name !== 'System') {
                const msg = that._stripHtml(e.detail.message.message);
                if (socket && socket.readyState === WebSocket.OPEN) {
                    socket.send(msg);
                    chatBot.sendMessage('', {
                        right: false,
                        loading: true
                    });
                }
            }
        });
    }
}

customElements.define('demo-chat', DemoChat);
