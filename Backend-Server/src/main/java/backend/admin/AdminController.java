package backend.admin;

import backend.token.Token;
import backend.token.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private final AdminService adminService;
    private final TokenService tokenService;


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
}
