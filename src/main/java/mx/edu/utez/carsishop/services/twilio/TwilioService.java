package mx.edu.utez.carsishop.services.twilio;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import mx.edu.utez.carsishop.models.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${TWILIO_ACCOUNT_SID}")
    String sid;
    @Value("${TWILIO_ACCOUNT_TOKEN}")
    String tokenTw;
    @Value("${TWILIO_ACCOUNT_PHONE}")
    String phoneNumber;

    public void sendSMS(String msj) throws ApiException {
        Twilio.init(sid, tokenTw);

        Message.creator(new PhoneNumber("whatsapp:+5217774239270"),
                new PhoneNumber(phoneNumber),
                msj
        ).create();

    }
}
