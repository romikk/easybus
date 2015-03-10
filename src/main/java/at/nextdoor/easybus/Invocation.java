package at.nextdoor.easybus;


/**
* Created by romikk on 06/03/15.
*/
final class Invocation<T> {
    public final Object listener;
    public final ThrowableConsumer<T> handler;
    public final Class<T> paramType;

    Invocation(Object listener, ThrowableConsumer<T> handler, Class<T> paramType) {
        this.listener = listener;
        this.handler = handler;
        this.paramType = paramType;
    }
}
