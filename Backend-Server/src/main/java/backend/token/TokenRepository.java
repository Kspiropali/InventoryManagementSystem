package backend.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("select v from Token v where v.token = ?1")
    Token findByToken(final String token);

    @Transactional
    @Modifying
    @Query("delete from Token v where v.token = ?1")
    void removeByToken(String token);

}
