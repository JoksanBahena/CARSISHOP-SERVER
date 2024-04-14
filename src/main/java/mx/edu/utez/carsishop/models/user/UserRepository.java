package mx.edu.utez.carsishop.models.user;

import mx.edu.utez.carsishop.models.sellers.Seller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    boolean existsUserByUsername(String username);

    boolean existsUserByPhone(String phone);

    @Query(value = "SELECT u FROM User u WHERE UPPER(u.name) LIKE UPPER(?1)")
    List<User> findAllByNamePagination(String value, Pageable offset);

    @Query(value = "SELECT u FROM User u WHERE UPPER(u.surname) LIKE UPPER(?1)")
    List<User> findAllBySurnamePagination(String value, Pageable offset);

    @Query(value = "SELECT u FROM User u WHERE UPPER(u.username) LIKE UPPER(?1)")
    List<User> findAllByUsernamePagination(String value, Pageable offset);

    //role
    @Query(value = "SELECT u FROM User u WHERE UPPER(u.role) LIKE UPPER(?1)")
    List<User> findAllByRolePagination(String value, Pageable offset);

    @Query(value = "SELECT COUNT(id) FROM user", nativeQuery = true)
    int searchCount();
}
