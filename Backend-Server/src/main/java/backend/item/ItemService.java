package backend.item;

import org.springframework.stereotype.Service;

@Service
public interface ItemService {
    byte[] getItemBarcodeImage(Long id);

    String getItemBarcode(Long id);

    String checkItemAvailability(String barcodeText);

    String getItemName(String barcode);

    String checkBarcodeTextMatches(String barcodeText);

    boolean checkIfItemExist(Long id);

    String getItemByBarcode(String barcode);

    Long getTotalItems();
}
