package backend.user;

import backend.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;


    public User registerUser(User user) {
        if (checkIfUserExist(user.getEmail())) {
            try {
                throw new Exception("User already exists with this email");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //Password validation needed


        User user_create = new User();
        user_create.setEmail(user.getEmail());
        user_create.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user_create);

        return user_create;
    }

    public void activateUserAccount(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    public boolean checkIfUserExist(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    public void sendRegistrationConfirmationEmail(String sendTo, String token) {
        emailService.sendSimpleMail(sendTo, token);
       // emailService.sendMailWithAttachment(sendTo, token);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format("user with email %s not found", email)));
    }
}


