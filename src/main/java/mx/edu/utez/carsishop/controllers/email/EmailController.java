package mx.edu.utez.carsishop.controllers.email;

import mx.edu.utez.carsishop.models.email.EmailDetails;
import mx.edu.utez.carsishop.services.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody EmailDetails emailDetails) {
        String result = emailService.sendSimpleMail(emailDetails);

        return result;
    }
}
