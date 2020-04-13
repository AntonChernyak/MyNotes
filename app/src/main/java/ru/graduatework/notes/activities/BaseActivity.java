package ru.graduatework.notes.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ru.graduatework.notes.R;
import ru.graduatework.notes.utils.Utils;

import static ru.graduatework.notes.activities.ListOfNotesActivity.NEW_NOTE_LABEL;
import static ru.graduatework.notes.activities.ListOfNotesActivity.NOTES_DATA_FILE_NAME;

@SuppressLint("Registered")
class BaseActivity extends AppCompatActivity {

    void handleMenu(Activity activity, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.finish();
                return;
            case R.id.action_save:
                // если изменяем заметку, то старую удалить
                Intent intent = activity.getIntent();
                String noteData = intent.getStringExtra(ListOfNotesActivity.NOTE_DATA_KEY);
                if (!"".equals(noteData)) {
                    File dataFile = new File(activity.getFilesDir(), NOTES_DATA_FILE_NAME);
                    Utils.deleteStringFromFile(noteData, dataFile);
                }
                // сохранение заметки
                saveIntoInternalStorage(activity);
                return;
            case R.id.action_settings:
                Intent intent2 = new Intent(activity, SettingsActivity.class);
                activity.startActivity(intent2);
        }
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

    // смена языка после пересоздания активити
    void languageChange(Activity activity) {
        // Если после смены языка нажали кнопку назад, то нужно пересоздать активити со списком заметок
        SharedPreferences mySpinnersSharedPref = activity.getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        int oldLang = mySpinnersSharedPref.getInt(SettingsActivity.OLD_LANG_SPINNER_VALUE, 0);
        int newLang = mySpinnersSharedPref.getInt(SettingsActivity.LANG_SPINNER_VALUE, 0);
        if (oldLang != newLang) {
            SharedPreferences.Editor mySpinnersEditor = mySpinnersSharedPref.edit();
            mySpinnersEditor.putInt(SettingsActivity.OLD_LANG_SPINNER_VALUE, newLang);
            mySpinnersEditor.apply();
            activity.recreate();
        }
    }
}
