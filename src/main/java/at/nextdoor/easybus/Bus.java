package at.nextdoor.easybus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

/**
 * Created by romikk on 10/03/15.
 */
public class Bus implements PubSub {
    private final InvocationResolverService invocationResolver = InvocationResolverService.instance(Subscribe.class);
    private final ConcurrentMap<Class, List<Invocation>> listeners = new ConcurrentHashMap<>();
    private final ConcurrentMap<Object, List<Runnable>> removals = new ConcurrentHashMap<>();
    private final Consumer<PublicationError> errorHandler;
    private final ForkJoinPool threadPool;

    public Bus(final Consumer<PublicationError> errorHandler, final ForkJoinPool threadPool) {
        this.errorHandler = errorHandler;
        this.threadPool = threadPool;
    }

    public <E> void publish(final E event) {
        final List<Invocation> invocations = listeners.get(event.getClass());
        if (invocations != null) {
            publish(event, invocations);
        }
    }

    public <E> void publishAsync(final E event) {
        final List<Invocation> invocations = listeners.get(event.getClass());
        if (invocations != null) {
            publishAsync(event, invocations);
        }
    }

    /**
     * This saves a hashmap lookup for even shorter path from publisher to listener
     */
    public <E> Publisher<E> publisher(final Class<E> eventType) {
        final List<Invocation> invocationList = listeners.computeIfAbsent(eventType, t -> new CopyOnWriteArrayList<>());
        return (event) -> publish(event, invocationList);
    }

    public <E> Publisher<E> publisherAsync(final Class<E> eventType) {
        final List<Invocation> invocationList = listeners.computeIfAbsent(eventType, t -> new CopyOnWriteArrayList<>());
        return (event) -> publishAsync(event, invocationList);
    }

    @SuppressWarnings("unchecked")
    private <E> void publish(final E event, final List<Invocation> invocations) {
        invocations.forEach(inv -> inv.handler.accept(event, errorConsumer(event, inv)));
    }

    @SuppressWarnings("unchecked")
    private <E> void publishAsync(final E event, final List<Invocation> invocations) {
        threadPool.submit(() ->
                invocations.parallelStream().forEach(inv ->
                        inv.handler.accept(event, errorConsumer(event, inv))));
    }

    private <E> Consumer<Throwable> errorConsumer(final E event, final Invocation inv) {
        return (ex)->errorHandler.accept(new PublicationError(inv.listener, inv.methodName, event,ex));
    }

    public void subscribe(final Object listener) {

        final List<Runnable> removalTasks = removals.computeIfAbsent(listener, l -> new CopyOnWriteArrayList<>());

        invocationResolver.resolveInvocations(listener).forEach(inv -> {
            final List<Invocation> invocationList = listeners.computeIfAbsent(inv.paramType, t -> new CopyOnWriteArrayList<>());
            invocationList.add(inv);
            removalTasks.add(() -> invocationList.remove(inv));
        });
    }

    public void unsubscribe(final Object listener) {
        final List<Runnable> removalTasks = removals.remove(listener);
        if (removalTasks != null) {
            removalTasks.forEach(Runnable::run);
        }
    }

    public void dispose() {
        listeners.clear();
        removals.clear();
    }
}
