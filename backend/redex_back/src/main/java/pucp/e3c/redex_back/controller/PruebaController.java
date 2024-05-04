package pucp.e3c.redex_back.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PruebaController {
    @MessageMapping("/prueba")
    @SendTo("/topic/mensaje")
    public String prueba(String mensaje){
        return mensaje;
    }
}
