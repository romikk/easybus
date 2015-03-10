# EasyBus
Fast event bus without reflection

 When I was looking for an Event Bus with API similar to the one in Guava, I was surprised that I couldn't find any that
 would be fast enough to be used in low latency applications (e.g. HFT). So I decided to make my own.
 The main drawback in all of the implementation that I've seen is that they all use reflection for listener method
 invocation, i.e. ```Method.invoke()```. Unfortunately this approach is not acceptable in low latency world and
 therefore I decided to use excellent OpenHFT/Java-Runtime-Compiler library to generate lambdas for handler methods at
 runtime. This way handler method gets statically linked and this results in shortest possible path from event publisher
 to event handler. LCompilation happens only at first subscription for a given handler class, results of that

## Features
- Fast listener invocation. It is as fast as hashmap lookup plus lambda invocation.
- Lightweight
- Simple

## Limitations
- Does not support private methods as handlers. And probably will never do.
- Requires JDK at runtime since it need to generate and compile code one the fly.
- Currently Java 1.8 only

## Usage
```
public class Handler {
    @Subscribe
    public void handle(MyEvent event) {
        ...
    }
    @Subscribe
    void handle(MyOtherEvent event) throws Exception {
        ...
    }
}
```