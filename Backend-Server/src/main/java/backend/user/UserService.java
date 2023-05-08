package backend.user;

public interface UserService {
    User registerUser(User user);
    void activateUserAccount(User user);

    void sendRegistrationConfirmationEmail(String sendTo, String token);

    Long getTotalUsers();

    Long getUsersLast24Hours();

    String getRegionalUsers();
}