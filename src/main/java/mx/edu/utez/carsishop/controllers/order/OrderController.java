package mx.edu.utez.carsishop.controllers.order;

import jakarta.validation.Valid;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.services.order.OrderService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(path="/api/order", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin({"*"})
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/makeOrder")
    public ResponseEntity<CustomResponse<Order>> makeOrder(@RequestHeader("Authorization") String authorizationHeader,@RequestBody OrderDto request) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            return ResponseEntity.ok(orderService.makeOrder(request,jwtToken));
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));

        }
    }
    @PostMapping("/updateStatus")
    public ResponseEntity<CustomResponse<Order>> updateStatus(@Valid @RequestBody OrderUpdateStatusDto order) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(orderService.updateStatus(order));
    }

    @PostMapping("/getOrders")
    public ResponseEntity<CustomResponse<Order>> getOrders(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return ResponseEntity.ok(orderService.getOrders(jwtToken));
        } else {
            return ResponseEntity.ok(new CustomResponse<>(null,true,400,"Error al obtener el token",1));
        }
    }

    @GetMapping("/getOrdersBySeller")
    public ResponseEntity<Object> getOrdersBySeller(){
        return orderService.getOrdersBySeller();
    }

    @PostMapping("/confirm-order")
    public ResponseEntity<Object> confirmOrder(@Validated({OrderDto.PayOrder.class}) @RequestBody OrderDto orderDto){
        return orderService.confirmOrder(orderDto);
    }

}
