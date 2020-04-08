package ru.graduatework.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Objects;
import java.util.Scanner;

import ru.graduatework.notes.databinding.ActivityNewNotesBinding;

import static ru.graduatework.notes.ListOfNotesActivity.NEW_NOTE_LABEL;
import static ru.graduatework.notes.ListOfNotesActivity.NOTES_DATA_FILE_NAME;

public class NewNotesActivity extends AppCompatActivity {

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
        if (item.getItemId() == R.id.action_save) {
            // если изменяем заметку, то старую удалить
            Intent intent = getIntent();
            String noteData = intent.getStringExtra(ListOfNotesActivity.NOTE_DATA_KEY);
            if (!"".equals(noteData)) {
                deleteStringFromFile(noteData);
            }

            // сохранение заметки
            saveIntoInternalStorage();
            return false;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
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


    // установка начальных даты и времени
    private void setInitialDateTime() {
        binding.dateTimeEditText.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
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

    // Запись в внутреннее хранлище
    private void saveIntoInternalStorage() {
        String noteTitle = binding.noteTitleEditText.getText().toString();
        String noteText = binding.noteTextEditText.getText().toString();
        String dateAndTime = binding.dateTimeEditText.getText().toString();

        if ("".equals(noteTitle) && "".equals(noteText) && "".equals(dateAndTime)) {
            Toast.makeText(this, R.string.empty_fields_notice, Toast.LENGTH_LONG).show();
        } else {
            File file = new File(getFilesDir(), NOTES_DATA_FILE_NAME);
            // если файла не существует, то пишем
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)))) {
                writer.append(dateAndTime).append("\n");
                writer.append(noteTitle).append("\n");
                writer.append(noteText).append(NEW_NOTE_LABEL);
                Toast.makeText(NewNotesActivity.this, R.string.note_created_successfully, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // получим данные из интента
    private void getIntentData() {
        Intent intent = getIntent();
        String noteTitle = intent.getStringExtra(ListOfNotesActivity.NOTE_TITLE_INTENT_KEY);
        String noteText = intent.getStringExtra(ListOfNotesActivity.NOTE_TEXT_INTENT_KEY);
        String noteDate = intent.getStringExtra(ListOfNotesActivity.NOTE_DATE_INTENT_KEY);
        if (!"".equals(noteDate) && noteDate != null) {
            System.out.println(noteDate);
            binding.deadLineCheckBox.setChecked(true);
            binding.calendarImageButton.setEnabled(true);
            binding.dateTimeEditText.setEnabled(true);
        }

        binding.noteTitleEditText.setText(noteTitle);
        binding.noteTextEditText.setText(noteText);
        binding.dateTimeEditText.setText(noteDate);
    }

    // удаляем строку из файла
    private void deleteStringFromFile(String deleteStr) {
        File temp = null;
        PrintWriter writer = null;
        Scanner scanner;
        File dataFile = new File(getFilesDir(), NOTES_DATA_FILE_NAME);
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


}