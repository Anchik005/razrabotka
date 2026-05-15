package ru.mirea.samoilenko.mireaproject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraFragment extends Fragment {

    private ImageView imageView;
    private TextView textPhotoPath;

    private Uri imageUri;
    private String currentPhotoPath;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        imageView = view.findViewById(R.id.imageView);
        textPhotoPath = view.findViewById(R.id.textPhotoPath);
        Button buttonTakePicture = view.findViewById(R.id.buttonTakePicture);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageView.setImageURI(imageUri);
                        textPhotoPath.setText("Фото сохранено:\n" + currentPhotoPath);
                        Toast.makeText(requireContext(), "Фото сохранено", Toast.LENGTH_SHORT).show();
                    } else {
                        textPhotoPath.setText("Съёмка отменена");
                        Toast.makeText(requireContext(), "Съёмка отменена", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        buttonTakePicture.setOnClickListener(v -> openCamera());
        imageView.setOnClickListener(v -> openCamera());

        return view;
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File photoFile = createImageFile();

            String authorities = requireContext().getPackageName() + ".fileprovider";

            imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    authorities,
                    photoFile
            );

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            cameraLauncher.launch(cameraIntent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    requireContext(),
                    "На устройстве не найдено приложение камеры",
                    Toast.LENGTH_LONG
            ).show();
            e.printStackTrace();

        } catch (IOException e) {
            Toast.makeText(
                    requireContext(),
                    "Не удалось создать файл для фото",
                    Toast.LENGTH_LONG
            ).show();
            e.printStackTrace();

        } catch (Exception e) {
            Toast.makeText(
                    requireContext(),
                    "Ошибка запуска камеры: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date());

        String imageFileName = "MIREA_PHOTO_" + timeStamp + "_";

        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDirectory != null && !storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDirectory
        );

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }
}