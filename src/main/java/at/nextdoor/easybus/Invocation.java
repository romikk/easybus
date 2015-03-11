package at.nextdoor.easybus;


/**
* Created by romikk on 06/03/15.
*/
final class Invocation<T> {
    public final Object listener;
    public final String methodName;
    public final ThrowableConsumer<T> handler;
    public final Class<T> paramType;

    Invocation(Object listener, final String methodName, ThrowableConsumer<T> handler, Class<T> paramType) {
        this.listener = listener;
        this.methodName = methodName;
        this.handler = handler;
        this.paramType = paramType;
    }
}
