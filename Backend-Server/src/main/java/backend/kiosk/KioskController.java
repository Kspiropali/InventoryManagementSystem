package backend.kiosk;


import backend.item.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/kiosk")
public class KioskController {
    private final ItemService itemService;
    private final KioskService kioskService;

    @PreAuthorize("hasRole('KIOSK')")
    @PostMapping("/login")
    public ResponseEntity<String> termLogin(Authentication authentication) {
        // check if kiosk is available
        String unitId = authentication.getName();
        if (!kioskService.checkKioskAvailability(unitId)) {
            return ResponseEntity.badRequest().body("Kiosk is not available");
        }
        //change current kiosk availability to false
        kioskService.changeKioskAvailability(unitId, false);
        return ResponseEntity.ok("Kiosk Login Successful");
    }


//    @PreAuthorize("hasRole('KIOSK')")
//    @PostMapping("/logout")
//    public ResponseEntity<String> termLogout(Authentication authentication, HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
//        // Logout from kiosk by changing availability to true
//        String unitId = authentication.getName();
//        kioskService.changeKioskAvailability(unitId, true);
//        // delete cookies and session ids
//
//        boolean isSecure = false;
//        String contextPath = null;
//        if (request != null) {
//            HttpSession session = request.getSession(false);
//            if (session != null) {
//                session.invalidate();
//            }
//            isSecure = request.isSecure();
//            contextPath = request.getContextPath();
//        }
//        SecurityContext context = SecurityContextHolder.getContext();
//        SecurityContextHolder.clearContext();
//        context.setAuthentication(null);
//        if (response != null) {
//            Cookie cookie = new Cookie("JSESSIONID", null);
//            String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
//            cookie.setPath(cookiePath);
//            cookie.setMaxAge(0);
//            cookie.setSecure(isSecure);
//            response.addCookie(cookie);
//        }
//
//        return ResponseEntity.ok("Kiosk Logout Successful");
//    }

    @PreAuthorize("hasRole('KIOSK')")
    @PostMapping("/checkItemByBarcode")
    public String getItemByBarcode(@RequestParam String barcode, Authentication auth) {
        // check if kiosk has been logged out by admin

        String unitId = auth.getName();
        Kiosk kiosk = kioskService.getKioskByUnitId(unitId);
        if (kiosk == null) {
            return "Kiosk not found";
        } else if (kiosk.isAvailable()) {
            return "Kiosk is not logged in";
        }

        if (barcode == null) {
            return "Wrong barcode";
        }

        String details = itemService.getItemByBarcode(barcode);
        if (details.equals("Item not found")) {
            return "Item not found";
        }
        return itemService.getItemByBarcode(barcode);
    }

    @PreAuthorize("hasRole('KIOSK')")
    @PostMapping("/ping")
    public ResponseEntity<String> checkLoginStatus(Authentication authentication) {
        // check if kiosk is available
        String unitId = authentication.getName();
        return ResponseEntity.ok("You are logged in with kiosk id: " + unitId);
    }

}
