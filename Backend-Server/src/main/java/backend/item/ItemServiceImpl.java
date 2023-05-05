package backend.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    @Override
    public byte[] getItemBarcodeImage(Long id) {
        byte[] image = itemRepository.findById(id).get().getBarcodeImage();

        return image;
    }

    @Override
    public String getItemBarcode(Long id) {
        return null;
    }

    @Override
    public String checkItemAvailability(String barcodeText) {
        return null;
    }

    @Override
    public String getItemName(String barcode) {
        return null;
    }

    @Override
    public String checkBarcodeTextMatches(String barcodeText) {
        return null;
    }

    @Override
    public boolean checkIfItemExist(Long id) {
        Item item = itemRepository.findById(id).orElse(null);
        return item != null;
    }

    public String getItemByBarcode(String barcode){
        Item item = itemRepository.findItemByBarcodeText(barcode);
        if(item == null){
            return "Item not found";
        }
        return item.toString();
    }

    @Override
    public Long getTotalItems() {
        return itemRepository.count();
    }
}
