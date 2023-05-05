package backend.admin;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements UserDetailsService, AdminService {
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findAdminByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format("Admin with email %s not found", email)));
    }

    @Override
    public Admin registerAdmin(Admin admin) {
        return null;
    }

    @Override
    public boolean checkIfAdminExist(String email) {
        return false;
    }

    @Override
    public void activateAdminAccount(Admin admin) {

    }

    @Override
    public String sendRegistrationConfirmationEmail(String email, String token) {
        return null;
    }

}
