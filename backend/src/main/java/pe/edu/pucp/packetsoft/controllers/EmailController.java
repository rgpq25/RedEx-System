package pe.edu.pucp.packetsoft.controllers;

import pe.edu.pucp.packetsoft.services.EmailService;
import java.util.List;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailService emailService; 

    //Envia un email dado 3 cadenas, mensaje, email destino y asunto
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    boolean sendEmail(@RequestBody List<String> correo)throws AuthenticationException{
        boolean success = false;

        try{
            success = emailService.sendEmailTool(correo.get(0),correo.get(1),correo.get(2));
        }
        catch(Exception ex){ System.out.print(ex.getMessage()); }
        
        return success;
    }

}
