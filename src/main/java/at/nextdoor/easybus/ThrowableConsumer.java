package at.nextdoor.easybus;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by romikk on 10/03/15.
 */
@FunctionalInterface
public interface ThrowableConsumer<T> extends BiConsumer<T,Consumer<Throwable>>{

    default void accept(T o, Consumer<Throwable> errorHandler){
        try {
            acceptThrow(o);
        } catch (Throwable t) {
            errorHandler.accept(t);
        }
    }

    void acceptThrow(T o) throws Throwable;
}
