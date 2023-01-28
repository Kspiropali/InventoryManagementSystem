package backend.user;

import backend.item.ItemRepository;
import backend.item.ItemType;
import backend.token.Token;
import backend.token.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/user")
public class UserController {
    //private final BeanConfigurer beanConfigurer;
    private final UserService userService;
    private final TokenService tokenService;
    private final ItemRepository itemRepository;

    @PostMapping(path = "/register")
    public String registerUser(@RequestBody User user) {
        //Email validator

        User newUser = userService.registerUser(user);
        Token newToken = tokenService.createToken(newUser);
        userService.sendRegistrationConfirmationEmail(user.getEmail(), newToken.getToken());

        System.out.println("Your registration is: http://localhost:8080/user/verifyRegistration?token=" + newToken.getToken());

        return "Success";
    }

    @GetMapping(path = "/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        Object validated_user = tokenService.validateToken(token);
        userService.activateUserAccount((User) validated_user);
        tokenService.removeTokenByToken(token);
        return "User verified successfully!";
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken) {
        Token newVerificationToken = tokenService.generateNewToken(oldToken);
        User user = newVerificationToken.getUser();

        userService.sendRegistrationConfirmationEmail(user.getEmail(), newVerificationToken.getToken());

        return "Verification Link sent";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/login")
    //@PreAuthorize("hasAuthority('USER_WRITE')")
    public String terminalLogin() {
        /*TODO: We cant just return an id here. Implementation of login tokens are required. We can save the token in
        cookies with expiration time etc.*/
        return "Logged in!";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/login")
    //@PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<byte[]> browserLogin() throws IOException {
        /*TODO: We cant just return an id here. Implementation of login tokens are required. We can save the token in
        cookies with expiration time etc.*/
        //beanConfigurer.removeExistingAndAddNewBean("jdbcCustom");
        byte[] imageBaos = itemRepository.findByItemType(ItemType.FOOD).get().getBarcodeImage();


        //BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBaos));
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBaos);

    }

}

