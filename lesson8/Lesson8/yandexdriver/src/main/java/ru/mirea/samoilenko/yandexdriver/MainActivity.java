package ru.mirea.samoilenko.yandexdriver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.samoilenko.yandexdriver.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {

    private ActivityMainBinding binding;

    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private MapObjectCollection mapObjects;

    private LocationManager locationManager;
    private LocationListener locationListener;

    // Можешь заменить на своё любимое заведение.
    private static final Point PLACE_POINT = new Point(55.670005, 37.479894);
    private static final String PLACE_TITLE = "Любимое заведение";
    private static final String PLACE_DESCRIPTION =
            "РТУ МИРЭА, Москва, проспект Вернадского, 78. Точка выбрана для практической работы №8.";

    private final int[] routeColors = {
            0xFFFF0000,
            0xFF00AA00,
            0xFF0000FF,
            0xFFFF8800
    };

    private final MapObjectTapListener placeTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            Toast.makeText(
                    MainActivity.this,
                    PLACE_TITLE + "\n" + PLACE_DESCRIPTION,
                    Toast.LENGTH_LONG
            ).show();

            return true;
        }
    };

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                        Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                        if (Boolean.TRUE.equals(fineLocation) || Boolean.TRUE.equals(coarseLocation)) {
                            requestUserLocationAndBuildRoute();
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

        binding.mapView.getMap().setRotateGesturesEnabled(false);

        mapObjects = binding.mapView.getMap().getMapObjects().addCollection();

        drivingRouter = DirectionsFactory
                .getInstance()
                .createDrivingRouter(DrivingRouterType.ONLINE);

        addPlaceMarker();
        moveCameraToPoint(PLACE_POINT, 12.0f);

        if (hasLocationPermission()) {
            requestUserLocationAndBuildRoute();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestUserLocationAndBuildRoute() {
        if (!hasLocationPermission()) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String provider = locationManager.getBestProvider(criteria, true);

        if (provider == null) {
            Toast.makeText(this, "Включи геолокацию на устройстве", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Location lastLocation = locationManager.getLastKnownLocation(provider);

            if (lastLocation != null) {
                Point userPoint = new Point(
                        lastLocation.getLatitude(),
                        lastLocation.getLongitude()
                );

                buildRoute(userPoint, PLACE_POINT);
                return;
            }

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Point userPoint = new Point(
                            location.getLatitude(),
                            location.getLongitude()
                    );

                    buildRoute(userPoint, PLACE_POINT);

                    if (locationManager != null && locationListener != null) {
                        locationManager.removeUpdates(locationListener);
                    }
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            };

            locationManager.requestLocationUpdates(
                    provider,
                    1000L,
                    1.0f,
                    locationListener
            );

        } catch (SecurityException exception) {
            Toast.makeText(this, "Нет разрешения на геолокацию", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildRoute(Point startPoint, Point endPoint) {
        mapObjects.clear();

        addStartMarker(startPoint);
        addPlaceMarker();

        Point center = new Point(
                (startPoint.getLatitude() + endPoint.getLatitude()) / 2.0,
                (startPoint.getLongitude() + endPoint.getLongitude()) / 2.0
        );

        moveCameraToPoint(center, 11.0f);

        DrivingOptions drivingOptions = new DrivingOptions();
        drivingOptions.setRoutesCount(4);

        VehicleOptions vehicleOptions = new VehicleOptions();

        ArrayList<RequestPoint> requestPoints = new ArrayList<>();

        requestPoints.add(new RequestPoint(
                startPoint,
                RequestPointType.WAYPOINT,
                null,
                null,
                null
        ));

        requestPoints.add(new RequestPoint(
                endPoint,
                RequestPointType.WAYPOINT,
                null,
                null,
                null
        ));

        drivingSession = drivingRouter.requestRoutes(
                requestPoints,
                drivingOptions,
                vehicleOptions,
                this
        );
    }

    private void addStartMarker(Point startPoint) {
        PlacemarkMapObject startMarker = mapObjects.addPlacemark();
        startMarker.setGeometry(startPoint);
        startMarker.setIcon(ImageProvider.fromResource(this, android.R.drawable.ic_menu_mylocation));
    }

    private void addPlaceMarker() {
        PlacemarkMapObject placeMarker = mapObjects.addPlacemark();
        placeMarker.setGeometry(PLACE_POINT);
        placeMarker.setIcon(ImageProvider.fromResource(this, android.R.drawable.star_big_on));
        placeMarker.addTapListener(placeTapListener);
    }

    private void moveCameraToPoint(Point point, float zoom) {
        binding.mapView.getMap().move(
                new CameraPosition(
                        point,
                        zoom,
                        0.0f,
                        0.0f
                )
        );
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
        int routesCount = Math.min(routes.size(), routeColors.length);

        for (int i = 0; i < routesCount; i++) {
            mapObjects
                    .addPolyline(routes.get(i).getGeometry())
                    .setStrokeColor(routeColors[i]);
        }

        Toast.makeText(this, "Маршрут построен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = "Неизвестная ошибка построения маршрута";

        if (error instanceof RemoteError) {
            errorMessage = "Ошибка сервера маршрутизации";
        } else if (error instanceof NetworkError) {
            errorMessage = "Ошибка сети";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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