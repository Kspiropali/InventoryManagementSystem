package backend.analytics;

import backend.kiosk.Kiosk;
import backend.kiosk.KioskRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;


@Configuration
@DependsOn({"itemConfig", "kioskConfig"})
public class AnalyticsConfig {
    private final AnalyticsRepository analyticsRepository;
    private final KioskRepository kioskRepository;
    public AnalyticsConfig(AnalyticsRepository analyticsRepository, KioskRepository kioskRepository) {
        this.analyticsRepository = analyticsRepository;
        this.kioskRepository = kioskRepository;
        generatesampledata();
    }


    public void generatesampledata() {
        Kiosk kiosk = kioskRepository.findKioskByUnitId("testing123!").get();
        AnalyticsModel analyticsModel = new AnalyticsModel("Milk", 2, 2.95F, kiosk, "User");
        AnalyticsModel analyticsModel1 = new AnalyticsModel("Salad", 8, 5.95F, kiosk, "User");
        AnalyticsModel analyticsModel2 = new AnalyticsModel("Fennel", 2, 4.95F, kiosk, "User");
        AnalyticsModel analyticsModel3 = new AnalyticsModel("Potatoes", 4, 3.45F, kiosk, "User");
        AnalyticsModel analyticsModel4 = new AnalyticsModel("Rosemary", 20, 2.95F, kiosk, "User");
        AnalyticsModel analyticsModel5 = new AnalyticsModel("Coconut", 4, 4.5F, kiosk, "Kiosk");
        AnalyticsModel analyticsModel6 = new AnalyticsModel("Egg_Noodles", 3, 1.95F, kiosk, "Kiosk");

        analyticsRepository.saveAll(List.of(analyticsModel, analyticsModel1, analyticsModel2, analyticsModel3, analyticsModel4, analyticsModel5, analyticsModel6));
    }
}
