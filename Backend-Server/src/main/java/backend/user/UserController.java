package backend.user;

import backend.event.Publisher;
import backend.item.Item;
import backend.item.ItemRepository;
import backend.item.ItemService;
import backend.item.ItemType;
import backend.token.Token;
import backend.token.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/user")
public class UserController {
    //private final BeanConfigurer beanConfigurer;
    private final UserService userService;
    private final TokenService tokenService;
    private final Publisher eventPublisher;
    private final ItemService itemService;

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
        System.out.println(((User) validated_user).getUsername() + " verified his account");
        eventPublisher.publishCustomEvent(((User) validated_user).getUsername(), "user register");
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
    public String terminalLogin(ServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username + " " + password);
        /*TODO: We cant just return an id here. Implementation of login tokens are required. We can save the token in
        cookies with expiration time etc.*/
        return "Login successful";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/login")
    //@Transactional
    //@PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<String> browserLogin() throws IOException {
        /*TODO: We cant just return an id here. Implementation of login tokens are required. We can save the token in
        cookies with expiration time etc.*/
        //beanConfigurer.removeExistingAndAddNewBean("jdbcCustom");
//        List<List<Item>> imageBaos = Collections.singletonList(itemRepository.findItemsByItemType(ItemType.FRESH).get());
//        System.out.println(imageBaos);
//        byte[] firstImage = imageBaos.get(0).get(1).getImage();
//
//        //BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBaos));
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(firstImage);

        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Login successful");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/getItems")
    public List<Item> getAllItems() {
        // return item name, item price, item image and item type
        ArrayList<Item> items = (ArrayList<Item>) itemService.getAllItems();
        for (Item item : items) {
            item.setId(null);
            item.setBarcodeImage(null);
            item.setBarcodeText(null);
            item.setCreatedAt(null);
            item.setExpirationTime(null);
            item.setQrCodeImage(null);
            item.setQrCodeText(null);
            item.setQuantity(0);
        }

        return items;
    }
}

