package ru.mirea.samoilenko.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class BackgroundTaskFragment extends Fragment {

    public BackgroundTaskFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_background_task, container, false);

        Button buttonRunTask = view.findViewById(R.id.buttonRunTask);
        TextView textTaskInfo = view.findViewById(R.id.textTaskInfo);

        buttonRunTask.setOnClickListener(v -> {
            WorkRequest workRequest =
                    new OneTimeWorkRequest.Builder(SimpleWorker.class).build();

            WorkManager.getInstance(requireContext()).enqueue(workRequest);
            textTaskInfo.setText("Task enqueued");
        });

        return view;
    }
}