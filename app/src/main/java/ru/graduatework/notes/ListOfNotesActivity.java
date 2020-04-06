package ru.graduatework.notes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import ru.graduatework.notes.databinding.ActivityListOfNotesBinding;

public class ListOfNotesActivity extends AppCompatActivity {

    private ActivityListOfNotesBinding binding;
    private long back_pressed;
    private Toast backToast;
    private ArrayList<String> notesList = new ArrayList<>();

    public static final String NEW_NOTE_LABEL = "\n\n###new_notes_label###\n\n";
    public static final String FINISH_APP_KEY = "finish";
    public static final String NOTE_TITLE_INTENT_KEY = "note_title";
    public static final String NOTE_TEXT_INTENT_KEY = "note_text";
    public static final String NOTE_DATE_INTENT_KEY = "note_date";
    public static final String NOTES_DATA_FILE_NAME = "notes_data";
    public static final String NOTE_DATA_KEY = "note_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        binding = ActivityListOfNotesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        addNotesFabButtonOnClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notesList.clear();
        readNotesFromFile();
    }

    // обработка нажатия на fab кнопку лобавления новой заметки
    private void addNotesFabButtonOnClick() {
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(ListOfNotesActivity.this, NewNotesActivity.class);
            startActivity(intent);
        });
    }

    // Добавляем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_of_notes, menu);
        return true;
    }

    // Обработка клика на пункт меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(ListOfNotesActivity.this, SettingsActivity.class);
            startActivity(intent);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    // Выход из приложения по двойному нажатию на кнопку назад
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            // убираем тост, чтобы не светился после закрытия
            backToast.cancel();
            // закрываем приложение
            Intent intent = new Intent(ListOfNotesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(FINISH_APP_KEY, true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.double_back_pressed, Toast.LENGTH_SHORT);
            backToast.show();
        }
        back_pressed = System.currentTimeMillis();
    }

    // Чтение из файла
    private void readNotesFromFile() {
        File dataFile = new File(getFilesDir(), NOTES_DATA_FILE_NAME);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)))) {
            // ищем файл и проверяем, файл ли это. Если да, то считываем
            if (dataFile.isFile()) {

                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                initData(sb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // установим адаптер
    private void initData(StringBuilder sb) {
        String[] notesArray = sb.toString().split(NEW_NOTE_LABEL);
        notesList.addAll(Arrays.asList(notesArray));

        Collections.sort(notesList);
        Collections.reverse(notesList);

        ListView listView = binding.listView;
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), R.layout.item_list_view, notesList);
        listView.setAdapter(adapter);

        // обработаем долгое нажатие на элемент списка
        listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
            addItemDialog(notesList, pos, adapter);
            return true;
        });

        // обработаем короткое нажатие на элемент списка
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ListOfNotesActivity.this, NewNotesActivity.class);

            String noteData = notesList.get(position);
            String[] noteDataArray = noteData.split("\n");

            // String noteTitle = noteDataArray[0];
            String noteDate = noteDataArray[0];
            String noteTitle = "";
            String noteText = "";
            if (noteDataArray.length > 1) {
                noteTitle = noteDataArray[1];
                noteText = arrayToString(copyPartArray(noteDataArray, 2));
            }

            intent.putExtra(NOTE_TITLE_INTENT_KEY, noteTitle);
            intent.putExtra(NOTE_TEXT_INTENT_KEY, noteText);
            intent.putExtra(NOTE_DATE_INTENT_KEY, noteDate);
            intent.putExtra(NOTE_DATA_KEY, noteData);

            //deleteStringFromFile(notesList.get(position));
            startActivity(intent);
        });

    }

    // Добавим диалог для долгого нажатия на элемент списка
    private void addItemDialog(ArrayList<String> notes, int deletePosition, CustomAdapter adapter) {
        String title = getString(R.string.attention);
        String message = getString(R.string.note_delete_notice);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfNotesActivity.this);
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение
        builder.setIcon(R.drawable.ic_delete_black_24dp); // иконка

        // кнопка удалить
        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            deleteStringFromFile(notes.get(deletePosition));
            notes.remove(deletePosition);
            adapter.notifyDataSetChanged();
        });

        // кнопка отмена
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        // покажем диалог
        builder.show();
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

    private String[] copyPartArray(String[] a, int start) {
        if (a == null)
            return null;
        if (start > a.length)
            return null;
        String[] r = new String[a.length - start];
        System.arraycopy(a, start, r, 0, a.length - start);
        return r;
    }

    private String arrayToString(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

}
