package Application;

import org.json.simple.JSONObject;

/**
 * Interface for the QueueSystem which contains the common functionalities
 * exposed to other objects.
 */
public interface QueueContainer {
    public void subscribe(String msg, Consumer cons);
    public void notifyConsumers(String  msg);
    public void addToQueue(JSONObject json);
    public void addDependency(Consumer a, Consumer b);
}
