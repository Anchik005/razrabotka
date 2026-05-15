package ru.mirea.samoilenko.yandexmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import ru.mirea.samoilenko.yandexmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {

    private ActivityMainBinding binding;
    private UserLocationLayer userLocationLayer;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                        Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                        if (Boolean.TRUE.equals(fineLocation) || Boolean.TRUE.equals(coarseLocation)) {
                            loadUserLocationLayer();
                        } else {
                            Toast.makeText(
                                    this,
                                    "Разрешение на геолокацию не выдано",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.initialize(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mapView.getMap().move(
                new CameraPosition(
                        new Point(55.751574, 37.573856),
                        11.0f,
                        0.0f,
                        0.0f
                ),
                new Animation(Animation.Type.SMOOTH, 1.0f),
                null
        );

        if (hasLocationPermission()) {
            loadUserLocationLayer();
        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        locationPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void loadUserLocationLayer() {
        if (!hasLocationPermission()) {
            return;
        }

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();

        userLocationLayer = mapKit.createUserLocationLayer(binding.mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setObjectListener(this);
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        if (userLocationLayer == null) {
            return;
        }

        userLocationLayer.setAnchor(
                new PointF(
                        binding.mapView.getWidth() * 0.5f,
                        binding.mapView.getHeight() * 0.5f
                ),
                new PointF(
                        binding.mapView.getWidth() * 0.5f,
                        binding.mapView.getHeight() * 0.83f
                )
        );

        userLocationView.getArrow().setIcon(
                ImageProvider.fromResource(this, android.R.drawable.arrow_up_float)
        );

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, android.R.drawable.ic_menu_mylocation),
                new IconStyle()
                        .setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(1.0f)
        );

        userLocationView.getAccuracyCircle().setFillColor(0x330000FF);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
    }

    @Override
    public void onObjectUpdated(
            @NonNull UserLocationView userLocationView,
            @NonNull ObjectEvent objectEvent
    ) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        binding.mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}