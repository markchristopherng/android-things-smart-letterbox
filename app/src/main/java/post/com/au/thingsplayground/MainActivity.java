package post.com.au.thingsplayground;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import post.com.au.thingsplayground.helper.NetworkHelper;

public class MainActivity extends RainbowBaseActivity {

    private static int MAX_COUNTER_NUMBER = 15;

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name) + " at " + NetworkHelper.getLocalIpAddress());
        display(counter);
        syncCounter();
    }

    @Override
    protected void onButtonEvent(WhichButton which, boolean pressed) {

        switch (which) {
            case A:
                // press button A to increase the counter
                if(pressed) {
                    light(WhichLed.RED, false);
                } else {
                    light(WhichLed.RED, true);
                    increaseCounter();
                }
                break;
            case B:
                // press button B to reset the counter
                if(pressed) {
                    light(WhichLed.GREEN, false);
                } else {
                    light(WhichLed.GREEN, true);
                    counter = 0;
                    display(counter);
                    syncCounter();
                }
                break;
            case C:
                // press button C to decrease the counter
                if(pressed) {
                    light(WhichLed.BLUE, false);
                } else {
                    light(WhichLed.BLUE, true);
                    decreaseCounter();
                }
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onTemperatureChange(float temperature) {
        // sync new temperature to firebase
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("postboxes").child("0").child("degree").setValue(String.format("%.1fC",
                temperature));
    }

    /**
     * Increases counter
     */
    private void increaseCounter() {
        display(++counter);
        syncCounter();
    }

    /**
     * decreases counter
     */
    private void decreaseCounter() {
        display(--counter);
        syncCounter();
    }

    /**
     * Sends latest counter to firebase and update led strip
     */
    private void syncCounter() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("postboxes").child("0").child("lettercount").setValue(counter);

        int color ;
        if (counter >= MAX_COUNTER_NUMBER * 2 / 3) {
            color = Color.RED;
        } else if (counter >= MAX_COUNTER_NUMBER / 3) {
            color = Color.YELLOW;
        } else {
            color = Color.GREEN;
        }

        setLedStrip(color, color, color, color, color, color, color);
    }

    @Override
    public void onMovement() {
        increaseCounter();
    }

    @Override
    protected void display(int number) {
        super.display(number);

        // if current counter is larger than max value, show text "FULL" instead of actual counter value
        if (counter > MAX_COUNTER_NUMBER) {
            display("FULL");
        }
    }
}
