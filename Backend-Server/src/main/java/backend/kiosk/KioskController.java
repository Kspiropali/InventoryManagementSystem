package backend.kiosk;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/kiosk")
public class KioskController {

    @PreAuthorize("hasRole('KIOSK')")
    @GetMapping("/login")
    public ResponseEntity<String> browserLogin() {

        return ResponseEntity.ok("Kiosk Login Successful");
    }

    @PreAuthorize("hasRole('KIOSK')")
    @PostMapping("/login")
    public ResponseEntity<String> termLogin() {

        return ResponseEntity.ok("Kiosk Login Successful");
    }
}
