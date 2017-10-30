package post.com.au.thingsplayground;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import post.com.au.thingsplayground.motionsensor.MotionSensor;
import post.com.au.thingsplayground.motionsensor.PirMotionSensor;

/**
 * base activity for wrapping up rainbow hat
 */

public class RainbowBaseActivity extends AppCompatActivity
        implements Button.OnButtonEventListener,
        SensorEventListener,
        MotionSensor.Listener{

    // display
    private static final String DISPLAY_I2C_BUS = "I2C1";
    private AlphanumericDisplay display;

    // buttons
    private static final String BUTTON_A_GPIO_PIN = "BCM21";
    private static final String BUTTON_B_GPIO_PIN = "BCM20";
    private static final String BUTTON_C_GPIO_PIN = "BCM16";

    private Button buttonA;
    private Button buttonB;
    private Button buttonC;

    protected enum WhichButton {A, B, C}

    // led lights
    private static final String LED_LEFT = "BCM6";
    private static final String LED_MID = "BCM19";
    private static final String LED_RIGHT = "BCM26";

    private Gpio ledRed;
    private Gpio ledGreen;
    private Gpio ledBlue;

    protected enum WhichLed {RED, GREEN, BLUE}

    // LED strip
    private static final String LED_STRIP = "SPI0.0";
    Apa102 ledStrip;

    // buzzer
    private static final String BUZZER = "BCM13";

    // motion sensor
    private static final String MOTION_SENSOR = "BCM18";
    PirMotionSensor pirMotionSensor;

    // services
    private SensorManager sensorManager;
    private PeripheralManagerService pioService = new PeripheralManagerService();

    // sensor
    private SensorManager.DynamicSensorCallback sensorCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLedStrip();
        setupAllButton();
        setupAlphanumericDisplay();
        setupAllLedLights();
        registerSensor();
        setUpMotionSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyLedStrip();
        destroyAllButton();
        destroyAlphanumericDisplay();
        destroyAllLights();
        unregisterSensor();
        destroyMotionSensor();
    }

    private void setupLedStrip() {
        try {
            ledStrip = new Apa102(LED_STRIP, Apa102.Mode.BGR);
            ledStrip.setBrightness(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyLedStrip() {
        if(ledStrip != null) {
            setLedStrip(0,0,0,0,0,0,0);

            try {
                ledStrip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ledStrip = null;
        }
    }

    protected void setLedStrip(int... colors) {
        if(ledStrip == null) {
            return;
        }

        try {
            ledStrip.write(colors);
            ledStrip.write(colors);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupAllButton() {
        try {
            buttonA = new Button(BUTTON_A_GPIO_PIN, Button.LogicState.PRESSED_WHEN_HIGH);
            buttonA.setOnButtonEventListener(this);

            buttonB = new Button(BUTTON_B_GPIO_PIN, Button.LogicState.PRESSED_WHEN_HIGH);
            buttonB.setOnButtonEventListener(this);

            buttonC = new Button(BUTTON_C_GPIO_PIN, Button.LogicState.PRESSED_WHEN_HIGH);
            buttonC.setOnButtonEventListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonEvent(Button button, boolean pressed) {
        if(button == buttonA) onButtonEvent(WhichButton.A, pressed);
        if(button == buttonB) onButtonEvent(WhichButton.B, pressed);
        if(button == buttonC) onButtonEvent(WhichButton.C, pressed);
    }

    /**
     * override this method to get callback from button pressing
     * @param which
     * @param pressed
     */
    protected void onButtonEvent(WhichButton which, boolean pressed) {

    }

    private void destroyAllButton() {
        destroyButton(buttonA);
        buttonA = null;
        destroyButton(buttonB);
        buttonB = null;
        destroyButton(buttonC);
        buttonC = null;
    }

    private void destroyButton(Button button) {
        if (button != null) {

            try {
                button.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupAlphanumericDisplay() {
        try {
            display = new AlphanumericDisplay(DISPLAY_I2C_BUS);
            display.setBrightness(1.0f);
            display.setEnabled(true);
            display("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyAlphanumericDisplay() {
        if (display != null) {
            try {
                display("");
                display.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                display = null;
            }
        }
    }

    protected void display(String alphaNumericText) {
        try {
            display.clear();
            display.display(alphaNumericText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void display(int number) {
        display(String.valueOf(number));
    }

    private void setupAllLedLights() {
        try {
            ledRed = pioService.openGpio(LED_LEFT);
            ledRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            ledGreen = pioService.openGpio(LED_MID);
            ledGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            ledBlue = pioService.openGpio(LED_RIGHT);
            ledBlue.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyAllLights() {
        destroyLight(ledRed);
        ledRed = null;
        destroyLight(ledGreen);
        ledGreen = null;
        destroyLight(ledBlue);
        ledBlue = null;
    }

    private void destroyLight(Gpio gpio) {
        try {
            gpio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void light(WhichLed whichLed, boolean on) {
        Gpio led = ledRed;

        switch(whichLed) {
            case RED:
                led = ledRed;
                break;
            case BLUE:
                led = ledBlue;
                break;
            case GREEN:
                led = ledGreen;
                break;
        }

        if(led != null) {
            try {
                led.setValue(on);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerSensor() {
        this.startService(new Intent(this, TemperaturePressureService.class));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorCallback = new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(Sensor sensor) {
                super.onDynamicSensorConnected(sensor);
                if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    sensorManager.registerListener(RainbowBaseActivity.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        };
        sensorManager.registerDynamicSensorCallback(sensorCallback);
    }

    private void unregisterSensor() {
        this.stopService(new Intent(this, TemperaturePressureService.class));
        sensorManager.unregisterDynamicSensorCallback(sensorCallback);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            onTemperatureChange(sensorEvent.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onTemperatureChange(float temperature) {

    }

    private void setUpMotionSensor() {
        try {
            Gpio motionGpio = new PeripheralManagerService().openGpio(MOTION_SENSOR);
            pirMotionSensor = new PirMotionSensor(motionGpio, this);
            pirMotionSensor.startup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyMotionSensor() {
        if(pirMotionSensor != null) {
            pirMotionSensor.shutdown();
        }
    }

    @Override
    public void onMovement() {

    }
}
