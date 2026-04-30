package ru.mirea.samoilenko.mireaproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWorkFragment extends Fragment {

    private static final String FILE_NAME = "notes.txt";
    private EditText editTextNote;
    private TextView textViewFileNotes;
    private Button buttonSaveNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_work, container, false);

        // Инициализация элементов
        editTextNote = view.findViewById(R.id.editTextNote);
        textViewFileNotes = view.findViewById(R.id.textViewFileNotes);
        buttonSaveNote = view.findViewById(R.id.buttonSaveNote);

        // Загрузка заметок из файла при запуске
        loadNotesFromFile();

        // Обработчик кнопки для сохранения заметки
        buttonSaveNote.setOnClickListener(v -> {
            String noteText = editTextNote.getText().toString().trim();

            if (!noteText.isEmpty()) {
                saveNoteToFile(noteText);  // Сохраняем заметку в файл
                loadNotesFromFile();  // Обновляем отображение
                editTextNote.setText("");  // Очищаем поле ввода после сохранения
            } else {
                Toast.makeText(requireContext(), "Заметка не может быть пустой", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Сохранение заметки в файл
    private void saveNoteToFile(String noteText) {
        try (FileOutputStream fos = requireContext().openFileOutput(FILE_NAME, Context.MODE_APPEND)) {
            fos.write((noteText + "\n").getBytes());
            Toast.makeText(requireContext(), "Заметка сохранена", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    // Загрузка заметок из файла
    private void loadNotesFromFile() {
        try (FileInputStream fis = requireContext().openFileInput(FILE_NAME)) {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String text = new String(bytes);
            textViewFileNotes.setText(text);  // Отображаем все сохраненные заметки
        } catch (IOException e) {
            textViewFileNotes.setText("Нет заметок");  // Если файла нет, показываем "Нет заметок"
        }
    }
}