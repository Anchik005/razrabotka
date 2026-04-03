package ru.mirea.samoilenko.favoritebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ShareActivity extends AppCompatActivity {

    private TextView textViewDeveloperBook;
    private EditText editTextUserBook;
    private Button buttonSendBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        textViewDeveloperBook = findViewById(R.id.textViewDeveloperBook);
        editTextUserBook = findViewById(R.id.editTextUserBook);
        buttonSendBack = findViewById(R.id.buttonSendBack);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String developerBook = extras.getString(MainActivity.KEY);
            textViewDeveloperBook.setText("Любимая книга разработчика - " + developerBook);
        }

        buttonSendBack.setOnClickListener(v -> {
            String text = editTextUserBook.getText().toString();

            Intent data = new Intent();
            data.putExtra(MainActivity.USER_MESSAGE, text);
            setResult(Activity.RESULT_OK, data);
            finish();
        });
    }
}