package mx.edu.utez.carsishop.models.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsUserByPhoneAndIdNot(String phone, long id);

    @Modifying
    @Query(
            value = "UPDATE user SET password = :password WHERE id = :id",nativeQuery = true
    )
    int updatePasswordById(@Param("password") String password, @Param("id") Long id);

    Optional<User> findById(Long id);
}
