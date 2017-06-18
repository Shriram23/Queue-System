package Callback;

import Application.Consumer;

/**
 * SynchronousCallback is a variation of callback to show differences in the various callbacks registered.
 * It containes a consumer object to identify the consumer who registered for it.
 */
public class SynchronousCallback extends Callback {

    Consumer consumer;

    // Getter function.
    public Consumer getConsumer() {
        return consumer;
    }

    // Setter function.
    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    // Prints the message when the registered callback is submitted and returns the customer who called it.
    @Override
    public Object call() throws Exception {
        // consumer callback.
        String message = "Method Synchronous call back from consumer received";
        System.out.println(message);
        return this.consumer;
    }
}
