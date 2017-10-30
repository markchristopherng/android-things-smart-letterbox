package post.com.au.thingsplayground.motionsensor;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;

import java.io.IOException;

public class PirMotionSensor implements MotionSensor {

    private final Gpio bus;

    private final MotionSensor.Listener listener;

    public PirMotionSensor(Gpio bus, Listener listener) {
        this.bus = bus;
        this.listener = listener;
    }

    @Override
    public void startup() {
        try {
            bus.setDirection(Gpio.DIRECTION_IN);
            bus.setActiveType(Gpio.ACTIVE_HIGH);
            bus.setEdgeTriggerType(Gpio.EDGE_RISING);
        } catch (IOException e) {
            throw new IllegalStateException("Sensor can't start", e);
        }
        try {
            bus.registerGpioCallback(callback);
        } catch (IOException e) {
            throw new IllegalStateException("Sensor can't register callback", e);
        }
    }

    private final GpioCallback callback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.d("PirMotionSensor", "motion detected");
            listener.onMovement();
            return true; // True to continue listening
        }
    };

    @Override
    public void shutdown() {
        bus.unregisterGpioCallback(callback);
        try {
            bus.close();
        } catch (IOException e) {
            Log.e("TUT", "Failed to shut down.", e);
        }
    }
}
