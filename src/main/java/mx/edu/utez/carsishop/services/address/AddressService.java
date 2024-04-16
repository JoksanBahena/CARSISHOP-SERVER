package mx.edu.utez.carsishop.services.address;

import mx.edu.utez.carsishop.Jwt.JwtService;
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
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private TownRepository townRepository;
    @Autowired
    private JwtService jwtService;

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
        address.setState(this.stateRepository.findStateByName(cryptoService.decrypt(addressDto.getState())).get());
        address.setTown(this.townRepository.findTownByName(cryptoService.decrypt(addressDto.getTown())).get());
        address.setCp(cryptoService.decrypt(addressDto.getCp()));
        address.setSuburb(cryptoService.decrypt(addressDto.getSuburb()));
        address.setStreet(cryptoService.decrypt(addressDto.getStreet()));
        address.setIntnumber(cryptoService.decrypt(addressDto.getIntnumber()));
        address.setExtnumber(cryptoService.decrypt(addressDto.getExtnumber()));
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

    public CustomResponse<Address> update(Address updatedAddress,long id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Address> address=addressRepository.findById(id);
        if(address.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Address not found",
                    0
            );
        }
        updatedAddress.decryptData();
        updatedAddress.setId(id);
        return new CustomResponse<>(
                addressRepository.save(updatedAddress),
                false,
                200,
                "OK",
                1
        );
    }

    @Transactional(rollbackFor = SQLException.class)
    public CustomResponse<String> delete(AddressDto addressDto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Address> address=addressRepository.findById(Long.parseLong(cryptoService.decrypt(addressDto.getId())));
        if(address.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Direccion no encontrada",
                    0
            );
        }
        Optional<Order> order=orderRepository.findByAddress(address.get());
        if(order.isEmpty()){
            addressRepository.delete(address.get());
            return new CustomResponse<>(
                    "Direccion eliminada correctamente",
                    false,
                    200,
                    "OK",
                    0
            );
        }else{
            address.get().setEnable(false);
            addressRepository.save(address.get());
            return new CustomResponse<>(
                    "Direccion deshabilitada correctamente",
                    false,
                    200,
                    "OK",
                    0
            );
        }
    }
}
