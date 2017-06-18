package Application;

import org.json.simple.JSONObject;

/**
 * Producer class which produces JSON objects to the queue
 * has a queue object to access queue's functionalities.
 */
public class Producer {

    private QueueContainer queueContainer;

    // Constructor for producer.
    public Producer(QueueContainer queueContainer) {
        this.queueContainer = queueContainer;
    }

    // Returns the JSONObject from the input given by the producer.
    private JSONObject getInputJSONObjects(String name, int age, String school) {
        JSONObject object = new JSONObject();
        object.put("Name", name);
        object.put("Age", age);
        object.put("School", school);
        return object;
    }

    // produces custom generated JSONMessages into the queue.
    // The JSON messages have been hardcoded to ease the implementation.
    // We can enhance the production by asking input from the user.
    public void produce() {
        JSONObject obj1 = getInputJSONObjects("Arjun", 19, "Vishwa");
        JSONObject obj2 = getInputJSONObjects("Shivaji", 20, "Vivekananda");
        JSONObject obj3 = getInputJSONObjects("Ajay", 12, "Ahobilam");
        queueContainer.addToQueue(obj1);
        queueContainer.addToQueue(obj2);
        queueContainer.addToQueue(obj3);
    }
}
