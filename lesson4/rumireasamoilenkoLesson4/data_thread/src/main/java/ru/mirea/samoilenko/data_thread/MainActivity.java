package ru.mirea.samoilenko.data_thread;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ru.mirea.samoilenko.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonStart.setOnClickListener(v -> {
            binding.tvInfo.setText("Start background thread...\n");

            final Runnable runn1 = new Runnable() {
                @Override
                public void run() {
                    binding.tvInfo.append("1) runOnUiThread: executes in UI thread immediately after posting\n");
                }
            };

            final Runnable runn2 = new Runnable() {
                @Override
                public void run() {
                    binding.tvInfo.append("2) post: added to the View queue without delay\n");
                }
            };

            final Runnable runn3 = new Runnable() {
                @Override
                public void run() {
                    binding.tvInfo.append("3) postDelayed: added to the View queue with 2000 ms delay\n");
                }
            };

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        runOnUiThread(runn1);

                        TimeUnit.SECONDS.sleep(1);
                        binding.tvInfo.postDelayed(runn3, 2000);
                        binding.tvInfo.post(runn2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        });
    }
}