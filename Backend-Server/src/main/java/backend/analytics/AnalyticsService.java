package backend.analytics;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public interface AnalyticsService {

    HashMap<String, Integer> getProductsSoldAtDate(String date);

    HashMap<String, Integer> getPopularProducts();

    // for kiosk checkout
    String checkout(List<AnalyticsModel> items, String unitId);

    // for user checkout
    String checkout(List<AnalyticsModel> items);

    Double getTotalNetWorth();

    String getTop5Quantity();

    String getTransactions();

    String getSystemLogs() throws IOException;

    String getSystemRam();
}
