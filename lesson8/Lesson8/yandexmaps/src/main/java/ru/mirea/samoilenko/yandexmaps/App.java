package ru.mirea.samoilenko.yandexmaps;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {
    private static final String MAPKIT_API_KEY = "565ed06a-017e-42b7-b048-9cae90f91c14";

    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }
}