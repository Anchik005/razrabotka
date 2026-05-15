package ru.mirea.samoilenko.osmmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import ru.mirea.samoilenko.osmmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLocationNewOverlay locationNewOverlay;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                        Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                        if (Boolean.TRUE.equals(fineLocation) || Boolean.TRUE.equals(coarseLocation)) {
                            addLocationOverlay();
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

        Configuration.getInstance().setUserAgentValue("ru.mirea.samoilenko.osmmaps");
        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupMap();
        addCompass();
        addScaleBar();
        addMarkers();

        if (hasLocationPermission()) {
            addLocationOverlay();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Кнопки масштабирования и увеличение двумя пальцами.
        binding.mapView.setBuiltInZoomControls(true);
        binding.mapView.setZoomRounding(true);
        binding.mapView.setMultiTouchControls(true);

        IMapController mapController = binding.mapView.getController();
        mapController.setZoom(13.0);

        // Центр карты.
        GeoPoint startPoint = new GeoPoint(55.670005, 37.479894);
        mapController.setCenter(startPoint);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void addLocationOverlay() {
        if (locationNewOverlay != null) {
            return;
        }

        locationNewOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()),
                binding.mapView
        );

        locationNewOverlay.enableMyLocation();
        locationNewOverlay.enableFollowLocation();

        binding.mapView.getOverlays().add(locationNewOverlay);
        binding.mapView.invalidate();
    }

    private void addCompass() {
        CompassOverlay compassOverlay = new CompassOverlay(
                getApplicationContext(),
                new InternalCompassOrientationProvider(getApplicationContext()),
                binding.mapView
        );

        compassOverlay.enableCompass();
        binding.mapView.getOverlays().add(compassOverlay);
    }

    private void addScaleBar() {
        Context context = getApplicationContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(binding.mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, 10);

        binding.mapView.getOverlays().add(scaleBarOverlay);
    }

    private void addMarkers() {
        addMarker(
                55.670005,
                37.479894,
                "РТУ МИРЭА",
                "Проспект Вернадского, 78. Главная точка для учебной практики."
        );

        addMarker(
                55.677828,
                37.506491,
                "Парк 50-летия Октября",
                "Большой парк недалеко от района университета."
        );

        addMarker(
                55.710063,
                37.543352,
                "Воробьёвы горы",
                "Смотровая площадка и одно из известных мест Москвы."
        );

        addMarker(
                55.751574,
                37.573856,
                "Центр Москвы",
                "Точка в центральной части города для демонстрации маркера."
        );

        binding.mapView.invalidate();
    }

    private void addMarker(double latitude, double longitude, String title, String description) {
        Marker marker = new Marker(binding.mapView);

        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setTitle(title);
        marker.setSnippet(description);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
            clickedMarker.showInfoWindow();

            Toast.makeText(
                    getApplicationContext(),
                    title + "\n" + description,
                    Toast.LENGTH_LONG
            ).show();

            return true;
        });

        binding.mapView.getOverlays().add(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        binding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Configuration.getInstance().save(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        binding.mapView.onPause();
    }
}