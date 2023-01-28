package backend.item;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
import org.hibernate.annotations.Type;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.*;
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
@ToString
@Entity
@Table(name = "_items")
public class Item {

    public Item(String name, float price, Date expirationTime, ItemType itemType) throws WriterException, IOException, OutputException, BarcodeException {
        this.name = name;
        this.price = price;
        this.expirationTime = expirationTime;
        this.itemType = itemType;
        this.qrCodeImage = calculateQrCodeImage();
        this.barcodeImage = calculateBarcodeImage();
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

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    private String name;
    private int quantity;
    private float price;

    @Column(updatable = false)
    @Basic(optional = false)
    private Date expirationTime;

    private ItemType itemType;
    //qr code generator
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] qrCodeImage;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
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

    public byte[] calculateBarcodeImage() throws BarcodeException, OutputException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String barcodeText = "111134233111";
        Barcode barcode = BarcodeFactory.createEAN13(barcodeText);
        barcode.setFont(Font.getFont("BOLD"));
        ImageIO.write(BarcodeImageHandler.getImage(barcode), "jpeg", baos);
        return baos.toByteArray();
    }
}