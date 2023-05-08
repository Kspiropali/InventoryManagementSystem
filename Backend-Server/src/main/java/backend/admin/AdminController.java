package backend.admin;

import backend.analytics.AnalyticsService;
import backend.item.ItemService;
import backend.kiosk.KioskService;
import backend.token.Token;
import backend.token.TokenService;
import backend.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private final AdminService adminService;
    private final TokenService tokenService;
    private final ItemService itemService;
    private final KioskService kioskService;
    private final UserService userService;
    private final AnalyticsService analyticsService;
    @PostMapping(path = "/register")
    public String registerAdmin(@RequestBody Admin admin) {
        //Email validator

        Admin newAdmin = adminService.registerAdmin(admin);
        Token newToken = tokenService.createToken(newAdmin);
        adminService.sendRegistrationConfirmationEmail(admin.getEmail(), newToken.getToken());

        System.out.println("Your registration is: http://localhost:8080/admin/verifyRegistration?token=" + newToken.getToken());

        return "Success";
    }

    @GetMapping(path = "/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        Object validated_admin = tokenService.validateToken(token);
        adminService.activateAdminAccount((Admin) validated_admin);
        tokenService.removeTokenByToken(token);
        return "Admin account activated!";
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken) {
        Token newVerificationToken = tokenService.generateNewToken(oldToken);
        Admin admin = newVerificationToken.getAdmin();

        adminService.sendRegistrationConfirmationEmail(admin.getEmail(), newVerificationToken.getToken());

        return "Verification Link sent";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/login")
    public String terminalLogin() {
        /*TODO: We cant just return an id here. Implementation of login tokens are required. We can save the token in
        cookies with expiration time etc.*/
        return "Logged in!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/login")
    public String browserLogin() {
        /*TODO: We cant just return an id here. Implementation of login tokens are required. We can save the token in
        cookies with expiration time etc.*/
        return "Logged in!";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/get/item/{id}")
    public ResponseEntity<byte[]> serveItemBarcode(@PathVariable("id") Long id) {
        // check for no value present
        if(id == null || id <= 0 || !itemService.checkIfItemExist(id)) {
            return ResponseEntity.badRequest().body(null);
        }
        byte[] itemImage = itemService.getItemBarcodeImage(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(itemImage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/kiosk/logout/{kioskId}")
    public ResponseEntity<String> kioskLogout(@PathVariable("kioskId") String kioskId) {
        if(kioskId == null || kioskId.isEmpty()) {
            return ResponseEntity.badRequest().body("Kiosk id is empty");
        } else if (kioskService.checkKioskAvailability(kioskId)) {
            return ResponseEntity.badRequest().body("Kiosk is already logged out");
        } else if(!kioskService.checkIfKioskExist(kioskId)) {
            return ResponseEntity.badRequest().body("Kiosk does not exist");
        }

        boolean result = kioskService.changeKioskAvailability(kioskId, true);
        if (result) {
            // delete cookies and session from kiosk
            return ResponseEntity.ok().body("Kiosk logged out");
        } else {
            return ResponseEntity.badRequest().body("Kiosk logout failed");
        }
    }

//    Get users total count
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/total")
    public ResponseEntity<Long> getTotalUsers() {
        Long totalUsers = userService.getTotalUsers();
        return ResponseEntity.ok().body(totalUsers);
    }


//    Get last 24 hours new users count
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/last24")
    public ResponseEntity<Long> getNewUsersLast24Hours() {
        Long usersLast24Hours = userService.getUsersLast24Hours();
        return ResponseEntity.ok().body(usersLast24Hours);
    }

//    Get total items count
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/items/total")
    public ResponseEntity<Long> getTotalItems() {
        Long totalItems = itemService.getTotalItems();
        return ResponseEntity.ok().body(totalItems);
    }

//    Get Total Net Worth from product sales
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/items/networth")
    public ResponseEntity<Double> getTotalNetWorth() {
        Double totalNetWorth = analyticsService.getTotalNetWorth();
        return ResponseEntity.ok().body(totalNetWorth);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/items/top5quantity")
    public ResponseEntity<String> getTop5Quantity() {
        String top5Quantity = analyticsService.getTop5Quantity();
        return ResponseEntity.ok().body(top5Quantity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/regional")
    public ResponseEntity<String> getRegionalUsers() {
        String regionalUsers = userService.getRegionalUsers();

        return ResponseEntity.ok().body(regionalUsers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/analytics/transactions")
    public ResponseEntity<String> getTransactions() {
        String transactions = analyticsService.getTransactions();

        return ResponseEntity.ok().body(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/system/logs")
    public ResponseEntity<String> getSystemLogs() throws IOException {
        String systemLogs = analyticsService.getSystemLogs();

        return ResponseEntity.ok().body(systemLogs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/system/ram")
    public ResponseEntity<String> getSystemRam() throws IOException {
        String systemRam = analyticsService.getSystemRam();

        return ResponseEntity.ok().body(systemRam);
    }
}
