package ru.mirea.samoilenko.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "history_date.txt";

    private EditText editTextHistory;
    private Button buttonSaveFile;
    private TextView textViewLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextHistory = findViewById(R.id.editTextHistory);
        buttonSaveFile = findViewById(R.id.buttonSaveFile);
        textViewLoaded = findViewById(R.id.textViewLoaded);

        editTextHistory.setText("22 июня — День памяти и скорби, день начала Великой Отечественной войны (1941 год)");

        buttonSaveFile.setOnClickListener(v -> {
            try {
                FileOutputStream outputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                outputStream.write(editTextHistory.getText().toString().getBytes());
                outputStream.close();

                Toast.makeText(this, "Файл сохранён", Toast.LENGTH_SHORT).show();
                textViewLoaded.setText(getTextFromFile());

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getTextFromFile() {
        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            return new String(bytes);
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fin != null) fin.close();
            } catch (IOException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }
}