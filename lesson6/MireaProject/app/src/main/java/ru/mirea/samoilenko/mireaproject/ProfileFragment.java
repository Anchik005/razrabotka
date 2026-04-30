package ru.mirea.samoilenko.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText editTextFullName, editTextGroup, editTextNumber;
    private Button buttonSaveProfile;
    private static final String PREF_NAME = "profile_data";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_GROUP = "group";
    private static final String KEY_NUMBER = "number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextGroup = view.findViewById(R.id.editTextGroup);
        editTextNumber = view.findViewById(R.id.editTextNumber);
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);

        SharedPreferences preferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editTextFullName.setText(preferences.getString(KEY_FULL_NAME, ""));
        editTextGroup.setText(preferences.getString(KEY_GROUP, ""));
        editTextNumber.setText(String.valueOf(preferences.getInt(KEY_NUMBER, 0)));

        buttonSaveProfile.setOnClickListener(v -> {
            String fullName = editTextFullName.getText().toString().trim();
            String group = editTextGroup.getText().toString().trim();
            String numberText = editTextNumber.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(group) || TextUtils.isEmpty(numberText)) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int number = Integer.parseInt(numberText);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_FULL_NAME, fullName);
            editor.putString(KEY_GROUP, group);
            editor.putInt(KEY_NUMBER, number);
            editor.apply();

            Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}