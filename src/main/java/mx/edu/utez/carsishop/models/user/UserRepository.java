package mx.edu.utez.carsishop.models.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsUserByPhoneAndIdNot(String phone, long id);
    boolean existsUserByUsername(String username);
    boolean existsUserByPhone(String phone);

    @Modifying
    @Query(
            value = "UPDATE user SET password = :password WHERE id = :id",nativeQuery = true
    )
    int updatePasswordById(@Param("password") String password, @Param("id") Long id);

    Optional<User> findById(Long id);

    @Modifying
    @Query(
            value = "UPDATE user SET status = true WHERE email = :username",nativeQuery = true
    )
    int updateStatusByEmail(@Param("username") String username);

    @Query(
            value = "SELECT status FROM user WHERE email = :username",nativeQuery = true
    )
    boolean getStatusByEmail(@Param("username") String username);
}
