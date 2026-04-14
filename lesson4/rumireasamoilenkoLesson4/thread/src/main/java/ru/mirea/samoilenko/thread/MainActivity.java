package ru.mirea.samoilenko.thread;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;

import ru.mirea.samoilenko.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();

        binding.tvThreadInfo.setText("Имя текущего потока: " + mainThread.getName());

        mainThread.setName("Samoilenko, номер по списку: 23");
        binding.tvThreadInfo.append("\nНовое имя потока: " + mainThread.getName());

        Log.d(MainActivity.class.getSimpleName(),
                "Stack: " + Arrays.toString(mainThread.getStackTrace()));

        Log.d(MainActivity.class.getSimpleName(),
                "Group: " + mainThread.getThreadGroup());

        binding.buttonCalc.setOnClickListener(v -> {
            String lessonsStr = binding.etLessons.getText().toString().trim();
            String daysStr = binding.etDays.getText().toString().trim();

            if (lessonsStr.isEmpty()) {
                binding.tvResult.setText("Ошибка: введите общее количество пар");
                return;
            }

            if (daysStr.isEmpty()) {
                binding.tvResult.setText("Ошибка: введите количество учебных дней");
                return;
            }

            int lessons = Integer.parseInt(lessonsStr);
            int days = Integer.parseInt(daysStr);

            if (days == 0) {
                binding.tvResult.setText("Ошибка: количество учебных дней не может быть 0");
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int numberThread = counter++;

                    Log.d("ThreadProject",
                            String.format(Locale.US,
                                    "Запущен поток № %d студентом Samoilenko, номер по списку № %d",
                                    numberThread, 23));

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    final double average = (double) lessons / days;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvResult.setText(
                                    String.format(Locale.US,
                                            "Среднее количество пар в день: %.2f",
                                            average)
                            );
                        }
                    });

                    Log.d("ThreadProject", "Выполнен поток № " + numberThread);
                }
            }).start();
        });
    }
}