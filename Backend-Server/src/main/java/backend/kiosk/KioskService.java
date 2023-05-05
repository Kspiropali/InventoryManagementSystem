package backend.kiosk;

public interface KioskService {
    boolean checkKioskAvailability(String unitId);
    boolean changeKioskAvailability(String unitId, boolean isAvailable);

    String getItemByBarcodeText(String barcode);

    boolean checkKioskLoggedIn(String unitId);

    Kiosk getKioskByUnitId(String unitId);

    boolean checkIfKioskExist(String kioskId);
}
