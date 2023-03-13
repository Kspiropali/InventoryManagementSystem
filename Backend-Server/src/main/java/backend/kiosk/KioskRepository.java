package backend.kiosk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KioskRepository extends JpaRepository<Kiosk, Long> {

    @Query("select k from Kiosk k where k.unitId = ?1")
    Optional<Kiosk> findKioskByUnitId(String unitId);


    @Query("select k from Kiosk k where k.isAvailable = ?1")
    Optional<Kiosk> findAllByIsAvailable(boolean isAvailable);
}
