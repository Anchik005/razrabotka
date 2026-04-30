package ru.mirea.samoilenko.securesharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPoet;
    private Button buttonSaveSecure;
    private ImageView imageViewPoet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPoet = findViewById(R.id.editTextPoet);
        buttonSaveSecure = findViewById(R.id.buttonSaveSecure);
        imageViewPoet = findViewById(R.id.imageViewPoet);

        imageViewPoet.setImageResource(R.drawable.mayakovsky);

        try {
            String mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            SharedPreferences secureSharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    mainKeyAlias,
                    getBaseContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String savedPoet = secureSharedPreferences.getString("POET", "Маяковский В. В.");
            editTextPoet.setText(savedPoet);

            buttonSaveSecure.setOnClickListener(v -> {
                secureSharedPreferences.edit()
                        .putString("POET", editTextPoet.getText().toString())
                        .apply();

                Toast.makeText(this, "Защищённые данные сохранены", Toast.LENGTH_SHORT).show();
            });

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}