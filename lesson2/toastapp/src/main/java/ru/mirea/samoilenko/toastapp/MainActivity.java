package ru.mirea.samoilenko.toastapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
    }

    public void onClickShowToast(View view) {
        int count = editText.getText().toString().length();
        Toast toast = Toast.makeText(
                getApplicationContext(),
                "СТУДЕНТ № 23 ГРУППА БСБО-08-23 Количество символов - " + count,
                Toast.LENGTH_SHORT
        );
        toast.show();
    }
}