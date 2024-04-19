package mx.edu.utez.carsishop.controllers.captcha;

import mx.edu.utez.carsishop.services.captcha.CaptchaService;
import mx.edu.utez.carsishop.utils.CaptchaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"*"})
@RequestMapping(path="/api/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
public class CaptchaController {
    private final CaptchaService captchaService;

    @Autowired
    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @PostMapping("/verifyCaptcha")
    public CaptchaResponse verifyCaptcha(@RequestParam("solution") String solution) {
        return captchaService.verifyCaptcha(solution);
    }
}
