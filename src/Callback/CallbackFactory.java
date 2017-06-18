package Callback;

/**
 * CallbackFactory is used to create different types of callback objects
 * from the String passed to its createCallBack method.
 * For differentiation purpose, i have named other callback types as sync and async
 */
public class CallbackFactory{

    // This method uses the factory design pattern to create objects at runtime.
    public Callback createCallBack(String type) {

        if(type == "sync") {
            return new SynchronousCallback();
        } else if(type == "async") {
            return new AsynchronousCallback();
        } else { // Default
            return new Callback();
        }

    }
}
