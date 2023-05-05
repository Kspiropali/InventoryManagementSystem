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

    public boolean checkKioskAvailability(String unitId) {
        Kiosk kiosk = kioskRepository.findKioskByUnitId(unitId)
                .orElse(null);
        if (kiosk == null) {
            return false;
        } else return kiosk.isAvailable();
    }

    public boolean changeKioskAvailability(String unitId, boolean isAvailable) {
        Kiosk kiosk = kioskRepository.findKioskByUnitId(unitId)
                .orElse(null);
        if (kiosk == null) {
            return false;
        } else {
            kiosk.setAvailable(isAvailable);
            kioskRepository.save(kiosk);
            return true;
        }
    }

    @Override
    public String getItemByBarcodeText(String id) {
        // barcode is the item id
        //String item = kioskRepository.findItemByBarcode(id);
        return "";
    }

    @Override
    public boolean checkKioskLoggedIn(String unitId) {
        Kiosk kiosk = kioskRepository.findKioskByUnitId(unitId)
                .orElse(null);
        if (kiosk == null) {
            return false;
        } else {
            return kiosk.isAvailable();
        }
    }

    @Override
    public Kiosk getKioskByUnitId(String unitId) {
        return kioskRepository.findKioskByUnitId(unitId)
                .orElse(null);
    }

    @Override
    public boolean checkIfKioskExist(String kioskId) {
        Kiosk kiosk = kioskRepository.findKioskByUnitId(kioskId)
                .orElse(null);
        return kiosk != null;
    }
}
