package backend.user;

import backend.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;


    public User registerUser(User user) {
        // check if username exists
        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("username already exists");
        }
        // check if email exists
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("email already exists");
        }


        User user_create = new User();
        user_create.setUsername(user.getUsername());
        user_create.setEmail(user.getEmail());
        user_create.setPassword(passwordEncoder.encode(user.getPassword()));
        user_create.setCreatedAt(new Date());

        userRepository.save(user_create);

        return user_create;
    }

    public void activateUserAccount(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void sendRegistrationConfirmationEmail(String sendTo, String token) {
        emailService.sendSimpleMail(sendTo, token);
       // emailService.sendMailWithAttachment(sendTo, token);
    }

    @Override
    public Long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public Long getUsersLast24Hours() {
        return userRepository.countUsersByCreatedAtAfter(new java.util.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
    }

    @Override
    public String getRegionalUsers() {
        // get regions and amount of users in each region
        // return as json
        HashMap<String, Long> regionalUsers = new HashMap<>();
        for (Region region : Region.values()) {
            regionalUsers.put(String.valueOf(region), userRepository.countUsersByRegion(region));
        }
        return regionalUsers.toString();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format("user with email %s not found", username)));
    }
}


