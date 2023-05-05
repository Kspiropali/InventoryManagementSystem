package backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
//@Qualifier("jdbcCustom")
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = ?1")
    Optional<User> findUserByEmail(String email);

    Long countUsersByCreatedAtAfter(Date createdAt);

    @Query("select count(u) from User u where u.region = ?1")
    Long countUsersByRegion(Region region);
}
