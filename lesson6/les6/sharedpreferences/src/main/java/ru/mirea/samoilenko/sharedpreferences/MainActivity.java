package ru.mirea.samoilenko.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextGroup;
    private EditText editTextNumber;
    private EditText editTextMovie;
    private Button buttonSave;

    private static final String PREF_NAME = "mirea_settings";
    private static final String KEY_GROUP = "GROUP";
    private static final String KEY_NUMBER = "NUMBER";
    private static final String KEY_MOVIE = "MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextGroup = findViewById(R.id.editTextGroup);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextMovie = findViewById(R.id.editTextMovie);
        buttonSave = findViewById(R.id.buttonSave);

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        editTextGroup.setText(preferences.getString(KEY_GROUP, "БСБО-08-23"));
        editTextNumber.setText(String.valueOf(preferences.getInt(KEY_NUMBER, 23)));
        editTextMovie.setText(preferences.getString(KEY_MOVIE, ""));

        buttonSave.setOnClickListener(v -> {
            String group = editTextGroup.getText().toString();
            String numberText = editTextNumber.getText().toString();
            String movie = editTextMovie.getText().toString();

            int number = 15;
            if (!numberText.isEmpty()) {
                number = Integer.parseInt(numberText);
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_GROUP, group);
            editor.putInt(KEY_NUMBER, number);
            editor.putString(KEY_MOVIE, movie);
            editor.apply();

            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
        });
    }
}