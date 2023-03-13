package backend.kiosk;


import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KioskServiceImpl implements UserDetailsService, KioskService {

        private final KioskRepository kioskRepository;

        @Override
        public Kiosk loadUserByUsername(String unitId) {
            return kioskRepository.findKioskByUnitId(unitId)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    String.format("Kiosk with unitId=%s was not found", unitId)
                            )
                    );
        }
}
