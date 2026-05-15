package ru.mirea.samoilenko.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class PlacesFragment extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    private final ArrayList<GeoPoint> placePoints = new ArrayList<>();

    public PlacesFragment() {
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );

        mapView = view.findViewById(R.id.mapPlaces);

        setupMap();
        addCompass();
        addPlaces();

        view.findViewById(R.id.buttonShowAllPlaces).setOnClickListener(v -> showAllPlaces());
        view.findViewById(R.id.buttonMyLocation).setOnClickListener(v -> enableMyLocation());

        return view;
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setZoomRounding(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(12.5);

        GeoPoint mireaPoint = new GeoPoint(55.670005, 37.479894);
        mapController.setCenter(mireaPoint);
    }

    private void addCompass() {
        CompassOverlay compassOverlay = new CompassOverlay(
                requireContext(),
                new InternalCompassOrientationProvider(requireContext()),
                mapView
        );

        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }

    private void addPlaces() {
        addPlaceMarker(
                55.670005,
                37.479894,
                "Студенческое кафе МИРЭА",
                "Москва, проспект Вернадского, 78",
                "Кафе рядом с учебными корпусами. Подходит для быстрого перекуса между парами."
        );

        addPlaceMarker(
                55.672550,
                37.481900,
                "Кофейня у университета",
                "Москва, проспект Вернадского, район РТУ МИРЭА",
                "Небольшая кофейня рядом с университетом. Можно взять кофе, выпечку и перекус с собой."
        );

        addPlaceMarker(
                55.676200,
                37.484600,
                "Пиццерия на Вернадского",
                "Москва, проспект Вернадского",
                "Заведение с пиццей и горячими напитками. Удобное место для встречи после занятий."
        );

        addPlaceMarker(
                55.681400,
                37.489200,
                "Бургерная рядом с метро",
                "Москва, район станции метро Юго-Западная",
                "Фастфуд-заведение рядом с транспортной развязкой. Подходит для быстрого обеда."
        );

        addPlaceMarker(
                55.686300,
                37.491900,
                "Кафе домашней кухни",
                "Москва, район Проспект Вернадского",
                "Кафе с горячими блюдами, салатами и напитками. Подходит для полноценного обеда."
        );

        mapView.invalidate();
    }

    private void addPlaceMarker(
            double latitude,
            double longitude,
            String title,
            String address,
            String description
    ) {
        GeoPoint point = new GeoPoint(latitude, longitude);
        placePoints.add(point);

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSnippet(address + "\n" + description);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setOnMarkerClickListener((clickedMarker, clickedMapView) -> {
            clickedMarker.showInfoWindow();

            Toast.makeText(
                    requireContext(),
                    title + "\n" + address + "\n" + description,
                    Toast.LENGTH_LONG
            ).show();

            return true;
        });

        mapView.getOverlays().add(marker);
    }

    private void showAllPlaces() {
        if (placePoints.isEmpty()) {
            return;
        }

        double north = placePoints.get(0).getLatitude();
        double south = placePoints.get(0).getLatitude();
        double east = placePoints.get(0).getLongitude();
        double west = placePoints.get(0).getLongitude();

        for (GeoPoint point : placePoints) {
            north = Math.max(north, point.getLatitude());
            south = Math.min(south, point.getLatitude());
            east = Math.max(east, point.getLongitude());
            west = Math.min(west, point.getLongitude());
        }

        BoundingBox boundingBox = new BoundingBox(
                north + 0.01,
                east + 0.01,
                south - 0.01,
                west - 0.01
        );

        mapView.zoomToBoundingBox(boundingBox, true);

        Toast.makeText(
                requireContext(),
                "Показаны все заведения на карте",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    requireContext(),
                    "Разрешение на геолокацию не выдано",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (locationOverlay == null) {
            locationOverlay = new MyLocationNewOverlay(
                    new GpsMyLocationProvider(requireContext()),
                    mapView
            );

            locationOverlay.enableMyLocation();
            locationOverlay.enableFollowLocation();

            mapView.getOverlays().add(locationOverlay);
            mapView.invalidate();
        }

        Toast.makeText(
                requireContext(),
                "Отображение текущего местоположения включено",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mapView != null) {
            Configuration.getInstance().load(
                    requireContext(),
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
            );

            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mapView != null) {
            Configuration.getInstance().save(
                    requireContext(),
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
            );

            mapView.onPause();
        }
    }
}