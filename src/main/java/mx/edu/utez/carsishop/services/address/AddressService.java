package mx.edu.utez.carsishop.services.address;

import mx.edu.utez.carsishop.models.address.Address;
import mx.edu.utez.carsishop.models.address.AddressRepository;
import mx.edu.utez.carsishop.models.order.Order;
import mx.edu.utez.carsishop.models.order.OrderRepository;
import mx.edu.utez.carsishop.models.user.User;
import mx.edu.utez.carsishop.models.user.UserRepository;
import mx.edu.utez.carsishop.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public CustomResponse<Address> register(Address address){
        return new CustomResponse<>(
                addressRepository.save(address),
                false,
                200,
                "OK"
        );
    }
    public CustomResponse<List<Address>> getByUser(String email){
        Optional<User> user=userRepository.findByUsername(email);
        if(user.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "User does not exists"
            );
        }
        return new CustomResponse<>(
                addressRepository.findAllByUser(user.get()),
                false,
                200,
                "OK"
        );
    }

    public CustomResponse<Address> update(Address updatedAddress,long id){
        Optional<Address> address=addressRepository.findById(id);
        if(address.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Address not found"
            );
        }
        updatedAddress.setId(id);
        return new CustomResponse<>(
                addressRepository.save(updatedAddress),
                false,
                200,
                "OK"
        );
    }


    public CustomResponse<String> delete(long id){
        Optional<Address> address=addressRepository.findById(id);
        if(address.isEmpty()){
            return new CustomResponse<>(
                    null,
                    true,
                    400,
                    "Address not found"
            );
        }
        Optional<Order> order=orderRepository.findByAddress(address.get());
        if(order.isEmpty() || order.get().getStatus().equals("Delivered") || order.get().getStatus().equals("Canceled") || order.get().getStatus().equals("Returned") ){
            addressRepository.delete(address.get());
            return new CustomResponse<>(
                    "Address deleted successfully",
                    false,
                    200,
                    "OK"
            );
        }else{
            return new CustomResponse<>(
                    "An Order is using this address, can not be deleted",
                    true,
                    400,
                    "Address can not be deleted"
            );
        }
    }


}
