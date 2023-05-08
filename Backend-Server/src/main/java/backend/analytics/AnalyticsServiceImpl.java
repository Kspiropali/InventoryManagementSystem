package backend.analytics;

import backend.admin.Admin;
import backend.admin.AdminRepository;
import backend.kiosk.Kiosk;
import backend.kiosk.KioskRepository;
import backend.user.User;
import backend.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Service
public class AnalyticsServiceImpl implements AnalyticsService, UserDetailsService {
    private final AnalyticsRepository analyticsRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final KioskRepository kioskRepository;

    public HashMap<String, Integer> getProductsSoldAtDate(String date) {
        // parse the date correctly and then call the repository
        System.out.println(date);

        LocalDate localDate = LocalDate.parse(date);

        List<AnalyticsModel> analyticsModelList = analyticsRepository.findAllByDatePurchased(Date.from(localDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)));
        HashMap<String, Integer> results = new HashMap<>();
        // convert List to name, quantity format
        for (AnalyticsModel analyticsModel : analyticsModelList) {
            if (results.containsKey(analyticsModel.getName())) {
                results.put(analyticsModel.getName(), results.get(analyticsModel.getName()) + analyticsModel.getQuantity());
            } else {
                results.put(analyticsModel.getName(), analyticsModel.getQuantity());
            }
        }

        return results;
    }

    @Override
    public HashMap<String, Integer> getPopularProducts() {
        List<AnalyticsModel> model = analyticsRepository.findAllByOrderByQuantity();
        System.out.println(model);
        HashMap<String, Integer> results = new HashMap<>();
        // max limit top 5 products
        for (AnalyticsModel analyticsModel : model) {
            results.put(analyticsModel.getName(), analyticsModel.getQuantity());
        }

        // return products by popularity
        return results;
    }

    // for kiosk checkout
    @Override
    public String checkout(List<AnalyticsModel> items, String unitId) {
        // save the items to the database
        //get the kiosk id from the unit id
        Kiosk kiosk = kioskRepository.findKioskByUnitId(unitId).orElse(null);
        if (kiosk == null) {
            return "Kiosk not found";
        }
        for (AnalyticsModel analyticsModel : items) {
            analyticsModel.setUnitId(kiosk);
            analyticsModel.setDatePurchased(new Date());
            analyticsModel.setTotal_profit(analyticsModel.getPrice());
            analyticsModel.setQuantity(analyticsModel.getQuantity());
            analyticsModel.setInitiator("Kiosk");
            analyticsRepository.save(analyticsModel);
        }

        return "Success";
    }

    // for user checkout
    @Override
    public String checkout(List<AnalyticsModel> items) {
        // save the items to the database, no kioskId since a user initiated the checkout
        for (AnalyticsModel analyticsModel : items) {
            analyticsModel.setUnitId(null);
            analyticsModel.setDatePurchased(new Date());
            analyticsModel.setTotal_profit(analyticsModel.getPrice());
            analyticsModel.setQuantity(analyticsModel.getQuantity());
            analyticsModel.setInitiator("User");
            analyticsRepository.save(analyticsModel);
        }

        return "Success";
    }

    @Override
    public Double getTotalNetWorth() {
        List<AnalyticsModel> analyticsModelList = analyticsRepository.findAll();
        double total = 0.0;
        for (AnalyticsModel analyticsModel : analyticsModelList) {
            total += analyticsModel.getTotal_profit();
        }
        // return the total net worth, limit 2 dp
        return Math.round(total * 100.0) / 100.0;
    }

    @Override
    public String getTop5Quantity() {
        // return 5 top product names and quantity by most quantity
        List<AnalyticsModel> model = analyticsRepository.findAllByOrderByQuantity();
        System.out.println(model);
        // there are multiple products with the same name, but different quantity
        // so we need to add the quantity together
        HashMap<String, Integer> results = new HashMap<>();
        for (AnalyticsModel analyticsModel : model) {
            if (results.containsKey(analyticsModel.getName())) {
                results.put(analyticsModel.getName(), results.get(analyticsModel.getName()) + analyticsModel.getQuantity());
            } else {
                results.put(analyticsModel.getName(), analyticsModel.getQuantity());
            }
        }
        System.out.println(results);
        // build with string builder the final string
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : results.keySet()) {
            stringBuilder.append(key).append(" ").append(results.get(key)).append("\n");
        }

        // remove last \n
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();

//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < 5; i++) {
//            stringBuilder.append(model.get(i).getName()).append(" ").append(model.get(i).getQuantity()).append("\n");
//        }
//        return stringBuilder.toString();
    }

    @Override
    public String getTransactions() {
        // return last 6 transactions
        List<AnalyticsModel> model = analyticsRepository.findAllByOrderByDatePurchasedDesc();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(model.get(i).getId()).append(" ").append(model.get(i).getName()).append(" ").append(model.get(i).getInitiator()).append(" ").append(model.get(i).getTotal_profit()).append(" ").append(model.get(i).getQuantity()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String getSystemLogs() throws IOException {
        // Open the log file in read-only mode
        try (RandomAccessFile file = new RandomAccessFile("Backend-Server/src/main/resources/logs/spring.log", "r")) {
            // Seek to the end of the file
            long fileLength = file.length();
            if (fileLength == 0) {
                return "";
            }
            file.seek(fileLength);

            // Read the last 10,000 characters from the file
            int bufferSize = Math.min(100000, (int) fileLength);
            byte[] buffer = new byte[bufferSize];
            file.seek(fileLength - bufferSize);
            file.read(buffer, 0, bufferSize);

            // Convert the buffer to a string and return it
            return new String(buffer, StandardCharsets.UTF_8);
        }
    }

    @Override
    public String getSystemRam() {
        // get the system used and total ram
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        float usedHeapMemory = (float) heapMemoryUsage.getUsed() / (1024*1024);
        float maxHeapMemory = (float) heapMemoryUsage.getMax() / (1024*1024);
        float usedNonHeapMemory = (float) nonHeapMemoryUsage.getUsed() / (1024*1024);

        return "Heap Memory Used: " + usedHeapMemory + "MB\nMax: " + maxHeapMemory + "MB\nNon Heap Memory Used: " + usedNonHeapMemory + "MB";
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findAdminByEmail(username).orElse(null);
        if (admin != null) {
            return admin;
        }

        User user = userRepository.findUserByEmail(username).orElse(null);
        if (user != null) {
            return user;
        }

        return kioskRepository.findKioskByUnitId(username).orElse(null);
    }
}
