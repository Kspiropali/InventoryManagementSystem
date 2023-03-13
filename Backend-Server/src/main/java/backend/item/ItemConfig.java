package backend.item;

import com.google.zxing.WriterException;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Configuration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Configuration
public class ItemConfig {
    private final ItemRepository itemRepository;

    private final FTPClient ftpClient = new FTPClient();
    public ItemConfig(ItemRepository itemRepository) throws WriterException, IOException, OutputException, BarcodeException {
        this.itemRepository = itemRepository;
        generateItems();
    }


    private void generateItems() throws WriterException, IOException, OutputException, BarcodeException {
        ArrayList<Item> items = new ArrayList<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ftpClient.connect("172.11.1.8", 21);
        ftpClient.login("kspir", "secure123!");

        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory("/products");
        ftpClient.retrieveFile("milk.jpg", baos);
        //generate items for testing along with their icons
        Item milk = new Item("Milk", 2.99F, new Date(), ItemType.FRESH,
                baos.toByteArray());
        items.add(milk);

        baos.reset();
        ftpClient.retrieveFile("baby_leaf_salad.jpg", baos);
        Item leaf_salad = new Item("Salad", 5.99F, new Date(), ItemType.VEGETABLE,
                baos.toByteArray());
        items.add(leaf_salad);

        baos.reset();
        ftpClient.retrieveFile("blueberries.jpg", baos);
        Item blueberries = new Item("Blueberries", 6.95F, new Date(), ItemType.FRUIT,
                baos.toByteArray());
        items.add(blueberries);

        baos.reset();
        ftpClient.retrieveFile("coconut.jpg", baos);
        Item coconut = new Item("Coconut", 4.49F, new Date(), ItemType.FRUIT,
                baos.toByteArray());
        items.add(coconut);

        baos.reset();
        ftpClient.retrieveFile("egg_noodles.jpg", baos);
        Item egg_noodles = new Item("Egg Noodles", 1.99F, new Date(), ItemType.FRESH,
                baos.toByteArray());
        items.add(egg_noodles);

        baos.reset();
        ftpClient.retrieveFile("fennel.jpg", baos);
        Item fennel = new Item("Fennel", 4.99F, new Date(), ItemType.FRESH,
                baos.toByteArray());
        items.add(fennel);


        baos.reset();
        ftpClient.retrieveFile("passion_fruit.jpg", baos);
        Item passion_fruit = new Item("Passion Fruit", 7.95F, new Date(), ItemType.FRUIT,
                baos.toByteArray());
        items.add(passion_fruit);

        baos.reset();
        ftpClient.retrieveFile("potatoes.jpg", baos);
        Item potatoes = new Item("Potatoes", 3.45F, new Date(), ItemType.FRESH,
                baos.toByteArray());
        items.add(potatoes);

        baos.reset();
        ftpClient.retrieveFile("rosemary.jpg", baos);
        Item rosemary = new Item("Rosemary", 2.99F, new Date(), ItemType.VEGETABLE,
                baos.toByteArray());
        items.add(rosemary);

        itemRepository.saveAll(items);

        ftpClient.logout();
        ftpClient.disconnect();
    }
}
