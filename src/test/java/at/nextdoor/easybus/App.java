package at.nextdoor.easybus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

/**
 * Hello world!
 *
 */
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {
        new App().doStuff();
    }

    public void doStuff() {
        Bus bus = new Bus((e)->LOG.error("{}", e, e.getError()), new ForkJoinPool());
        bus.subscribe(new Handler());

        bus.publish("Test sync event");
        bus.publishAsync("Test async event");
        bus.publisher(String.class).publish("Test sync publisher");
        bus.publisherAsync(String.class).publish("Test async publisher");

//        bus.publish(new Object());

    }

    public class Handler {

        @Subscribe
        public void handle(String event) throws Exception {
            LOG.info("Event: {}", event);
        }

        @Subscribe
        void handle(Object event) throws Exception {
            throw new Exception("boom!");
        }
    }
}
