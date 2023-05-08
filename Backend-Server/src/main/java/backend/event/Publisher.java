package backend.event;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Publisher {


    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final String message, final String type) {
        /*System.out.println("Publishing custom event. ");*/
        Event customSpringEvent = new Event(this, message, type);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}