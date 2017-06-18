package Callback;

import Application.Consumer;

/**
 * AsynchronousCallback is a variation of callback to show differences in the various callbacks registered.
 * It containes a consumer object to identify the consumer who registered for it.
 */
public class AsynchronousCallback extends Callback {

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
        String message = "Method Asynchronous call back from consumer received";
        System.out.println(message);
        return this.consumer;
    }
}
