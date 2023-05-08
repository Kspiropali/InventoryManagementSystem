package backend.chat;

import backend.admin.Admin;
import backend.admin.AdminRepository;
import backend.event.Publisher;
import backend.user.User;
import backend.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RequestMapping(path = "/download")
@RestController
@AllArgsConstructor
@Getter @Setter
public class ChatWrapper implements UserDetailsService {
    private final SocketEventListener socketEventListener;
    private final MessageRepository messageRepository;
    private final Publisher eventPublisher;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    //registered users, the ones that are registered by the website
    //not the preexisting ones from userConfig
    private ArrayList<String> registeredUsers;

    @GetMapping("/chat/{roomId}/messages")
    public ArrayList<Object> getMessages(@PathVariable String roomId) {
        /*System.out.println(roomId);*/

        // finds all messages that are equal to a roomId(or topic)
        //and maps them to an arraylist: [[message1], [message2], [message3]]]
        // where message = ["id","content","sender","destination","type","timestamp"]
        ArrayList<Object> messages = messageRepository
                .findAll()
                .stream()
                .filter(message -> message.getDestination().equals(roomId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        System.out.println("--------------exporting online users----------");

        return messages;
    }

    @GetMapping("/chat/users")
    public ArrayList<String> getOnlineUsers() {
        //find all users that are connected to an opened socket
        System.out.println("--------------exporting online users----------");
        return socketEventListener.getUsers();
    }


    @GetMapping("/chat/registeredUsers")
    public ArrayList<String> getRegisteredUsers() {
        System.out.println("--------------exporting registered users----------");
        //returns all registered and activated=true users
        return registeredUsers;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username: " + username);
        User user = userRepository.findUserByEmail(username).orElse(null);
        if (user != null) {
            return user;
        }

        Admin admin = adminRepository.findAdminByEmail(username).orElse(null);
        if (admin != null) {
            return admin;
        }

        throw new UsernameNotFoundException("User/Admin not found");
    }
}
