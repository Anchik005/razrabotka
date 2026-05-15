package ru.mirea.samoilenko.httpurlconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.samoilenko.httpurlconnection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String IP_INFO_URL = "https://ipinfo.io/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.studentTextView.setText("Samoilenko, БСБО-08-23, №23");

        binding.buttonLoad.setOnClickListener(view -> loadIpAndWeather());
    }

    private void loadIpAndWeather() {
        if (!hasInternetConnection()) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.statusTextView.setText("Статус: загрузка...");
        binding.buttonLoad.setEnabled(false);

        executorService.execute(() -> {
            try {
                String ipJson = downloadUrl(IP_INFO_URL);
                JSONObject ipObject = new JSONObject(ipJson);

                String loc = ipObject.optString("loc", "");
                String[] coordinates = loc.split(",");

                if (coordinates.length < 2) {
                    throw new IllegalStateException("В ответе ipinfo.io нет координат loc");
                }

                String latitude = coordinates[0].trim();
                String longitude = coordinates[1].trim();

                String weatherUrl =
                        "https://api.open-meteo.com/v1/forecast?latitude=" +
                                latitude +
                                "&longitude=" +
                                longitude +
                                "&current_weather=true";

                String weatherJson = downloadUrl(weatherUrl);
                JSONObject weatherObject = new JSONObject(weatherJson);

                mainHandler.post(() -> showResult(ipObject, weatherObject));

            } catch (Exception exception) {
                mainHandler.post(() -> {
                    binding.statusTextView.setText("Статус: ошибка — " + exception.getMessage());
                    binding.buttonLoad.setEnabled(true);
                });
            }
        });
    }

    private boolean hasInternetConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        Network network = connectivityManager.getActiveNetwork();

        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

        return capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private String downloadUrl(String address) throws Exception {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "MIREA Android Samoilenko");

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException(
                        connection.getResponseMessage() + ". Error code: " + responseCode
                );
            }

            inputStream = connection.getInputStream();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }

            return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void showResult(JSONObject ipObject, JSONObject weatherObject) {
        binding.statusTextView.setText("Статус: данные получены");

        binding.ipTextView.setText("IP: " + ipObject.optString("ip", "—"));
        binding.cityTextView.setText("Город: " + ipObject.optString("city", "—"));
        binding.regionTextView.setText("Регион: " + ipObject.optString("region", "—"));
        binding.countryTextView.setText("Страна: " + ipObject.optString("country", "—"));
        binding.locationTextView.setText("Координаты: " + ipObject.optString("loc", "—"));
        binding.orgTextView.setText("Провайдер: " + ipObject.optString("org", "—"));
        binding.timezoneTextView.setText("Часовой пояс: " + ipObject.optString("timezone", "—"));

        JSONObject currentWeather = weatherObject.optJSONObject("current_weather");

        if (currentWeather == null) {
            binding.weatherTextView.setText("Погода: нет блока current_weather");
        } else {
            String weatherText =
                    "Температура: " + currentWeather.optDouble("temperature") + " °C\n" +
                            "Скорость ветра: " + currentWeather.optDouble("windspeed") + " км/ч\n" +
                            "Направление ветра: " + currentWeather.optDouble("winddirection") + "°\n" +
                            "Код погоды: " + currentWeather.optInt("weathercode") + "\n" +
                            "Время измерения: " + currentWeather.optString("time");

            binding.weatherTextView.setText(weatherText);
        }

        binding.buttonLoad.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }
}