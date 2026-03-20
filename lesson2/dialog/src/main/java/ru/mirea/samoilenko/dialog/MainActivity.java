package ru.mirea.samoilenko.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickShowDialog(View view) {
        MyDialogFragment dialogFragment = new MyDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "mirea");
    }

    public void onClickShowTimeDialog(View view) {
        MyTimeDialogFragment timeDialogFragment = new MyTimeDialogFragment();
        timeDialogFragment.show(getSupportFragmentManager(), "timeDialog");
    }

    public void onClickShowDateDialog(View view) {
        MyDateDialogFragment dateDialogFragment = new MyDateDialogFragment();
        dateDialogFragment.show(getSupportFragmentManager(), "dateDialog");
    }

    public void onClickShowProgressDialog(View view) {
        MyProgressDialogFragment progressDialogFragment = new MyProgressDialogFragment();
        progressDialogFragment.show(getSupportFragmentManager(), "progressDialog");
    }

    public void onOkClicked() {
        Toast.makeText(getApplicationContext(),
                "Вы выбрали кнопку \"Иду дальше\"!",
                Toast.LENGTH_LONG).show();
    }

    public void onCancelClicked() {
        Toast.makeText(getApplicationContext(),
                "Вы выбрали кнопку \"Нет\"!",
                Toast.LENGTH_LONG).show();
    }

    public void onNeutralClicked() {
        Toast.makeText(getApplicationContext(),
                "Вы выбрали кнопку \"На паузе\"!",
                Toast.LENGTH_LONG).show();
    }
}