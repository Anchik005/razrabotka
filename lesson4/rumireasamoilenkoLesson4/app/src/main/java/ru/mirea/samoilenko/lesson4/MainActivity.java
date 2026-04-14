package ru.mirea.samoilenko.lesson4;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.samoilenko.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvTrackTitle.setText("Numb");
        binding.tvArtist.setText("Linkin Park");

        binding.buttonPlay.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Нажата кнопка Play", Toast.LENGTH_SHORT).show()
        );

        binding.buttonStop.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Нажата кнопка Stop", Toast.LENGTH_SHORT).show()
        );
    }
}