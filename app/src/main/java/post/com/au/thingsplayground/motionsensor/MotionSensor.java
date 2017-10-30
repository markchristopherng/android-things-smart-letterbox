package post.com.au.thingsplayground.motionsensor;

public interface MotionSensor {

    void startup();

    void shutdown();

    interface Listener {
        void onMovement();
    }
}
