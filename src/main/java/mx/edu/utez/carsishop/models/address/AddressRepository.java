package mx.edu.utez.carsishop.models.address;

import mx.edu.utez.carsishop.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUser(User user);
    List<Address> findAllByUserAndEnable(User user, boolean enable);
}
