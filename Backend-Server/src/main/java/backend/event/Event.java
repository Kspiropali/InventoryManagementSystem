package backend.event;

import org.springframework.context.ApplicationEvent;

public class Event extends ApplicationEvent {
    private final String message;

    public Event(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}