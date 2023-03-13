package backend.kiosk;

import backend.security.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;


@Configuration
public class KioskConfig {
    private final KioskRepository kioskRepository;

    private final PasswordEncoder passwordEncoder;

    public KioskConfig(KioskRepository kioskRepository, PasswordEncoder passwordEncoder) {
        this.kioskRepository = kioskRepository;
        this.passwordEncoder = passwordEncoder;
        init();
    }

    public void init() {

        ArrayList<Kiosk> kiosks = new ArrayList<>();
        Kiosk kiosk = new Kiosk("testing123!", passwordEncoder.encode("kiosk"));


        kiosks.add(kiosk);


        kioskRepository.saveAll(kiosks);
    }
}
