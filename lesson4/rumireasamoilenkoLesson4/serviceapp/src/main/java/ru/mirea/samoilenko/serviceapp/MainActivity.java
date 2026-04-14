package ru.mirea.samoilenko.serviceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.samoilenko.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textTrack.setText("My Song");
        binding.textArtist.setText("Samoilenko Player");

        binding.buttonPlay.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayerService.class);
            intent.putExtra("action", "play");
            startService(intent);
        });

        binding.buttonStop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayerService.class);
            intent.putExtra("action", "stop");
            startService(intent);
        });
    }
}