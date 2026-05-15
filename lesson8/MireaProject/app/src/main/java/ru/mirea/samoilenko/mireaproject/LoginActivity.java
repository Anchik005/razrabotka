package ru.mirea.samoilenko.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ru.mirea.samoilenko.mireaproject.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.studentTextView.setText("Samoilenko, БСБО-08-23, №23");

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            openMainActivity();
            return;
        }

        binding.signInButton.setOnClickListener(view -> signIn());
        binding.createAccountButton.setOnClickListener(view -> createAccount());
    }

    private boolean validateForm() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Введите email");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Некорректный email");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Введите пароль");
            return false;
        }

        if (password.length() < 6) {
            binding.passwordEditText.setError("Минимум 6 символов");
            return false;
        }

        return true;
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        binding.signInButton.setEnabled(false);
        binding.createAccountButton.setEnabled(false);
        binding.statusTextView.setText("Вход...");

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.signInButton.setEnabled(true);
                    binding.createAccountButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        binding.statusTextView.setText("Вход выполнен");
                        openMainActivity();
                    } else {
                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : "Ошибка входа";

                        binding.statusTextView.setText(message);

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void createAccount() {
        if (!validateForm()) {
            return;
        }

        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        binding.signInButton.setEnabled(false);
        binding.createAccountButton.setEnabled(false);
        binding.statusTextView.setText("Регистрация...");

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.signInButton.setEnabled(true);
                    binding.createAccountButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        binding.statusTextView.setText("Аккаунт создан");
                        openMainActivity();
                    } else {
                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : "Ошибка регистрации";

                        binding.statusTextView.setText(message);

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}