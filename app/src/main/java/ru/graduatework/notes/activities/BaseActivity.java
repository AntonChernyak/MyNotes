package ru.graduatework.notes.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ru.graduatework.notes.App;
import ru.graduatework.notes.R;
import ru.graduatework.notes.model.Note;


@SuppressLint("Registered")
class BaseActivity extends AppCompatActivity {

    final int RUS = 0;
    final int ENG = 1;

    void handleMenu(Activity activity, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.finish();
                return;
            case R.id.action_save:
                // если изменяем заметку, то старую удалить
                Intent intent = activity.getIntent();
                int noteId = intent.getIntExtra(ListOfNotesActivity.NOTE_ID_KEY, 0);
                if (noteId != 0) {
                    App.getNoteRepository().deleteById(noteId);
                }

                EditText noteTitleEditText = activity.findViewById(R.id.noteTitleEditText);
                EditText noteTextEditText = activity.findViewById(R.id.noteTextEditText);
                EditText noteDateEditText = activity.findViewById(R.id.dateTimeEditText);

                String noteTitle = noteTitleEditText.getText().toString();
                String noteText = noteTextEditText.getText().toString();
                String dateAndTime = noteDateEditText.getText().toString();

                // сохранение заметки
                Note note = new Note(noteId, noteTitle, noteText, dateAndTime);
                App.getNoteRepository().saveNote(note);
                return;
            case R.id.action_settings:
                Intent intent2 = new Intent(activity, SettingsActivity.class);
                activity.startActivity(intent2);
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

    // Метод выставляет языковые настройки по данным из памяти (предыдущему выбору пользователя)
    void onActivityCreateSetLocale() {
        SharedPreferences mySpinnersSharedPref = getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        int localePosition = mySpinnersSharedPref.getInt(SettingsActivity.LANG_SPINNER_VALUE, 0);
        Locale localeLang;

        switch (localePosition) {
            default:
            case RUS:
                localeLang = new Locale("ru");
                break;
            case ENG:
                localeLang = new Locale("en");
                break;
        }

        Configuration config = new Configuration();
        config.setLocale(localeLang);
        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

}