package mx.edu.utez.carsishop.services.order;

import mx.edu.utez.carsishop.controllers.order.OrderDto;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.address.AddressRepository;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.card.CardRepository;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.order.OrderRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AddressRepository addressRepository;


    public CustomResponse<Order> makeOrder(OrderDto request) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        request.uncrypt();
        Order order = new Order();
        Optional<User> user = userRepository.findByUsername(request.getEmail());
        if(user.isEmpty()){
            return new CustomResponse<>(null,true,400,"User not found");
        }
        Optional<Card> card = cardRepository.findById((Long.parseLong(request.getCard())));
        if(card.isEmpty()){
            return new CustomResponse<>(null,true,400,"Card not found");
        }
        Optional<Address> address = addressRepository.findById((Long.parseLong(request.getAddress())));
        if(address.isEmpty()){
            return new CustomResponse<>(null,true,400,"Address not found");
        }
        order.setAt(new Date(System.currentTimeMillis()));
        order.setUser(user.get());
        order.setCard(card.get());
        order.setAddress(address.get());
        order.setStatus(Order.Status.Paid);
        return new CustomResponse<>(orderRepository.save(order),false,200,"Order created");
    }
}
