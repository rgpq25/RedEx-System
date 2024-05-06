package pucp.e3c.redex_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Mensaje;

@RestController
@RequestMapping("/api")
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/startSendingMessages")
    @ResponseBody
    public String startSendingMessages() {
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(5000); // 5 seconds delay
                    sendMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return "Sending messages started";
    }

    private void sendMessage() {
        Mensaje mensaje = new Mensaje("Server", "example");
        messagingTemplate.convertAndSend("/tema/mensajes", mensaje);
    }
}
