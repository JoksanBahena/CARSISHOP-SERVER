package mx.edu.utez.carsishop.services.clothes_cart;

import mx.edu.utez.carsishop.jwt.JwtService;
import mx.edu.utez.carsishop.controllers.clothes_cart.ClothesCartDto;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.clothes.ClothesRepository;
import mx.edu.utez.carsishop.models.clothes_cart.ClothesCart;
import mx.edu.utez.carsishop.models.clothes_cart.ClothesCartRepository;
import mx.edu.utez.carsishop.models.shopping_cart.ShoppingCart;
import mx.edu.utez.carsishop.models.shopping_cart.ShoppingCartRepository;
import mx.edu.utez.carsishop.models.stock.Stock;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClothesCartService {
    private final ClothesCartRepository clothesCartRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ClothesRepository clothesRepository;
    private final JwtService jwtService;

    @Autowired
    public ClothesCartService(ClothesCartRepository clothesCartRepository, UserRepository userRepository, ShoppingCartRepository shoppingCartRepository, ClothesRepository clothesRepository, JwtService jwtService) {
        this.clothesCartRepository = clothesCartRepository;
        this.userRepository = userRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.clothesRepository = clothesRepository;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public CustomResponse<ShoppingCart> getClothesCartByUser(String token) {
        String username = jwtService.getUsernameFromToken(token);
        Optional<User> user=userRepository.findByUsername(username);
        if (user.isPresent()){
            Optional<ShoppingCart> shoppingCart=shoppingCartRepository.findByUser(user.get());
            if (shoppingCart.isPresent()){
                return new CustomResponse<>(shoppingCart.get(),false,200,"ok", 1);
            }
        }
        return new CustomResponse<>(null,true,400,"Carrito no encontrado para dicho usuario.", 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<ClothesCart> addClothesCart(ClothesCartDto request, String jwtToken) {
        String username = jwtService.getUsernameFromToken(jwtToken);
        Optional<User> user=userRepository.findByUsername(username);
        if(request.getAmount()<1){
            return new CustomResponse<>(null,true,400,"La cantidad de prendas debe ser mayor a 0", 0);
        }
        if (user.isPresent()){
            Optional<ShoppingCart> shoppingCart=shoppingCartRepository.findByUser(user.get());
            ClothesCart clothesCart=new ClothesCart();

            Optional<Clothes> clothes=clothesRepository.findById(request.getCloth().getId());
            if (!clothes.isPresent()){
                return new CustomResponse<>(null,true,400,"Prenda no encontrada", 0);
            }
            for (Stock stock:clothes.get().getStock()){
                if (stock.getSize().equals(request.getSize())){
                    if (stock.getQuantity()<request.getAmount()){
                        return new CustomResponse<>(null,true,400,"No hay suficiente stock", 0);
                    }
                }
            }
            if(shoppingCart.isPresent()){
                clothesCart.setShoppingCart(shoppingCart.get());
            }else{
                ShoppingCart newShoppingCart=new ShoppingCart();
                newShoppingCart.setUser(user.get());
                shoppingCartRepository.save(newShoppingCart);
                clothesCart.setShoppingCart(newShoppingCart);
            }
            Optional<ClothesCart> clothesCartOptional=clothesCartRepository.findByShoppingCartAndClothesAndSize(clothesCart.getShoppingCart(),request.getCloth(),request.getSize());
            if(clothesCartOptional.isPresent()){
                clothesCart=clothesCartOptional.get();
                clothesCart.setAmount(clothesCart.getAmount()+request.getAmount());
                return new CustomResponse<>(clothesCartRepository.save(clothesCart),false,200,"ok", 1);
            }
            clothesCart.setAmount(request.getAmount());
            clothesCart.setClothes(request.getCloth());

            clothesCart.setSize(request.getSize());

            return new CustomResponse<>(clothesCartRepository.save(clothesCart),false,200,"ok", 1);
        }
        return new CustomResponse<>(null,true,400,"Usuario no encontrado dentro del sistema", 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<String> deleteClothesCart(ClothesCartDto clothesCartDto) {
        long id = clothesCartDto.getId();
        Optional<ClothesCart> clothesCart=clothesCartRepository.findById(id);
        if (clothesCart.isPresent()){
            clothesCartRepository.deleteById(id);
            return new CustomResponse<>("deleted",false,200,"ok", 0);
        }
        return new CustomResponse<>(null,true,400,"Carrito no encontrado", 0);
    }
    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<ClothesCart> updateClothesCart(ClothesCartDto request) {
        Long id=request.getId();
        int amount=request.getAmount();
        Optional<ClothesCart> clothesCart=clothesCartRepository.findById(id);
        if (clothesCart.isPresent()){
            clothesCart.get().setAmount(amount);
            return new CustomResponse<>(clothesCartRepository.save(clothesCart.get()),false,200,"ok", 1);
        }
        return new CustomResponse<>(null,true,400,"Error, no se encontró ningun carrito registrado dentro del sistema.", 0);
    }
}
