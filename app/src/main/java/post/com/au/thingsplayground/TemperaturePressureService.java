package post.com.au.thingsplayground;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver;

import java.io.IOException;

/**
 * Service for monitoring temperature pressure
 */
public class TemperaturePressureService extends Service {
    private static final String TAG = TemperaturePressureService.class.getSimpleName();
    private static final String I2C_BUS = "I2C1";

    private Bmx280SensorDriver mTemperatureSensorDriver;

    @Override
    public void onCreate() {
        setupTemperaturePressureSensor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyTemperaturePressureSensor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void setupTemperaturePressureSensor() {
        try {
            mTemperatureSensorDriver = new Bmx280SensorDriver(I2C_BUS);
            mTemperatureSensorDriver.registerTemperatureSensor();
        } catch (IOException e) {
            Log.e(TAG, "Error configuring sensor", e);
        }
    }

    private void destroyTemperaturePressureSensor() {
        if (mTemperatureSensorDriver != null) {
            mTemperatureSensorDriver.unregisterTemperatureSensor();
            try {
                mTemperatureSensorDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing sensor", e);
            } finally {
                mTemperatureSensorDriver = null;
            }
        }
    }

}
