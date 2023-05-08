package backend.item;

import com.google.zxing.WriterException;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

@Configuration
public class ItemConfig {
    private final ItemRepository itemRepository;

    // get ftp_server address from application.properties
    @Value("${ftp.server}")
    private String ftp_server;

    private final FTPClient ftpClient = new FTPClient();

    public ItemConfig(ItemRepository itemRepository) throws WriterException, IOException, OutputException, BarcodeException {
        this.itemRepository = itemRepository;
        generateItems();
    }


    private void generateItems() throws WriterException, IOException, OutputException, BarcodeException {
        ArrayList<Item> items = new ArrayList<>();
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();


//        ftpClient.connect(ftp_server, 21);
//        ftpClient.login("kspir", "secure123!");
//        ftpClient.enterLocalPassiveMode();
//        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//        ftpClient.changeWorkingDirectory("/products");
//        ftpClient.retrieveFile("milk.jpg", baos);
        InputStream in = getClass().getResourceAsStream("/static/images/products/milk.jpg");
        //generate items for testing along with their icons
        Item milk = new Item("Milk", 2.95F, new Date(), ItemType.FRESH,
                in.readAllBytes());
        items.add(milk);

//        baos.reset();
//        ftpClient.retrieveFile("baby_leaf_salad.jpg", baos);

        in = getClass().getResourceAsStream("/static/images/products/baby_leaf_salad.jpg");
        Item leaf_salad = new Item("Salad", 5.95F, new Date(), ItemType.VEGETABLE,
                in.readAllBytes());
        items.add(leaf_salad);


//        baos.reset();
//        ftpClient.retrieveFile("blueberries.jpg", baos);
        in = getClass().getResourceAsStream("/static/images/products/blueberries.jpg");
        Item blueberries = new Item("Blueberries", 6.95F, new Date(), ItemType.FRUIT,
                in.readAllBytes());
        items.add(blueberries);


//        baos.reset();
//        ftpClient.retrieveFile("coconut.jpg", baos);
        in = getClass().getResourceAsStream("/static/images/products/coconut.jpg");
        Item coconut = new Item("Coconut", 4.50F, new Date(), ItemType.FRUIT,
                in.readAllBytes());
        items.add(coconut);


//        baos.reset();
//        ftpClient.retrieveFile("egg_noodles.jpg", baos);
        in = getClass().getResourceAsStream("/static/images/products/egg_noodles.jpg");
        Item egg_noodles = new Item("Egg_Noodles", 1.95F, new Date(), ItemType.FRESH,
                in.readAllBytes());
        items.add(egg_noodles);


//        baos.reset();
//        ftpClient.retrieveFile("fennel.jpg", baos);
        in = getClass().getResourceAsStream("/static/images/products/fennel.jpg");
        Item fennel = new Item("Fennel", 4.95F, new Date(), ItemType.FRESH,
                in.readAllBytes());
        items.add(fennel);


//        baos.reset();
//        ftpClient.retrieveFile("passion_fruit.jpg", baos);
        in = getClass().getResourceAsStream("/static/images/products/passion_fruit.jpg");
        Item passion_fruit = new Item("Passion_Fruit", 7.95F, new Date(), ItemType.FRUIT,
                in.readAllBytes());
        items.add(passion_fruit);


//        baos.reset();
//        ftpClient.retrieveFile("potatoes.jpg", baos);
        in = getClass().getResourceAsStream("/static/images/products/potatoes.jpg");
        Item potatoes = new Item("Potatoes", 3.45F, new Date(), ItemType.FRESH,
                in.readAllBytes());
        items.add(potatoes);


//        baos.reset();
//        ftpClient.retrieveFile("rosemary.jpg", baos);
        in = getClass().getResourceAsStream("/static/images/products/rosemary.jpg");
        Item rosemary = new Item("Rosemary", 2.95F, new Date(), ItemType.VEGETABLE,
                in.readAllBytes());
        items.add(rosemary);

        itemRepository.saveAll(items);

//        ftpClient.logout();
//        ftpClient.disconnect();
    }
}
