package ru.mirea.samoilenko.mireaproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int REQUEST_CODE_MICROPHONE = 102;

    private DrawerLayout drawerLayout;

    private Button buttonMenu;
    private Button btnProfile;
    private Button btnFileWork;
    private Button btnData;
    private Button btnNetwork;
    private Button btnWeb;
    private Button btnBackground;
    private Button btnSensor;
    private Button btnCamera;
    private Button btnMicrophone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        buttonMenu = findViewById(R.id.buttonMenu);
        btnProfile = findViewById(R.id.btnProfile);
        btnFileWork = findViewById(R.id.btnFileWork);
        btnData = findViewById(R.id.btnData);
        btnNetwork = findViewById(R.id.btnNetwork);
        btnWeb = findViewById(R.id.btnWeb);
        btnBackground = findViewById(R.id.btnBackground);
        btnSensor = findViewById(R.id.btnSensor);
        btnCamera = findViewById(R.id.btnCamera);
        btnMicrophone = findViewById(R.id.btnMicrophone);

        requestMainPermissions();

        buttonMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        btnProfile.setOnClickListener(v -> {
            openFragment(new ProfileFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnFileWork.setOnClickListener(v -> {
            openFragment(new FileWorkFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnData.setOnClickListener(v -> {
            openFragment(new DataFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnNetwork.setOnClickListener(v -> {
            openFragment(new NetworkInfoFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnWeb.setOnClickListener(v -> {
            openFragment(new WebViewFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnBackground.setOnClickListener(v -> {
            openFragment(new BackgroundTaskFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnSensor.setOnClickListener(v -> {
            openFragment(new SensorFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnCamera.setOnClickListener(v -> {
            openFragment(new CameraFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnMicrophone.setOnClickListener(v -> {
            openFragment(new MicrophoneFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        if (savedInstanceState == null) {
            openFragment(new DataFragment());
        }
    }

    private void requestMainPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA
            );
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_MICROPHONE
            );
        }
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}