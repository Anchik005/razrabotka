package ru.mirea.samoilenko.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

public class MicrophoneFragment extends Fragment {

    private MediaRecorder recorder;
    private MediaPlayer player;

    private String recordFilePath;

    private Button buttonRecord;
    private Button buttonPlay;
    private TextView textViewStatus;

    private boolean isRecording = false;
    private boolean isPlaying = false;

    private ActivityResultLauncher<String> audioPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_microphone, container, false);

        buttonRecord = view.findViewById(R.id.buttonRecord);
        buttonPlay = view.findViewById(R.id.buttonPlay);
        textViewStatus = view.findViewById(R.id.textViewStatus);

        File musicDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (musicDirectory != null && !musicDirectory.exists()) {
            musicDirectory.mkdirs();
        }

        File audioFile = new File(musicDirectory, "mirea_audio_record.3gp");
        recordFilePath = audioFile.getAbsolutePath();

        buttonPlay.setEnabled(audioFile.exists());

        audioPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startRecording();
                    } else {
                        Toast.makeText(requireContext(), "Разрешение микрофона отклонено", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        buttonRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                checkPermissionAndStartRecording();
            }
        });

        buttonPlay.setOnClickListener(v -> {
            if (isPlaying) {
                stopPlaying();
            } else {
                startPlaying();
            }
        });

        textViewStatus.setText("Готово к записи\nФайл: " + recordFilePath);

        return view;
    }

    private void checkPermissionAndStartRecording() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void startRecording() {
        try {
            stopPlaying();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(recordFilePath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            recorder.prepare();
            recorder.start();

            isRecording = true;

            buttonRecord.setText("Остановить запись");
            buttonPlay.setEnabled(false);
            textViewStatus.setText("Идёт запись...\nГоворите в микрофон.");

        } catch (IOException | RuntimeException e) {
            isRecording = false;
            releaseRecorder();
            Toast.makeText(requireContext(), "Ошибка начала записи", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
            }

            textViewStatus.setText("Запись сохранена:\n" + recordFilePath);
            Toast.makeText(requireContext(), "Запись остановлена", Toast.LENGTH_SHORT).show();

        } catch (RuntimeException e) {
            Toast.makeText(requireContext(), "Запись была слишком короткой или повреждена", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        } finally {
            releaseRecorder();

            isRecording = false;

            buttonRecord.setText("Начать запись");
            buttonPlay.setEnabled(true);
        }
    }

    private void startPlaying() {
        try {
            player = new MediaPlayer();
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();

            isPlaying = true;

            buttonPlay.setText("Остановить воспроизведение");
            buttonRecord.setEnabled(false);
            textViewStatus.setText("Воспроизведение записи...");

            player.setOnCompletionListener(mp -> stopPlaying());

        } catch (IOException | RuntimeException e) {
            isPlaying = false;
            releasePlayer();
            Toast.makeText(requireContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        if (player != null) {
            releasePlayer();
        }

        isPlaying = false;

        if (buttonPlay != null) {
            buttonPlay.setText("Воспроизвести запись");
        }

        if (buttonRecord != null) {
            buttonRecord.setEnabled(true);
        }

        if (textViewStatus != null && recordFilePath != null) {
            textViewStatus.setText("Готово\nФайл: " + recordFilePath);
        }
    }

    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isRecording) {
            stopRecording();
        }

        if (isPlaying) {
            stopPlaying();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releaseRecorder();
        releasePlayer();
    }
}