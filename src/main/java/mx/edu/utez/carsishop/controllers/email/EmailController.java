package mx.edu.utez.carsishop.controllers.email;

import mx.edu.utez.carsishop.models.email.EmailDetails;
import mx.edu.utez.carsishop.services.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/email", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmailController {
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody EmailDetails emailDetails) {

        return emailService.sendSimpleMail(emailDetails);
    }
}
