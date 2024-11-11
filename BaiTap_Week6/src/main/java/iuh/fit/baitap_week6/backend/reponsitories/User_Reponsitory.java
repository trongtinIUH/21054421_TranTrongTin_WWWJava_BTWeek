package iuh.fit.baitap_week6.backend.reponsitories;

import iuh.fit.baitap_week6.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface User_Reponsitory extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile); // Tìm người dùng qua email
//    @Query("SELECT u FROM User u WHERE u.id = :id")
//    Optional<User> findById(@Param("id") Long id);
}
