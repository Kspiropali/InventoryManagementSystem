package backend.admin;

public interface AdminService {

    Admin registerAdmin(Admin admin);

    boolean checkIfAdminExist(String email);

    void activateAdminAccount(Admin admin);

    String sendRegistrationConfirmationEmail(String email, String token);

}
