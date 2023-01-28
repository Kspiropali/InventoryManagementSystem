package backend.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class Listener implements ApplicationListener<Event> {
    @Override
    public void onApplicationEvent(Event event) {
        System.out.println("Received spring custom event - " + event.getMessage());
    }
}
