package backend.item;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.*;
import org.apache.commons.net.util.Base64;
import org.hibernate.annotations.CreationTimestamp;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "_items")
public class Item {
    private static int item_id = 1;

    public Item(String name, float price, Date expirationTime, ItemType itemType, byte[] image) throws WriterException, IOException, OutputException, BarcodeException {
        this.name = name;
        this.price = price;
        this.expirationTime = expirationTime;
        this.itemType = itemType;
        this.image = image;
        this.qrCodeImage = calculateQrCodeImage();
        this.barcodeImage = calculateBarcodeImage();
        item_id++;
    }

    @Id
    @SequenceGenerator(
            name = "items_sequence",
            sequenceName = "items_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "items_sequence"
    )
    private Long id;
    private String barcodeText;
    private String qrCodeText;
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    private String name;
    private float price;

    private int quantity;
    @Lob
    private byte[] image;
    @Column(updatable = false)
    @Basic(optional = false)
    private Date expirationTime;

    private ItemType itemType;
    //qr code generated image
    @Lob
    private byte[] qrCodeImage;

    //barcode generated image
    @Lob
    private byte[] barcodeImage;

    public byte[] calculateQrCodeImage() throws WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String barcodeText = "asdasdasd";
        QRCodeWriter barcodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix =
                barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        MatrixToImageWriter.writeToStream(bitMatrix, "jpeg", baos);
        return baos.toByteArray();
    }

    //Barcode is of type EAN13
    //Needs to be at least 12 digits long, maximum 13 digits
    //The last digit is a checksum digit
    //The (1)first and (2)second digits are the country code
    //The (3)third, (4)fourth, (5)fifth, (6)sixth digits are the product id in the database
    //The (7)seventh, (8)eighth, (9)ninth, (10)tenth is the expiry date
    //The (11)eleventh is the batch number
    //The (12)twelfth is the checksum of the barcode
    public byte[] calculateBarcodeImage() throws BarcodeException, OutputException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String text = "";
        //making sure the id is 4 digits long
        if (item_id < 10) {
            text = "000" + item_id;
        } else if (item_id < 100) {
            text = "00" + item_id;
        } else if (item_id < 1000) {
            text = "0" + item_id;
        }
        String expiryDate = String.valueOf(expirationTime.getMonth() + expirationTime.getYear());

        // get year and month only
        // adding a single 0 in front of the barcode in order to start from 1 in the loop later in calculateBarcode
        String barcodeText = "044" + text + expiryDate + "33";
        barcodeText = calculateBarcode(barcodeText);
        System.out.println(barcodeText);
        this.barcodeText = barcodeText;
        Barcode barcode = BarcodeFactory.createEAN13(barcodeText);
        barcode.setFont(Font.getFont("BOLD"));

        ImageIO.write(BarcodeImageHandler.getImage(barcode), "jpeg", baos);

        return baos.toByteArray();
    }

    private String calculateBarcode(String barcodeText) {
        int evenSumMultiplied = 0;
        int oddSum = 0;

        for (int i = 1; i < barcodeText.length(); i++) {
            int number = Integer.parseInt(barcodeText.substring(i, i + 1));
            if (i % 2 == 0) {
                evenSumMultiplied += number;
            } else {
                oddSum += number * 3;
            }
        }

        int totalSum = evenSumMultiplied + oddSum;
        if (totalSum % 10 == 0) {
            return (barcodeText + "0").substring(1);
        } else {
            return (barcodeText + (10 - (totalSum % 10))).substring(1);
        }
    }

    @Override
    public String toString() {
        return name + "," + price + "," + expirationTime + "," + itemType + "," + Base64.encodeBase64String(image);
    }
}