package backend.analytics;

import backend.item.Item;
import backend.kiosk.Kiosk;
import lombok.*;

import javax.persistence.*;
import java.util.Date;


@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "_analytics")
public class AnalyticsModel {

    public AnalyticsModel(String name, int quantity, float price, Kiosk unitId, String initiator) {
        this.name = name;
        this.quantity = quantity;
        this.datePurchased = new Date();
        this.total_profit = price * quantity;
        this.unitId = unitId;
        this.initiator = initiator;
    }

    @Id
    @SequenceGenerator(
            name = "analytics_sequence",
            sequenceName = "analytics_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "analytics_sequence"
    )
    private Long id;
    private String name;
    private int quantity;
    private float total_profit;
    @Transient
    private float price;
    private String initiator;

    // date of transaction
    @Column(updatable = false)
    private Date datePurchased;

    @ManyToOne
    @JoinColumn(name = "unitId", referencedColumnName = "id")
    private Kiosk unitId;
}
