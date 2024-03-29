package backend.user;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "_users")
public class User implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "users_sequence",
            sequenceName = "users_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_sequence"
    )
    @Column(unique = true)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @Column(length = 60)
    private String password;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private int failedLoginAttempts = 0;
    private boolean credentialsNonExpired = true;
    private boolean enabled = false;
    private Region region;
    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = new Date();
        // select a random region
        this.region = Region.values()[(int) (Math.random() * Region.values().length)];
    }

    public User(String username, String email, String password, Date createdAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        // set the createdAt to the last months date
        this.createdAt = createdAt;
        // select a random region
        this.region = Region.values()[(int) (Math.random() * Region.values().length)];
    }

    private Date createdAt;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return this.username;
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
