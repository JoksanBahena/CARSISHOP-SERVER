package mx.edu.utez.carsishop.services.clothesCart;

import mx.edu.utez.carsishop.controllers.clothesCart.ClothesCartDto;
import mx.edu.utez.carsishop.models.clothes.Clothes;
import mx.edu.utez.carsishop.models.clothes.ClothesRepository;
import mx.edu.utez.carsishop.models.clothesCart.ClothesCart;
import mx.edu.utez.carsishop.models.clothesCart.ClothesCartRepository;
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
import java.util.List;
import java.util.Optional;

@Service
public class ClothesCartService {
    @Autowired
    private ClothesCartRepository clothesCartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ClothesRepository clothesRepository;

    private CryptoService cryptoService;

    @Transactional(readOnly = true)
    public CustomResponse<ShoppingCart> getClothesCartByUser(String email) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        email=cryptoService.decrypt(email);
        Optional<User> user=userRepository.findByUsername(email);
        if (user.isPresent()){
            Optional<ShoppingCart> shoppingCart=shoppingCartRepository.findByUser(user.get());
            if (shoppingCart.isPresent()){
                return new CustomResponse<>(shoppingCart.get(),false,200,"ok", 1);
            }
        }
        return new CustomResponse<>(null,true,400,"not found", 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<ClothesCart> addClothesCart(ClothesCartDto request) {
        Optional<User> user=userRepository.findByUsername(request.getEmail());

        if (user.isPresent()){
            Optional<ShoppingCart> shoppingCart=shoppingCartRepository.findByUser(user.get());
            ClothesCart clothesCart=new ClothesCart();
            clothesCart.setAmount(request.getAmount());

            Optional<Clothes> clothes=clothesRepository.findById(request.getCloth().getId());
            if (!clothes.isPresent()){
                return new CustomResponse<>(null,true,400,"cloth not found", 0);
            }

            clothesCart.setClothes(request.getCloth());
            if(shoppingCart.isPresent()){
                clothesCart.setShoppingCart(shoppingCart.get());
            }else{
                ShoppingCart newShoppingCart=new ShoppingCart();
                newShoppingCart.setUser(user.get());
                shoppingCartRepository.save(newShoppingCart);
                clothesCart.setShoppingCart(newShoppingCart);
            }
            clothesCart.setSize(request.getSize());
            return new CustomResponse<>(clothesCartRepository.save(clothesCart),false,200,"ok", 1);
        }
        return new CustomResponse<>(null,true,400,"user not found", 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<String> deleteClothesCart(Long id) {
        Optional<ClothesCart> clothesCart=clothesCartRepository.findById(id);
        if (clothesCart.isPresent()){
            clothesCartRepository.deleteById(id);
            return new CustomResponse<>("deleted",false,200,"ok", 0);
        }
        return new CustomResponse<>(null,true,400,"not found", 0);
    }
    @Transactional(rollbackFor = Exception.class)
    public CustomResponse<ClothesCart> updateClothesCart(Long id, int amount) {
        Optional<ClothesCart> clothesCart=clothesCartRepository.findById(id);
        if (clothesCart.isPresent()){
            clothesCart.get().setAmount(amount);
            return new CustomResponse<>(clothesCartRepository.save(clothesCart.get()),false,200,"ok", 1);
        }
        return new CustomResponse<>(null,true,400,"not found", 0);
    }
}
