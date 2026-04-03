package ru.mirea.samoilenko.mireaproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Button buttonMenu;
    private Button btnData;
    private Button btnWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        buttonMenu = findViewById(R.id.buttonMenu);
        btnData = findViewById(R.id.btnData);
        btnWeb = findViewById(R.id.btnWeb);

        // Кнопка открытия бокового меню
        buttonMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        // При первом запуске открываем DataFragment
        if (savedInstanceState == null) {
            openFragment(new DataFragment());
        }

        // Пункт меню DataFragment
        btnData.setOnClickListener(v -> {
            openFragment(new DataFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Пункт меню WebViewFragment
        btnWeb.setOnClickListener(v -> {
            openFragment(new WebViewFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}