package com.example.kazuki.orientationget;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mMagField;
    private Sensor mAccelerometer;

    private static final int MATRIX_SIZE = 16;

    private float[] mgValues = new float[3];
    private float[] acValues = new float[3];

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagField);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        TextView txt01 = findViewById(R.id.txt01);
        float[] inR = new float[MATRIX_SIZE];
        float[] outR = new float[MATRIX_SIZE];
        float[] I = new float[MATRIX_SIZE];
        float[] orValues = new float[3];

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                acValues = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mgValues = sensorEvent.values.clone();
                break;
        }

        if (mgValues != null && acValues != null) {
            SensorManager.getRotationMatrix(inR, I, acValues, mgValues);

            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X,
                    SensorManager.AXIS_Y, outR);
            SensorManager.getOrientation(outR, orValues);

            StringBuilder strBuild = new StringBuilder();
            strBuild.append("方向角（アジマス）:");
            strBuild.append(rad2Deg(orValues[0]));
            strBuild.append("\n");
            strBuild.append("傾斜角（ピッチ）:");
            strBuild.append(rad2Deg(orValues[1]));
            strBuild.append("\n");
            strBuild.append("回転角（ロール）:");
            strBuild.append(rad2Deg(orValues[2]));
            strBuild.append("\n");
            txt01.setText(strBuild.toString());
        }
    }

    private int rad2Deg(float rad) {
        return (int) Math.floor(Math.toDegrees(rad));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
