package mx.edu.utez.carsishop.services.address;

import mx.edu.utez.carsishop.jwt.JwtService;
import mx.edu.utez.carsishop.controllers.address.AddressDto;
import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.address.AddressRepository;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.order.OrderRepository;
import mx.edu.utez.carsishop.models.state.StateRepository;
import mx.edu.utez.carsishop.models.town.TownRepository;
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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;
    private final JwtService jwtService;
    private final StateRepository stateRepository;
    private final TownRepository townRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository, UserRepository userRepository, OrderRepository orderRepository, JwtService jwtService, StateRepository stateRepository, TownRepository townRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.jwtService = jwtService;
        this.stateRepository = stateRepository;
        this.townRepository = townRepository;
    }


    private CryptoService cryptoService = new CryptoService();

    public CustomResponse<Address> register(AddressDto addressDto, String jwtToken) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException{
        String username=jwtService.getUsernameFromToken(jwtToken);
        Optional<User> user=userRepository.findByUsername(username);

        if(user.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "User does not exists",
                    0
            );
        }

        Address address= new Address();
        address.setName(cryptoService.decrypt(addressDto.getName()));
        address.setState(this.stateRepository.findStateByName(addressDto.getState()).get());
        address.setTown(this.townRepository.findTownByName(addressDto.getTown()).get());
        address.setCp(cryptoService.decrypt(addressDto.getCp()));
        address.setSuburb(cryptoService.decrypt(addressDto.getSuburb()));
        address.setStreet(cryptoService.decrypt(addressDto.getStreet()));
        address.setIntnumber(cryptoService.decrypt(addressDto.getIntnumber()));
        address.setExtnumber(cryptoService.decrypt(addressDto.getExtnumber()));
        address.setEnable(true);
        address.setUser(user.get());

        return new CustomResponse<>(
                addressRepository.save(address),
                false,
                200,
                "OK",
                1
        );
    }

    public CustomResponse<List<Address>> getByUser(String jwtToken) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String username=jwtService.getUsernameFromToken(jwtToken);
        Optional<User> user=userRepository.findByUsername(username);
        if(user.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "User does not exists",
                    0
            );
        }else {
            List<Address> addresses=addressRepository.findAllByUserAndEnable(user.get(),true);
            for (Address address: addresses) {
                address.encryptData();
            }
            return new CustomResponse<>(
                    addresses,
                    false,
                    200,
                    "OK",
                    addresses.size()
            );
        }

    }

    public CustomResponse<Address> update(AddressDto addressDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Address> address=addressRepository.findById(Long.parseLong(cryptoService.decrypt(addressDto.getId())));
        if(address.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Address not found",
                    0
            );
        }

        Address updatedAddress = address.get();
        updatedAddress.setName(cryptoService.decrypt(addressDto.getName()));
        updatedAddress.setState(this.stateRepository.findStateByName(addressDto.getState()).get());
        updatedAddress.setTown(this.townRepository.findTownByName(addressDto.getTown()).get());
        updatedAddress.setCp(cryptoService.decrypt(addressDto.getCp()));
        updatedAddress.setSuburb(cryptoService.decrypt(addressDto.getSuburb()));
        updatedAddress.setStreet(cryptoService.decrypt(addressDto.getStreet()));
        updatedAddress.setIntnumber(cryptoService.decrypt(addressDto.getIntnumber()));
        updatedAddress.setExtnumber(cryptoService.decrypt(addressDto.getExtnumber()));

        return new CustomResponse<>(
                addressRepository.save(updatedAddress),
                false,
                200,
                "OK",
                1
        );
    }

    public CustomResponse<String> delete(AddressDto addressDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Address> address = this.addressRepository.findById(Long.parseLong(cryptoService.decrypt(addressDto.getId())));
        if(address.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "La dirección no se encuentra registrada en el sistema",
                    0
            );
        }
        Optional<Order> order=orderRepository.findByAddress(address.get());
        if(order.isEmpty()){
            addressRepository.delete(address.get());
            return new CustomResponse<>(
                    "Dirección eliminada correctamente",
                    false,
                    200,
                    "OK",
                    0
            );
        }else{
            address.get().setEnable(false);
            addressRepository.save(address.get());
            return new CustomResponse<>(
                    "Una orden esta asociada a esta dirección, no se puede eliminar",
                    true,
                    400,
                    "Error al eliminar la dirección",
                    0
            );
        }
    }


}
