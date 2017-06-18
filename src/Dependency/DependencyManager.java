package Dependency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import Application.Consumer;

/**
 * Class to manage and add dependencies between various consumers
 * MAX_DEPENDENCIES = maximum number of dependencies a consumer can have
 * dependentConsumers = Map which has Consumer as key and list of consumers on whom this consumer is dependent on as value
 * backlogQueue = contains the consumers who are waiting for their callbacks to be executed
 * as they are blocked because of other consumers
 */
public class DependencyManager {
    private int MAX_DEPENDENCIES = 100;
    private Map<Consumer, List<Consumer>> dependentConsumers;
    private ArrayBlockingQueue<Consumer> backlogQueue;

    // Constructor for DependencyManager
    public DependencyManager() {
        dependentConsumers = new ConcurrentHashMap<Consumer, List<Consumer>>();
        backlogQueue = new ArrayBlockingQueue<Consumer>(MAX_DEPENDENCIES);
    }

    public List<Consumer> getDependencies(Consumer consumer) {
        return dependentConsumers.get(consumer);
    }

    // Method to add dependency between 2 consumers
    public void addDependency(Consumer clientConsumer, Consumer dependentConsumer ) {
        List<Consumer> existingDependentConsumers = dependentConsumers.get(clientConsumer);
        if(existingDependentConsumers == null) {
            existingDependentConsumers = new ArrayList<Consumer>();
        }
        existingDependentConsumers.add(dependentConsumer);
        dependentConsumers.put(clientConsumer, existingDependentConsumers);
    }

    // Method to determine if there are any other consumers dependent on the message this consumer is consuming,
    public boolean isDependenciesResolved(Consumer consumer) {
        List<Consumer> dependentConsumers = getDependencies(consumer);
        if(dependentConsumers == null) {
            return true;
        }
        for(Iterator<Consumer> it = dependentConsumers.iterator(); it.hasNext();) {
            Consumer currentConsumer = it.next();
            // isConsumed will be false if the consumption has finished
            // isConsumed is to indicate that the message is being consumed.
            if(currentConsumer.isConsumed) {
                return false;
            }
        }
        return true;
    }

    public Map<Consumer, List<Consumer>> getDependentConsumers() {
        return dependentConsumers;
    }

    public void setDependentConsumers(Map<Consumer, List<Consumer>> dependentConsumers) {
        this.dependentConsumers = dependentConsumers;
    }

    public BlockingQueue<Consumer> getBacklogQueue() {
        return backlogQueue;
    }

    public void setBacklogQueue(ArrayBlockingQueue<Consumer> backlogQueue) {
        this.backlogQueue = backlogQueue;
    }
}
