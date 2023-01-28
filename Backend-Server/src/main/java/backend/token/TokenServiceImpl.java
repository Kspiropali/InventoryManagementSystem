package backend.token;

import backend.admin.Admin;
import backend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    public void removeTokenByToken(String token) {
        tokenRepository.removeByToken(token);
    }

    @Override
    public Token createToken(User user) {
        String token = UUID.randomUUID().toString();
        Token verificationToken = new Token(user, token);

        tokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public Token createToken(Admin admin) {
        String token = UUID.randomUUID().toString();
        Token verificationToken = new Token(admin, token);

        tokenRepository.save(verificationToken);

        return verificationToken;
    }


    @Override
    public Object validateToken(String token) {
        Token foundToken = tokenRepository.findByToken(token);

        if (foundToken == null) {
            try {
                throw new Exception("Token is Invalid or used");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Calendar cal = Calendar.getInstance();

        if ((foundToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(foundToken);
            try {
                throw new Exception("Token is expired");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(foundToken.getAdmin()==null){
            return foundToken.getUser();
        }
        return foundToken.getAdmin();
    }


    public Token generateNewToken(String oldToken) {
        //finding the old token provided
        Token newToken = tokenRepository.findByToken(oldToken);
        //generating and assigning new token
        newToken.setToken(UUID.randomUUID().toString());
        //saving it to token db
        tokenRepository.save(newToken);

        return newToken;
    }
}
