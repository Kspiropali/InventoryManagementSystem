package backend.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        System.out.println("--------------Initialized /ws websocket!----------");
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/channel");
    }


    //setting limits to socket buffer, timeouts and message size limits
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        System.out.println("--------------Websocket limits set successfully----------");
        registration.setMessageSizeLimit(200000); // default : 64 * 1024
        registration.setSendTimeLimit(15 * 10000); // default : 10 * 10000
        registration.setSendBufferSizeLimit(512 * 1024); // default : 512 * 1024

    }
}