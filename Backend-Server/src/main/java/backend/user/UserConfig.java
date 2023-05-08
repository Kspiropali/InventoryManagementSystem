package backend.user;

import backend.event.Publisher;
import backend.security.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Date;


@Configuration
public class UserConfig {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Publisher eventPublisher;

    public UserConfig(UserRepository userRepository, PasswordEncoder passwordEncoder, Publisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        addUsers();
    }

    public void addUsers() {
        ArrayList<User> users = new ArrayList<>();

        // Testing only, Setup couple of users for testing
        // create users with createdAt date of last month for testing
        User bob = new User("bob","bob@bob.com", passwordEncoder.encode("testingbob123!"), new Date(System.currentTimeMillis() - 2629746000L));
        User sam = new User("sam","sam@sam.com", passwordEncoder.encode("testingsam123!"), new Date(System.currentTimeMillis() - 2629746000L));
        User john = new User("john","john@john.com", passwordEncoder.encode("testingjohn123!"), new Date(System.currentTimeMillis() - 2629746000L));

//        for (int i = 0; i < 100; i++) {
//            User user = new User("user" + i + "@gmail", "password");
//            user.setEnabled(true);
//            users.add(user);
//        }
        bob.setEnabled(true);
        sam.setEnabled(true);
        john.setEnabled(true);

        users.add(bob);
        users.add(sam);
        users.add(john);
        System.out.println("Users added to database"+ users.get(0).getUsername());
        eventPublisher.publishCustomEvent(bob.getUsername(), "user register");
        eventPublisher.publishCustomEvent(sam.getUsername(), "user register");
        eventPublisher.publishCustomEvent(john.getUsername(), "user register");
        userRepository.saveAll(users);
    }
}
