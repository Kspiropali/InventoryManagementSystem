package backend.token;

import backend.admin.Admin;
import backend.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "_tokens")
public class Token {

    //Expiration time 10 minutes
    private static final int EXPIRATION_TIME = 10;


    @Id
    @SequenceGenerator(
            name = "tokens_sequence",
            sequenceName = "tokens_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tokens_sequence"
    )
    private Long id;

    @Column(unique = true)
    private String token;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @Column(updatable = false)
    @Basic(optional = false)
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id",
            foreignKey = @ForeignKey(name = "FK_USER_TOKEN"))
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admins_id",
            foreignKey = @ForeignKey(name = "FK_ADMIN_TOKEN"))
    private Admin admin;

    public Token(User user, String token) {
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = calculateExpirationDate();
    }

    public Token(Admin admin, String token) {
        super();
        this.token = token;
        this.admin = admin;
        this.expirationTime = calculateExpirationDate();
    }

    private Date calculateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, Token.EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }

}


