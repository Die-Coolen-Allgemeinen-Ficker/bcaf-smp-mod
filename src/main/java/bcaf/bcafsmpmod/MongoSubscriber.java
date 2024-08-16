package bcaf.bcafsmpmod;

import java.util.ArrayList;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MongoSubscriber<T> implements Subscriber<T> {
    public static interface NextCallback<T> {
        void callback(T t);
    }
    
    public static interface CompleteCallback<T> {
        void callback(ArrayList<T> received);
    }

    private NextCallback<T> nextCallback;
    private CompleteCallback<T> completeCallback;
    private ArrayList<T> received = new ArrayList<T>();

    public MongoSubscriber () {}

    @Override
    public void onSubscribe (Subscription s) {
        s.request(Integer.MAX_VALUE);
    }

    @Override
    public void onNext (T t) {
        received.add(t);
        if (nextCallback != null)
            nextCallback.callback(t);
    }

    @Override
    public void onError (Throwable t) {
        Bcafsmpmod.LOGGER.error("MongoSubscriber encountered an error");
        t.printStackTrace();
    }

    @Override
    public void onComplete () {
        if (completeCallback != null)
            completeCallback.callback(received);
    }

    public void setNextCallback (NextCallback<T> callback) {
        this.nextCallback = callback;
    }

    public void setCompleteCallback (CompleteCallback<T> callback) {
        this.completeCallback = callback;
    }
}