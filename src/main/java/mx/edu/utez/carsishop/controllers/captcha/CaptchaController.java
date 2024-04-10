package mx.edu.utez.carsishop.controllers.captcha;

import mx.edu.utez.carsishop.services.captcha.CaptchaService;
import mx.edu.utez.carsishop.utils.CaptchaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/captcha")
public class CaptchaController {
    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/verifyCaptcha")
    public CaptchaResponse verifyCaptcha(@RequestParam("solution") String solution) {
        return captchaService.verifyCaptcha(solution);
    }
}
