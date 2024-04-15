package mx.edu.utez.carsishop.services.order;

import mx.edu.utez.carsishop.Jwt.JwtService;
import mx.edu.utez.carsishop.controllers.order.OrderDto;
import mx.edu.utez.carsishop.controllers.order.OrderUpdateStatusDto;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.address.AddressRepository;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.card.CardRepository;
import mx.edu.utez.carsishop.models.clothOrder.ClothOrder;
import mx.edu.utez.carsishop.models.clothOrder.ClothOrderRepository;
import mx.edu.utez.carsishop.models.clothesCart.ClothesCart;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.order.OrderRepository;
import mx.edu.utez.carsishop.models.shoppingCart.ShoppingCart;
import mx.edu.utez.carsishop.models.shoppingCart.ShoppingCartRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CryptoService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ClothOrderRepository clothOrderRepository;

    @Autowired
    private JwtService jwtService;

    private CryptoService cryptoService = new CryptoService();


    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Order> makeOrder(OrderDto request, String jwtToken) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        request.uncrypt();
        Order order = new Order();
        String username=jwtService.getUsernameFromToken(jwtToken);
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            return new CustomResponse<>(null,true,400,"Usuario no encontrado", 0);
        }


        Optional<Card> card = cardRepository.findById((Long.parseLong(request.getCard())));
        if(card.isEmpty()){
            return new CustomResponse<>(null,true,400,"Tarjeta no encontrada", 0);
        }
        Optional<Address> address = addressRepository.findById((Long.parseLong(request.getAddress())));
        if(address.isEmpty()){
            return new CustomResponse<>(null,true,400,"Direccion no encontrada", 0);
        }
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findByUser(user.get());
        if(shoppingCart.isEmpty()){
            return new CustomResponse<>(null,true,400,"Carrito no encontrado", 0);
        }
        order.setAt(new Date(System.currentTimeMillis()));
        order.setUser(user.get());
        order.setCard(card.get());
        order.setAddress(address.get());
        order.setStatus(Order.Status.Paid);
        order= orderRepository.save(order);
        List<ClothesCart> clothesCarts = shoppingCart.get().getClothesCarts();
        List<ClothOrder> clothOrders = new ArrayList<>();
        for (ClothesCart clothesCart: clothesCarts) {
            ClothOrder clothOrder = new ClothOrder();
            clothOrder.setClothes(clothesCart.getClothes());
            clothOrder.setAmount(clothesCart.getAmount());
            clothOrder.setSize(clothesCart.getSize());
            clothOrder.setTheorder(order);
            clothOrders.add(clothOrder);
        }
        clothOrderRepository.saveAll(clothOrders);
        return new CustomResponse<>(order,false,200,"Pedido Realizado", 1);
    }
    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<Order> updateStatus(OrderUpdateStatusDto updateStatusDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        updateStatusDto.setId(cryptoService.decrypt(updateStatusDto.getId()));
        updateStatusDto.setStatus(cryptoService.decrypt(updateStatusDto.getStatus()));
        Optional<Order> order = orderRepository.findById(Long.parseLong(updateStatusDto.getId()));
        if(order.isEmpty()){
            return new CustomResponse<>(null,true,400,"Pedido no encontrado", 0);
        }
        order.get().setStatus(Order.Status.valueOf(updateStatusDto.getStatus()));
        orderRepository.save(order.get());
        return new CustomResponse<>(order.get(),false,200,"Pedido actualizado", 1);
    }
}
