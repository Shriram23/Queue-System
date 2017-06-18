package Callback;

import Application.Consumer;

import java.util.concurrent.Callable;

/**
 * Callback is the base(default) version of callback registered by the consumer.
 * It containes a consumer object to identify the consumer who registered for it.
 */
public class Callback implements Callable{

    Consumer consumer;

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    // Prints the message when the registered callback is submitted and returns the customer who called it.
    @Override
    public Object call() throws Exception {
        // consumer callback.
        String message = "Method call back from consumer received";
        System.out.println(message);
        return this.consumer;
    }
}
