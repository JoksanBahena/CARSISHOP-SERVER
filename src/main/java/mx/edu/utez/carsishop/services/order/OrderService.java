package mx.edu.utez.carsishop.services.order;

import mx.edu.utez.carsishop.jwt.JwtService;
import mx.edu.utez.carsishop.controllers.order.OrderDto;
import mx.edu.utez.carsishop.controllers.order.OrderUpdateStatusDto;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.address.AddressRepository;
import mx.edu.utez.carsishop.models.card.Card;
import mx.edu.utez.carsishop.models.card.CardRepository;
import mx.edu.utez.carsishop.models.cloth_order.ClothOrder;
import mx.edu.utez.carsishop.models.cloth_order.ClothOrderRepository;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.clothes_cart.ClothesCart;
import mx.edu.utez.carsishop.models.clothes_cart.ClothesCartRepository;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.order.OrderRepository;
import mx.edu.utez.carsishop.models.sellers.Seller;
import mx.edu.utez.carsishop.models.shopping_cart.ShoppingCart;
import mx.edu.utez.carsishop.models.shopping_cart.ShoppingCartRepository;
import mx.edu.utez.carsishop.models.stock.Stock;
import mx.edu.utez.carsishop.models.stock.StockRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CryptoService;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final CardRepository cardRepository;

    private final AddressRepository addressRepository;

    private final ShoppingCartRepository shoppingCartRepository;

    private final ClothOrderRepository clothOrderRepository;

    private final JwtService jwtService;

    private final CryptoService cryptoService = new CryptoService();

    private final StockRepository stockRepository;

    private final ClothesCartRepository clothesCartRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, CardRepository cardRepository, AddressRepository addressRepository, ShoppingCartRepository shoppingCartRepository, ClothOrderRepository clothOrderRepository, JwtService jwtService, StockRepository stockRepository, ClothesCartRepository clothesCartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.addressRepository = addressRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.clothOrderRepository = clothOrderRepository;
        this.jwtService = jwtService;
        this.stockRepository = stockRepository;
        this.clothesCartRepository = clothesCartRepository;
    }


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
        order.setStatus(Order.Status.PAID);
        order.setPaid(false);
        order= orderRepository.save(order);
        List<ClothesCart> clothesCarts = shoppingCart.get().getClothesCarts();
        List<ClothOrder> clothOrders = new ArrayList<>();
        List<Stock> postStock = new ArrayList<>();
        for (ClothesCart clothesCart: clothesCarts) {
            ClothOrder clothOrder = new ClothOrder();
            Clothes clothes= clothesCart.getClothes();
            int amount = clothesCart.getAmount();
            for (Stock stock: clothes.getStock()) {
                if(stock.getSize().equals(clothesCart.getSize())){
                    if(stock.getQuantity()<amount){
                        return new CustomResponse<>(null,true,400,"No hay suficiente stock para tu compra", 0);

                    }else{
                        stock.setQuantity(stock.getQuantity()-amount);
                        postStock.add(stock);
                    }
                    break;
                }
            }
            clothOrder.setClothes(clothes);
            clothOrder.setAmount(clothesCart.getAmount());
            clothOrder.setSize(clothesCart.getSize());
            clothOrder.setTheorder(order);
            clothOrders.add(clothOrder);
            clothesCartRepository.delete(clothesCart);
        }
        stockRepository.saveAll(postStock);
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

    public CustomResponse<Order> getOrders(String jwtToken) {
        String username=jwtService.getUsernameFromToken(jwtToken);
        Optional<User> user=userRepository.findByUsername(username);
        if(user.isEmpty()){
            return new CustomResponse<>(null,true,400,"Usuario no encontrado", 0);
        }
        Order order = orderRepository.findByUser(user.get().getId());
        return new CustomResponse<>(order,false,200,"Pedidos encontrados", 1);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> getOrdersBySeller(){
        List<Order> allOrders = orderRepository.findAll(); // Obtener todas las órdenes

        Map<Seller, List<Order>> salesBySeller = new HashMap<>(); // Mapa para almacenar las ventas por vendedor

        for (Order order : allOrders) {
            List<ClothOrder> clothOrders = order.getClothOrders(); // Obtener la lista de órdenes de ropa de cada orden

            for (ClothOrder clothOrder : clothOrders) {
                // Obtener la prenda asociada a la orden de ropa
                Clothes clothes = clothOrder.getClothes();

                // Obtener el vendedor de la prenda
                Seller seller = clothes.getSeller(); // Suponiendo que hay un método "getSeller()" en el modelo Clothes

                // Agregar la orden a la lista correspondiente al vendedor en el mapa
                List<Order> sales = salesBySeller.getOrDefault(seller, new ArrayList<>());
                sales.add(order);
                salesBySeller.put(seller, sales);
            }
        }

        return new ResponseEntity<>(new CustomResponse<>(salesBySeller, false, HttpStatus.OK.value(), "Ventas por vendedor obtenidas correctamente.", salesBySeller.size()), HttpStatus.OK);
    }

    //paid order
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> confirmOrder(OrderDto orderDto) {
        Optional<Order> order = orderRepository.findById(orderDto.getId());

        if (order.isEmpty()){
            return new ResponseEntity<>(new CustomResponse<>(null, true, 404, "La orden no se encuentra registrada dentro del sistema", 0), HttpStatus.NOT_FOUND);
        }

        if (order.get().isPaid()){
            return new ResponseEntity<>(new CustomResponse<>(null, true, 400, "La orden ya ha sido pagada", 0), HttpStatus.BAD_REQUEST);
        }

        order.get().setPaid(true);
        orderRepository.save(order.get());

        return new ResponseEntity<>(new CustomResponse<>(order.get(), false, 200, "Orden pagada correctamente", 1), HttpStatus.OK);
    }
}
