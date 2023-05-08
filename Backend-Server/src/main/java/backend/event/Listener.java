package backend.event;

import backend.chat.ChatController;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class Listener implements ApplicationListener<Event> {
    private final ChatController chatController;
    @Override
    public void onApplicationEvent(Event event) {
        if (Objects.equals(event.getType(), "user register")) {
            System.out.println("New user registration detected, updating user list(username: " + event.getMessage() + ")");
            chatController.sendRegistrationMessage(event.getMessage());
        }else if(Objects.equals(event.getType(), "user deletion")){
            System.out.println("User deletion detected, deleting user from list(username: " + event.getMessage() + ")");
            chatController.sendRemovalMessage(event.getMessage());
        }

    }
}
