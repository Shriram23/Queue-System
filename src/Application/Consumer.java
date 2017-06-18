package Application;

import Callback.Callback;

import java.util.Scanner;

/**
 * Consumer class which contains callback for it to register with Queue service
 * A queue object
 * A message String which indicates the message it is subscribing for
 * An isConsumed flag which indicates whether it has finished consuming from the queue
 */
public class Consumer {

    private Callback callback;
    private QueueContainer queueContainer;
    public String message;
    public boolean isConsumed;

    // Constructor for consumer.
    public Consumer(QueueContainer queueContainer, String message, Callback callback) {
        this.queueContainer = queueContainer;
        this.message = message;
        this.callback = callback;
        this.callback.setConsumer(this);
    }

    // Subscribe method allows the consumers to subscribe to a particular message from the queue system.
    public void subscribe() {
        this.queueContainer.subscribe(this.message, this);
    }

    // Tells the callback registered to the queue system by this particular client.
    public Callback getCallback() {
        return this.callback;
    }
}
