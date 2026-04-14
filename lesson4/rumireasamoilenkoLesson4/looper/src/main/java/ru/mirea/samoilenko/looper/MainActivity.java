package ru.mirea.samoilenko.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.samoilenko.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("RESULT");
                Log.d("MainActivity", "Task executed. Result: " + result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        };

        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        binding.buttonSend.setOnClickListener(v -> {
            if (myLooper.mHandler == null) {
                Toast.makeText(MainActivity.this, "Looper is not ready yet", Toast.LENGTH_SHORT).show();
                return;
            }

            String ageStr = binding.editTextAge.getText().toString().trim();
            String job = binding.editTextJob.getText().toString().trim();

            if (ageStr.isEmpty()) {
                binding.textViewInfo.setText("Enter your age");
                return;
            }

            if (job.isEmpty()) {
                binding.textViewInfo.setText("Enter your job");
                return;
            }

            int age = Integer.parseInt(ageStr);

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putInt("AGE", age);
            bundle.putString("JOB", job);
            msg.setData(bundle);

            myLooper.mHandler.sendMessage(msg);

            binding.textViewInfo.setText("Message sent to background thread");
        });
    }
}