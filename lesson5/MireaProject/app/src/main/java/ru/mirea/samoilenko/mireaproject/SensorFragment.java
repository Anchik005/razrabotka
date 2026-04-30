package ru.mirea.samoilenko.mireaproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SensorFragment extends Fragment implements SensorEventListener {

    private TextView textViewAzimuth;
    private TextView textViewPitch;
    private TextView textViewRoll;
    private TextView textViewTask;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        textViewAzimuth = view.findViewById(R.id.textViewAzimuth);
        textViewPitch = view.findViewById(R.id.textViewPitch);
        textViewRoll = view.findViewById(R.id.textViewRoll);
        textViewTask = view.findViewById(R.id.textViewTask);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelerometerSensor == null) {
            Toast.makeText(requireContext(), "Акселерометр не найден", Toast.LENGTH_LONG).show();
            textViewTask.setText("На устройстве отсутствует акселерометр");
        } else {
            textViewTask.setText("Задача: определить положение телефона по данным акселерометра");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorManager != null && accelerometerSensor != null) {
            sensorManager.registerListener(
                    this,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float valueAzimuth = event.values[0];
            float valuePitch = event.values[1];
            float valueRoll = event.values[2];

            textViewAzimuth.setText("Azimuth / X: " + valueAzimuth);
            textViewPitch.setText("Pitch / Y: " + valuePitch);
            textViewRoll.setText("Roll / Z: " + valueRoll);

            String position = getPhonePosition(valueAzimuth, valuePitch, valueRoll);
            textViewTask.setText("Положение телефона: " + position);
        }
    }

    private String getPhonePosition(float x, float y, float z) {
        if (z > 8) {
            return "лежит экраном вверх";
        } else if (z < -8) {
            return "лежит экраном вниз";
        } else if (x > 6) {
            return "наклонён вправо";
        } else if (x < -6) {
            return "наклонён влево";
        } else if (y > 6) {
            return "наклонён нижней частью вниз";
        } else if (y < -6) {
            return "наклонён верхней частью вниз";
        } else {
            return "положение не определено";
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Метод обязателен для интерфейса SensorEventListener.
    }
}