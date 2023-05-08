package backend.analytics;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/getQuantitySoldAtDate")
    public String getProductsSoldAtDate() {

        return "not implemented";
    }


    // has role admin or kiosk or user
    @PreAuthorize("hasRole('ADMIN') or hasRole('KIOSK') or hasRole('USER')")
    @PostMapping("/getPopularProducts")
    public HashMap<String, Integer> getPopularProducts() {

        return analyticsService.getPopularProducts();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('KIOSK') or hasRole('USER')")
    @PostMapping("/kiosk/checkout")
    public String kioskCheckout(@RequestBody HashMap<String, List<AnalyticsModel>> json, Authentication auth) {
        String unitId = auth.getName();
        // extract the item names from the json
        List<AnalyticsModel> items = json.get("products");
        return analyticsService.checkout(items, unitId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('KIOSK') or hasRole('USER')")
    @PostMapping("/user/checkout")
    public String userCheckout(@RequestBody HashMap<String, List<AnalyticsModel>> json) {
        // extract the item names from the json
        List<AnalyticsModel> items = json.get("products");
        return analyticsService.checkout(items);
    }
}
