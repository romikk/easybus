package at.nextdoor.easybus;

/**
 * Created by romikk on 10/03/15.
 */
public class InvocationError {
    private final Object listener;
    private final Object event;
    private final Throwable error;

    InvocationError(final Object listener, final Object event, final Throwable error) {
        this.listener = listener;
        this.event = event;
        this.error = error;
    }

    public Object getListener() {
        return listener;
    }

    public Object getEvent() {
        return event;
    }

    public Throwable getError() {
        return error;
    }
}
