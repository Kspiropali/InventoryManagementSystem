package backend.token;

import backend.admin.Admin;
import backend.user.User;

public interface TokenService {
    void removeTokenByToken(String token);

    Token createToken(User user);

    Token createToken(Admin admin);

    Object validateToken(String token);

    Token generateNewToken(String oldToken);
}
