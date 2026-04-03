package ru.mirea.samoilenko.intentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button buttonOpenSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOpenSecond = findViewById(R.id.buttonOpenSecond);

        buttonOpenSecond.setOnClickListener(v -> {
            long dateInMillis = System.currentTimeMillis();
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Moscow"));
            String dateString = sdf.format(new Date(dateInMillis));

            int myNumber = 23;
            int square = myNumber * myNumber;

            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("current_time", dateString);
            intent.putExtra("square_number", square);
            startActivity(intent);
        });
    }
}