package ru.mirea.samoilenko.notebook;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextFileName;
    private EditText editTextQuote;
    private Button buttonSaveExternal;
    private Button buttonLoadExternal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextFileName = findViewById(R.id.editTextFileName);
        editTextQuote = findViewById(R.id.editTextQuote);
        buttonSaveExternal = findViewById(R.id.buttonSaveExternal);
        buttonLoadExternal = findViewById(R.id.buttonLoadExternal);

        buttonSaveExternal.setOnClickListener(v -> writeFileToExternalStorage());
        buttonLoadExternal.setOnClickListener(v -> readFileFromExternalStorage());
    }

    public void writeFileToExternalStorage() {
        String fileName = editTextFileName.getText().toString().trim();
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show();
            return;
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!path.exists()) {
            path.mkdirs();
        }

        File file = new File(path, fileName + ".txt");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
            OutputStreamWriter output = new OutputStreamWriter(fileOutputStream);
            output.write(editTextQuote.getText().toString());
            output.close();
            Toast.makeText(this, "Файл сохранён: " + file.getName(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
            Toast.makeText(this, "Ошибка записи", Toast.LENGTH_SHORT).show();
        }
    }

    public void readFileFromExternalStorage() {
        String fileName = editTextFileName.getText().toString().trim();
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show();
            return;
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, fileName + ".txt");

        try {
            FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile());
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }

            StringBuilder builder = new StringBuilder();
            for (String s : lines) {
                builder.append(s).append("\n");
            }

            editTextQuote.setText(builder.toString().trim());
            Toast.makeText(this, "Файл загружен", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.w("ExternalStorage", "Read failed", e);
            Toast.makeText(this, "Ошибка чтения", Toast.LENGTH_SHORT).show();
        }
    }
}