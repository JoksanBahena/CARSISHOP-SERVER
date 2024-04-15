package mx.edu.utez.carsishop.controllers.order;

import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.services.order.OrderService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/order")
@CrossOrigin({"*"})
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/makeOrder")
    public ResponseEntity<CustomResponse<Order>> makeOrder(@RequestHeader("Authorization") String authorizationHeader,@RequestBody OrderDto request) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            return ResponseEntity.ok(orderService.makeOrder(request,jwtToken));
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }


}
