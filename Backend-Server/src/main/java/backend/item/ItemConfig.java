/*
package backend.item;

import com.google.zxing.WriterException;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Date;

@Configuration
public class ItemConfig {
    private final ItemRepository itemRepository;

    public ItemConfig(ItemRepository itemRepository) throws WriterException, IOException, OutputException, BarcodeException {
        this.itemRepository = itemRepository;
        generateItems();
    }


    private void generateItems() throws WriterException, IOException, OutputException, BarcodeException {
        //generate items for testing
        Item grocery1 = new Item("Milk", 2.99F ,new Date(), ItemType.FOOD);
        itemRepository.save(grocery1);
    }
}
*/
