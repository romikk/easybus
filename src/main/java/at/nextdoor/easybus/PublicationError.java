package at.nextdoor.easybus;

/**
 * Created by romikk on 10/03/15.
 */
public class PublicationError {
    private final Object listener;
    private final String methodName;
    private final Object event;
    private final Throwable error;

    PublicationError(final Object listener, final String methodName, final Object event, final Throwable error) {
        this.listener = listener;
        this.methodName = methodName;
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

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "PublicationError{" +
                "listener=" + listener +
                ", methodName='" + methodName + '\'' +
                ", event=" + event +
                ", error=" + error +
                '}';
    }
}
