package backend.admin;


import backend.security.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminConfig(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        addAdmins();
    }

    public void addAdmins() {
        //Testing only, Setup admin for testing
        Admin admin = new Admin("admin", passwordEncoder.encode("admin"));
        admin.setEnabled(true);
        adminRepository.save(admin);
    }
}
