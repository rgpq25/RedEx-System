let stompCliente;


const onConnectSocket = () => {
    // Subscribe to /tema/timer topic
    stompCliente.subscribe('/tema/timer', (message) => {
        mostrarSegundo(message.body);
    });
};

const onWebSocketClose = () => {
    if (stompCliente !== null) {
        stompCliente.deactivate();
    }
};

const conectarWS = () => {
    onWebSocketClose();
    stompCliente = new StompJs.Client({
        webSocketFactory: () => new WebSocket('ws://localhost:8080/websocket')
    });
    stompCliente.onConnect = onConnectSocket;
    stompCliente.onWebSocketClose = onWebSocketClose;
    stompCliente.activate();
};

const mostrarSegundo = (segundo) => {
    const outputDiv = document.getElementById('output');
    outputDiv.innerText = `Received second: ${segundo}`;
};

document.addEventListener('DOMContentLoaded', () => {
    const btnEnviar = document.getElementById('btnEnviar');
    btnEnviar.addEventListener('click', (e) => {
        e.preventDefault();
        enviarMensaje();
    });
    conectarWS();
});