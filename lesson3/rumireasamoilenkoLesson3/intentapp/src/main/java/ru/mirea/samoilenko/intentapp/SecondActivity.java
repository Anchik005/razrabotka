package ru.mirea.samoilenko.intentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textViewResult = findViewById(R.id.textViewResult);

        Intent intent = getIntent();
        String time = intent.getStringExtra("current_time");
        int square = intent.getIntExtra("square_number", 0);

        String result = "Квадрат значения моего номера по списку в группе " +
                "составляет " + square + ", а текущее время " + time;

        textViewResult.setText(result);
    }
}