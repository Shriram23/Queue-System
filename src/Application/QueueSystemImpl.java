package Application;

import Dependency.DependencyManager;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * QueueSystemImpl is our main application and it implements QueueContainer
 * MAX_QUEUE_SIZE = max size of the queue.
 * messageQueue = JSONMessages queue where producers can produce messages and consumers consume.
 * current_queue_size = variable to determine current queue size.
 * subscribedConsumers = list of consumers who have subscribed for any message using queue.
 * consumersMap = A Map with messages as the key and the list of consumers who have subscribed for the match as the Value.
 * pool = Execution service object to be passed on to ExecutorCompletionService.
 * callbackService = used to execute the registered callback methods from the consumer.
 * dependencyManager = object to manage dependencies between the consumers.
 * MAX_RETRY_COUNT = Maximum retry cap for the number of times we can retry the failed logic.
 */
public class QueueSystemImpl implements QueueContainer {
    private int MAX_QUEUE_SIZE = 100;
    private ArrayBlockingQueue<JSONObject> messageQueue;
    private static int current_queue_size = 0;
    private ArrayList<Consumer> subscribedConsumers;
    private Map<String,ArrayList<Consumer>> consumersMap;
    private ExecutorService servicePool = Executors.newFixedThreadPool(5);
    private ExecutorCompletionService callbackService = new ExecutorCompletionService(servicePool);
    private DependencyManager dependencyManager;
    private int MAX_RETRY_COUNT = 5;


    // Constructor for QueueSystem.
    public QueueSystemImpl() {
        subscribedConsumers =  new ArrayList<Consumer>();
        messageQueue = new ArrayBlockingQueue<JSONObject>(MAX_QUEUE_SIZE);
        consumersMap = new HashMap<>();
        dependencyManager = new DependencyManager();
    }

    // Allows consumers to subscribe to messages of their preferred messages.
    @Override
    public void subscribe(String msg, Consumer subscribingCustomer) {
        subscribedConsumers.add(subscribingCustomer);
        // Add subscription to the message queue
        ArrayList<Consumer> existingConsumers = consumersMap.get(msg);
        if(existingConsumers == null) {
            // First consumer subscribing for this message.
            existingConsumers = new ArrayList<Consumer>();
        }
        existingConsumers.add(subscribingCustomer);
        consumersMap.put(msg, existingConsumers);
    }

    // Method to notify consumers when a new message has arrived.
    @Override
    public void notifyConsumers(String msg) {
        ArrayList<Consumer> registeredConsumers = consumersMap.get(msg);
        // Return if no consumers have registered for the message
        if(registeredConsumers == null) {
            return;
        }
        int notification_counter = 0;
        for(Iterator<Consumer> it = registeredConsumers.iterator(); it.hasNext();) {
            Consumer registeredConsumer = it.next();
            boolean isSubmitted =  notifyConsumer(registeredConsumer, msg);
            if(isSubmitted) {
                notification_counter++;
            }
        }
        setConsumedStatus(notification_counter);
        retryForBlockedConsumers(msg);
    }

    // call getFutureCompletion method for the consumers who have been notified about new message
    // and we are waiting for them to complete.
    private void setConsumedStatus(int counter) {
        for(int i=0 ; i < counter ; i++)
        {
            getFutureCompletion();
        }
    }

    // Method used to retry the execution of registered callback for consumers who are waiting
    // for the completion of queue consumption from the consumers they are dependent on.
    private void retryForBlockedConsumers(String message) {
        // Get the list of blocked Consumers and do not retry if no one is blocked.
        BlockingQueue<Consumer> blockedConsumers = dependencyManager.getBacklogQueue();

        if(blockedConsumers == null) {
            return;
        }

        int retry_counter = 0;
        // Keep retrying till we reach the maximum retry limit which we have set internally.
        while(retry_counter < MAX_RETRY_COUNT) {
            boolean executionResult = executeBlockingConsumers(blockedConsumers, message);
            if(executionResult) {
                System.out.println("All Blocked Consumers have finshed executing");
                return;
            }
            retry_counter++;
        }
    }

    // Take the list of completed consumers from callbackService and set isConsumed to false
    // as they have completed consuming from the queue.
    private void getFutureCompletion() {
        try {
            Future future = callbackService.take();
            Consumer finishedConsumer = (Consumer) future.get();
            finishedConsumer.isConsumed = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Try to execute the callback for blocked consumers.
    private boolean executeBlockingConsumers(BlockingQueue<Consumer> blockedConsumers, String msg) {
        int blockedConsumerSize = blockedConsumers.size();
        for(Iterator<Consumer> it = blockedConsumers.iterator(); it.hasNext();) {
            Consumer blockedConsumer = it.next();
            boolean isSubmitted = notifyConsumer(blockedConsumer, msg);
            if(isSubmitted) {
                blockedConsumerSize --;
                getFutureCompletion();
            }
        }
        return blockedConsumerSize <= 0;
    }

    // Method to notify individual consumer.
    private boolean notifyConsumer(Consumer consumer, String msg) {
        // Check for dependency between consumers
        if(dependencyManager.isDependenciesResolved(consumer)) {
            consumer.isConsumed = true;
            callbackService.submit(consumer.getCallback());
            return true;
        } else {
            // Check if the consumer is already waiting in the backlogQueue.
            if(dependencyManager.getBacklogQueue().contains(consumer) == false) {
                dependencyManager.getBacklogQueue().add(consumer);
            }
            System.out.println("This consumer " + consumer.message + "is waiting");
            return false;
        }
    }

    // Method to add dependency between 2 consumers.
    public void addDependency(Consumer requestingConsumer, Consumer dependentConsumer) {
        dependencyManager.addDependency(requestingConsumer, dependentConsumer);
    }

    // Method to add messages into the queue
    @Override
    public void addToQueue(JSONObject object) {
        // Check for QUEUE overflow.
        if(current_queue_size >= MAX_QUEUE_SIZE) {
            System.out.println("Queue overflow");
            // remove the first element we added to the queue
            // TODO : implement LRU cache type to guarantee high availability.
            messageQueue.remove(0);
        }
        current_queue_size++;
        messageQueue.add(object);
        // Parse through the object and notify consumers if the messages they subscribed for has arrived.
        for(Iterator<String> it = object.keySet().iterator(); it.hasNext(); ) {
            String subscriptionMessage = it.next();
            notifyConsumers(subscriptionMessage);
        }
    }
}
