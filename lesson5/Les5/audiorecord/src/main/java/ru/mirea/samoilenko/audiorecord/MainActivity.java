package ru.mirea.samoilenko.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ru.mirea.samoilenko.audiorecord.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private boolean isWork = false;
    private String recordFilePath;

    private Button recordButton;
    private Button playButton;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private boolean isStartRecording = true;
    private boolean isStartPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recordButton = binding.recordButton;
        playButton = binding.playButton;

        playButton.setEnabled(false);

        recordFilePath = new File(
                getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "audiorecordtest.3gp"
        ).getAbsolutePath();

        int audioRecordPermissionStatus =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_PERMISSION
            );
        }

        recordButton.setOnClickListener(v -> {
            if (!isWork) return;

            if (isStartRecording) {
                recordButton.setText("Остановить запись");
                playButton.setEnabled(false);
                startRecording();
            } else {
                recordButton.setText("Начать запись. БСБО-08-23, №23");
                playButton.setEnabled(true);
                stopRecording();
            }
            isStartRecording = !isStartRecording;
        });

        playButton.setOnClickListener(v -> {
            if (!isWork) return;

            if (isStartPlaying) {
                playButton.setText("Остановить воспроизведение");
                recordButton.setEnabled(false);
                startPlaying();
            } else {
                playButton.setText("Воспроизвести");
                recordButton.setEnabled(true);
                stopPlaying();
            }
            isStartPlaying = !isStartPlaying;
        });
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (recorder != null) {
            stopRecording();
        }

        if (player != null) {
            stopPlaying();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            isWork = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

        if (!isWork) {
            finish();
        }
    }
}