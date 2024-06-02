package pucp.e3c.redex_back.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import pucp.e3c.redex_back.model.Mensaje;

@Controller
@CrossOrigin(origins = "https://inf226-981-3c.inf.pucp.edu.pe")
public class MensajeController {

    @MessageMapping("/envio")
    @SendTo("/tema/mensajes")
    public Mensaje envio(Mensaje mensaje){
        return new Mensaje(mensaje.nombre(), mensaje.contenido());
    }
}
