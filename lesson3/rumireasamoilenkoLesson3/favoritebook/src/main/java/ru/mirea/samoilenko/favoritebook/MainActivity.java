package ru.mirea.samoilenko.favoritebook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final String KEY = "book_name";
    static final String USER_MESSAGE = "MESSAGE";

    private TextView textViewBook;
    private Button buttonOpenInput;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewBook = findViewById(R.id.textViewBook);
        buttonOpenInput = findViewById(R.id.buttonOpenInput);

        ActivityResultCallback<ActivityResult> callback = result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    String userBook = data.getStringExtra(USER_MESSAGE);
                    textViewBook.setText("Название Вашей любимой книги: " + userBook);
                }
            }
        };

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                callback
        );

        buttonOpenInput.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShareActivity.class);
            intent.putExtra(KEY, "Коты-Воители");
            activityResultLauncher.launch(intent);
        });
    }
}