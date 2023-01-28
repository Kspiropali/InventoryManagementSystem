package backend.admin;

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
@Table(name = "_admins")
public class Admin implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "admins_sequence",
            sequenceName = "admins_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "admins_sequence"
    )
    @Column(unique = true)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(length = 60)
    private String password;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private int failedLoginAttempts = 0;
    private boolean credentialsNonExpired = true;
    private boolean enabled = false;

    GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");

    public Admin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return this.getEmail();
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

