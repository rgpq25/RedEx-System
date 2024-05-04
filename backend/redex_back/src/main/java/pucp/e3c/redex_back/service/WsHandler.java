package pucp.e3c.redex_back.service;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import ch.qos.logback.classic.Logger;

@Service
public class WsHandler extends TextWebSocketHandler{
    //private static final Logger logger = (Logger) LoggerFactory.getLogger(WsHandler.class);

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>(); //tipo de lista para entornos de concurrencia, no esta diseniado para listas en constante modificaicon, esposible que se necesite otro tipo de listas para otros casos

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        //logger.info("New session connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        //logger.info("Session closed: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //logger.info("Message received: " + message.getPayload());
        for (WebSocketSession webSocketSession : sessions) {
            webSocketSession.sendMessage(message);
        }
    }
}
