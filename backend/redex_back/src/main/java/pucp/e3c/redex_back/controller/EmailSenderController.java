package pucp.e3c.redex_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.EmailRequest;
import pucp.e3c.redex_back.service.EmailSenderService;

@RestController
@CrossOrigin(origins = "https://inf226-981-3c.inf.pucp.edu.pe")
@RequestMapping("back/email")
public class EmailSenderController {
    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest emailReques){
        emailSenderService.sendEmail(emailReques.getToEmail(),emailReques.getSubject(),emailReques.getBody());
        return "Email sent successfully";
    }
}
