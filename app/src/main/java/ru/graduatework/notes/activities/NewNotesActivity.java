package ru.graduatework.notes.activities;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import ru.graduatework.notes.App;
import ru.graduatework.notes.R;
import ru.graduatework.notes.databinding.ActivityNewNotesBinding;
import ru.graduatework.notes.model.Note;

public class NewNotesActivity extends BaseActivity {

    private ActivityNewNotesBinding binding;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.new_note);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        binding = ActivityNewNotesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // устанавливаем кнопку назад в appBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        deadLineCheckBoxInit();
        getIntentData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleMenu(NewNotesActivity.this, item);
        return super.onOptionsItemSelected(item);
    }

    // отображаем диалоговое окно для выбора даты и времени
    private void initCalendarButton() {
        binding.calendarImageButton.setOnClickListener(v -> {

            new TimePickerDialog(NewNotesActivity.this, time,
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), true)
                    .show();

            new DatePickerDialog(NewNotesActivity.this, date,
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
    }

    // установка обработчика выбора даты
    private DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setInitialDateTime();
    };

    // установка обработчика выбора времени
    private TimePickerDialog.OnTimeSetListener time = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        setInitialDateTime();
    };

    // установка даты и времени
    private void setInitialDateTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-y, HH:mm");
        binding.dateTimeEditText.setText(dateFormat.format(dateAndTime.getTimeInMillis()));
    }

    // Работа Deadline checkbox
    private void deadLineCheckBoxInit() {
        ImageButton calendarImageButton = binding.calendarImageButton;
        EditText dataTimeEditText = binding.dateTimeEditText;

        calendarImageButton.setEnabled(false);
        dataTimeEditText.setEnabled(false);
        binding.deadLineCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                calendarImageButton.setEnabled(true);
                dataTimeEditText.setEnabled(true);
                setInitialDateTime();
                initCalendarButton();
            } else {
                calendarImageButton.setEnabled(false);
                dataTimeEditText.setText("");
                dataTimeEditText.setEnabled(false);

            }
        });
    }

    // получим данные из интента
    private void getIntentData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra(ListOfNotesActivity.NOTE_ID_KEY, 0);

        Note note = App.getNoteRepository().getNoteById(id);

        String noteTitle = note.getTitle();
        String noteText = note.getText();
        String noteDate = note.getDate();

        if (!"".equals(noteDate) && noteDate != null) {
            binding.deadLineCheckBox.setChecked(true);
            binding.calendarImageButton.setEnabled(true);
            binding.dateTimeEditText.setEnabled(true);
        }

        binding.noteTitleEditText.setText(noteTitle);
        binding.noteTextEditText.setText(noteText);
        binding.dateTimeEditText.setText(noteDate);

    }

}