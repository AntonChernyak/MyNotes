package ru.graduatework.notes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ru.graduatework.notes.databinding.ActivityListOfNotesBinding;

public class ListOfNotesActivity extends BaseActivity {

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
        this.setTitle(R.string.notes);
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
        initData();
        initListView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.languageChange(ListOfNotesActivity.this);
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
        handleMenu(ListOfNotesActivity.this, item);
        return super.onOptionsItemSelected(item);
    }

    // Выход из приложения по двойному нажатию на кнопку назад
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            // убираем тост, чтобы не светился после закрытия
            backToast.cancel();
            // закрываем приложение
            Intent intent = new Intent(ListOfNotesActivity.this, PinCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(FINISH_APP_KEY, true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.double_back_pressed, Toast.LENGTH_SHORT);
            backToast.show();
        }
        back_pressed = System.currentTimeMillis();
    }

    // установим адаптер
    private void initData() {
        File dataFile = new File(getFilesDir(), NOTES_DATA_FILE_NAME);
        String dataString = Utils.readFileToString(dataFile);
        String[] notesArray = dataString.split(NEW_NOTE_LABEL);
        notesList.addAll(Arrays.asList(notesArray));

        if (dataString.isEmpty()) notesList.clear();

        Collections.sort(notesList);
        Collections.reverse(notesList);
    }

    private void initListView(){
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

            String noteDate = noteDataArray[0];
            String noteTitle = "";
            String noteText = "";
            if (noteDataArray.length > 1) {
                noteTitle = noteDataArray[1];
                noteText = Utils.arrayToString(Utils.copyPartArray(noteDataArray, 2));
            }

            intent.putExtra(NOTE_TITLE_INTENT_KEY, noteTitle);
            intent.putExtra(NOTE_TEXT_INTENT_KEY, noteText);
            intent.putExtra(NOTE_DATE_INTENT_KEY, noteDate);
            intent.putExtra(NOTE_DATA_KEY, noteData);

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
            File dataFile = new File(getFilesDir(), NOTES_DATA_FILE_NAME);
            Utils.deleteStringFromFile(notes.get(deletePosition), dataFile);
            notes.remove(deletePosition);
            adapter.notifyDataSetChanged();
        });

        // кнопка отмена
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        // покажем диалог
        builder.show();
    }
}