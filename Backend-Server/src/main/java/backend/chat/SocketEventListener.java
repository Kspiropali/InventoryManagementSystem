package backend.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.String.format;

@Component
@AllArgsConstructor
@Getter
public class SocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SocketEventListener.class);
    private final SimpMessageSendingOperations messagingTemplate;
    //logged in users
    private ArrayList<String> users;

    //handles when a user connects to the chat
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = Objects.requireNonNull(headerAccessor.getUser()).getName();
        System.out.println("Calling login function: before adding:"+users);
        if(!users.contains(username)) {
            users.add(username);
        }
        System.out.println("Calling login function: after adding:"+users);
        logger.info("Received a new web socket connection. from User: " + username);
    }

    //handles when a user disconnects from the chat
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
        String destination = (String) headerAccessor.getSessionAttributes().get("destination");
        //System.out.println("ROOMID"+roomId);
        if (username != null) {
            System.out.println("Calling logout function: before removing:"+users);
            logger.info("User Disconnected: " + username);

            Message chatMessage = new Message();
            chatMessage.setType(String.valueOf(Message.MessageType.LEAVE));
            chatMessage.setSender(username);
            /*System.out.println("User leaving the chat and joining: "+destination);*/
            chatMessage.setDestination(destination);
            users.remove(username);
            /*System.out.println("Calling logout function: after removing:"+users);*/
            messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
        }
    }

    //handles when a user joins a chat aka online status
    public ArrayList<String> getUsers() {
        return users;
    }
}

