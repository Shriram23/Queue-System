package Application;

import Callback.CallbackFactory;

/**
 * The main(test) class to demo the queue service
 */
public class PhonePeDemoApp {
    public static void  main(String args[]) {

        // Creating queue object.
        QueueContainer qc = new QueueSystemImpl();

        // Producer creation
        Producer producer = new Producer(qc);

        // Using Factory pattern to generate different types of callbacks.
        CallbackFactory callbackFactory = new CallbackFactory();

        // Creating 3 different types of consumers
        Consumer consumer1 = new Consumer(qc, "Age", callbackFactory.createCallBack("sync"));
        Consumer consumer2 = new Consumer(qc, "Age", callbackFactory.createCallBack("async"));
        Consumer consumer3 = new Consumer(qc, "Age", callbackFactory.createCallBack("Default"));

        // Subscribing the messages from queue
        consumer1.subscribe();
        consumer2.subscribe();
        consumer3.subscribe();

        // Creating dependency between consumers. Similar to C -> (A,B)
        qc.addDependency(consumer3, consumer1);
        qc.addDependency(consumer3, consumer2);

        // Producing inputs to the queue.
        producer.produce();
    }
}
