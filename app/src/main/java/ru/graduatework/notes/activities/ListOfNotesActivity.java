package ru.graduatework.notes.activities;

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

import ru.graduatework.notes.App;
import ru.graduatework.notes.adapters.CustomAdapter;
import ru.graduatework.notes.R;
import ru.graduatework.notes.model.Note;
import ru.graduatework.notes.databinding.ActivityListOfNotesBinding;

public class ListOfNotesActivity extends BaseActivity {

    private ActivityListOfNotesBinding binding;
    private long back_pressed;
    private Toast backToast;
    public static final String NOTE_ID_KEY = "note_id_key";

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
            super.onBackPressed();
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.double_back_pressed, Toast.LENGTH_SHORT);
            backToast.show();
        }
        back_pressed = System.currentTimeMillis();
    }


    private void initListView() {
        ListView listView = binding.listView;
        // установим адаптер
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), R.layout.item_list_view, App.getNoteRepository().getNotes());
        listView.setAdapter(adapter);

        // обработаем долгое нажатие на элемент списка
        listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
            Note note = App.getNoteRepository().getNotes().get(pos);
            addItemDialog(note.getId(), adapter);
            return true;
        });

        // обработаем короткое нажатие на элемент списка
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ListOfNotesActivity.this, NewNotesActivity.class);
            Note note = App.getNoteRepository().getNotes().get(position);
            intent.putExtra(NOTE_ID_KEY, note.getId());
            startActivity(intent);
        });

    }

    // Добавим диалог для долгого нажатия на элемент списка
    private void addItemDialog(int id, CustomAdapter adapter) {
        String title = getString(R.string.attention);
        String message = getString(R.string.note_delete_notice);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfNotesActivity.this);
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение
        builder.setIcon(R.drawable.ic_delete_black_24dp); // иконка

        // кнопка удалить
        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            App.getNoteRepository().deleteById(id);
            adapter.notifyDataSetChanged();
            recreate();
        });

        // кнопка отмена
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        // покажем диалог
        builder.show();
    }
}