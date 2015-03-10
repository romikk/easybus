package at.nextdoor.easybus;

/**
 * Created by romikk on 10/03/15.
 */
public interface Publisher<E> {

    void publish(E event);
}
