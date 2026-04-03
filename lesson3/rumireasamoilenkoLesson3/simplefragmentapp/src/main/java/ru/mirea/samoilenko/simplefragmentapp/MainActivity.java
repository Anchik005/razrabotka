package ru.mirea.samoilenko.simplefragmentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    // Фрагменты
    private Fragment fragment1;
    private Fragment fragment2;

    // Менеджер фрагментов
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Создаём фрагменты
        fragment1 = new FirstFragment();
        fragment2 = new SecondFragment();
    }

    // Метод для кнопок
    public void onClick(View view) {
        fragmentManager = getSupportFragmentManager();

        int id = view.getId();

        // Какая кнопка нажата
        if (id == R.id.btnFragment1) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment1)
                    .commit();

        } else if (id == R.id.btnFragment2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment2)
                    .commit();
        }
    }
}