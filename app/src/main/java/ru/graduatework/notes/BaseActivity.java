package ru.graduatework.notes;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import static ru.graduatework.notes.ListOfNotesActivity.NEW_NOTE_LABEL;
import static ru.graduatework.notes.ListOfNotesActivity.NOTES_DATA_FILE_NAME;

class BaseActivity {

    void HandleMenu(Activity activity, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.finish();
                return;
            case R.id.action_save:
                // если изменяем заметку, то старую удалить
                Intent intent = activity.getIntent();
                String noteData = intent.getStringExtra(ListOfNotesActivity.NOTE_DATA_KEY);
                if (!"".equals(noteData)) {
                    deleteStringFromFile(noteData, activity);
                }
                // сохранение заметки
                saveIntoInternalStorage(activity);
                return;
            case R.id.action_settings:
                Intent intent2 = new Intent(activity, SettingsActivity.class);
                activity.startActivity(intent2);
        }
    }

    // удаляем строку из файла
    void deleteStringFromFile(String deleteStr, Activity activity) {
        File temp = null;
        PrintWriter writer = null;
        Scanner scanner;
        File dataFile = new File(activity.getFilesDir(), NOTES_DATA_FILE_NAME);
        String charset = "UTF-8";

        try {
            temp = File.createTempFile("tempDataFile", ".txt", dataFile.getParentFile());
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));

            boolean flag = true;
            scanner = new Scanner(dataFile);
            scanner.useDelimiter(NEW_NOTE_LABEL);
            while (scanner.hasNext()) {
                String line = scanner.next();
                if (line.equals(deleteStr) && flag) {
                    flag = false;
                    continue;
                }
                writer.print(line + NEW_NOTE_LABEL);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert writer != null;
            writer.close();
        }

        dataFile.delete();
        temp.renameTo(dataFile);

    }

    // Запись в внутреннее хранилище
    private void saveIntoInternalStorage(Activity activity) {
        EditText noteTitleEditText = activity.findViewById(R.id.noteTitleEditText);
        EditText noteTextEditText = activity.findViewById(R.id.noteTextEditText);
        EditText noteDateEditText = activity.findViewById(R.id.dateTimeEditText);

        String noteTitle = noteTitleEditText.getText().toString();
        String noteText = noteTextEditText.getText().toString();
        String dateAndTime = noteDateEditText.getText().toString();

        if ("".equals(noteTitle) && "".equals(noteText) && "".equals(dateAndTime)) {
            Toast.makeText(activity, R.string.empty_fields_notice, Toast.LENGTH_LONG).show();
        } else {
            File file = new File(activity.getFilesDir(), NOTES_DATA_FILE_NAME);
            // если файла не существует, то пишем
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)))) {
                writer.append(dateAndTime).append("\n");
                writer.append(noteTitle).append("\n");
                writer.append(noteText).append(NEW_NOTE_LABEL);
                Toast.makeText(activity, R.string.note_created_successfully, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
