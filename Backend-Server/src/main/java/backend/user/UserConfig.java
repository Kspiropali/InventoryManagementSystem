package backend.user;

import backend.security.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;


@Configuration
public class UserConfig {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    public UserConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        addUsers();
    }

    public void addUsers() {
        //Testing only, Setup couple of users for testing
        User bob = new User("bob@bob.com", passwordEncoder.encode("testingbob123!"));
        User sam = new User("sam@sam.com", passwordEncoder.encode("testingsam123!"));
        User john = new User("john@john.com", passwordEncoder.encode("testingjohn123!"));
        bob.setEnabled(true);
        sam.setEnabled(true);
        john.setEnabled(true);
        ArrayList<User> users = new ArrayList<>();
        users.add(bob);
        users.add(sam);
        users.add(john);
        userRepository.saveAll(users);
    }
}
