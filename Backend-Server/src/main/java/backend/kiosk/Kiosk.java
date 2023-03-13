package backend.kiosk;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "_kiosks")
public class Kiosk implements UserDetails {
    @Id
    @SequenceGenerator(
            name = "kiosks_sequence",
            sequenceName = "kiosks_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "kiosks_sequence"
    )
    private Long id;

    @Column(unique = true)
    private String unitId;

    private boolean isAvailable = true;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private int failedLoginAttempts = 0;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    private String default_password;

    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_KIOSK");

    public Kiosk(String unitId, String password) {
        this.unitId = unitId;
        this.default_password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return default_password;
    }

    @Override
    public String getUsername() {
        return unitId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }
}