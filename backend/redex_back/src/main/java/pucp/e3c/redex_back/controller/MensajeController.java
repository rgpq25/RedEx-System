package pucp.e3c.redex_back.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import pucp.e3c.redex_back.model.Mensaje;

@Controller
public class MensajeController {

    @MessageMapping("/envio")
    @SendTo("/tema/mensajes")
    public Mensaje envio(Mensaje mensaje){
        return new Mensaje(mensaje.nombre(), mensaje.contenido());
    }
}
