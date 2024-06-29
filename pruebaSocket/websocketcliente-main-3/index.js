let stompClient = null;


const onConnectSocket = () => {
    const mensajeMostrar = { nombre: 'Usuario', contenido: 'Hola' }
    const mensajeError = { nombre: 'Usuario', contenido: 'Error' }
    stompClient.subscribe('/algoritmo/diaDiaRespuesta', (mensaje) => {
        //mostrarMensaje(JSON.parse(mensaje.body));
        if(mensaje.body){
            mostrarMensaje(mensajeMostrar);
        }
        else{
            mostrarMensaje(mensajeError);
        }
        
    });
};


const onWebSocketClose = () => {
    if (stompClient !== null) {
        stompClient.deactivate();
    }
};

const conectarWS = () => {
    onWebSocketClose();
    stompClient = new StompJs.Client({
        webSocketFactory: () => new WebSocket('ws://localhost:8080/websocket')
    });
    stompClient.onConnect = onConnectSocket;
    stompClient.onWebSocketClose = onWebSocketClose;
    stompClient.activate();
};

const mostrarMensaje = (mensaje) => {
    const ULMensajes = document.getElementById('ULMensajes');

    const mensajeLI = document.createElement('li');
    mensajeLI.classList.add('list-group-item');
    mensajeLI.innerHTML = `<strong>${mensaje.nombre}</strong>: ${mensaje.contenido}`;
    ULMensajes.appendChild(mensajeLI);
};

document.addEventListener('DOMContentLoaded', () => {
    conectarWS();
});