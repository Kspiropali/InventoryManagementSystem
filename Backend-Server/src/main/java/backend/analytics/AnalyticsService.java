package backend.analytics;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public interface AnalyticsService {

    HashMap<String, Integer> getProductsSoldAtDate(String date);

    HashMap<String, Integer> getPopularProducts();

    String checkout(List<AnalyticsModel> items, String unitId);

    Double getTotalNetWorth();

    String getTop5Quantity();

    String getTransactions();
}
