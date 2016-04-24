package hackathon.spaceapps.spacesleeve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;
import android.widget.Chronometer;
import android.widget.TextView;

public class MissionActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorManager sSensorManager;
    private Sensor sStepCounterSensor;
    private Sensor sStepDetectorSensor;
    private Toast mToast;
    private int stepCount = 0;
    private static double DISTANCE_CONVERSION = .75; //.75m = 1 step
    private Chronometer timer;
    private TextView distanceView;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            finish();
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0)
        {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER)
        {
            value++;
            stepCount = value;
            distanceView.setText(new Double(stepCount * DISTANCE_CONVERSION).toString() + " m");
            showToast("Steps: " + value);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    protected void onResume()
    {
        super.onResume();
        sSensorManager.registerListener(this, sStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sSensorManager.registerListener(this, sStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        registerReceiver(messageReceiver, new IntentFilter("1"));
        System.out.println("Mission resumed.");
    }

    protected void onPause()
    {
        super.onPause();
        sSensorManager.unregisterListener(this, sStepCounterSensor);
        sSensorManager.unregisterListener(this, sStepDetectorSensor);
            unregisterReceiver(messageReceiver);
        System.out.println("Mission paused.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        sSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sStepCounterSensor = sSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sStepDetectorSensor = sSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        sSensorManager.registerListener(this, sStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //timer.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void showToast(String text)
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else
        {
            mToast.setText(text);
        }
        mToast.show();
    }
}