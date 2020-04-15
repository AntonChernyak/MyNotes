package ru.graduatework.notes.repositories;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import ru.graduatework.notes.R;
import ru.graduatework.notes.comparator.NotesFileComparator;
import ru.graduatework.notes.model.Note;
import ru.graduatework.notes.utils.Utils;

public class FileNoteRepository implements NoteRepository {

    private Context mContext;

    public FileNoteRepository(Context context) {
        this.mContext = context;
    }

    @Override
    public Note getNoteById(int id) {
        File file = new File(mContext.getFilesDir(), String.valueOf(id));

        Note note;
        String title = "";
        String text = "";

        String[] noteArray = Utils.readFileToString(file).split("\n");

        String date = noteArray[0];
        if (noteArray.length > 1) {
            title = noteArray[1];
            text = Utils.arrayToString(Utils.copyPartArray(noteArray, 2));
        }
        note = new Note(id, title, text, date);
        return note;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        for (String id : mContext.fileList()) {
            notes.add(getNoteById(Integer.parseInt(id)));
        }
        notes.sort(new NotesFileComparator(mContext));
        return notes;
    }

    @Override
    public void saveNote(Note note) {
        String noteTitle = note.getTitle();
        String noteText = note.getText();
        String dateAndTime = note.getDate();
        int id = note.getId();

        if(note.getId() == 0) {
            // зададим id по дате в секундах
            id = (int) ((new Date().getTime()) / 1000);
            note.setId(id);
        }

        if (noteTitle.isEmpty() && noteText.isEmpty() && dateAndTime.isEmpty()) {
            Toast.makeText(mContext, R.string.empty_fields_notice, Toast.LENGTH_LONG).show();
        } else {
            // Запись в внутреннее хранилище
            File file = new File(mContext.getFilesDir(), String.valueOf(id));
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)))) {
                writer.append(dateAndTime).append("\n");
                writer.append(noteTitle).append("\n");
                writer.append(noteText);
                Toast.makeText(mContext, R.string.note_created_successfully, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void deleteById(int id) {
        File file = new File(mContext.getFilesDir(), String.valueOf(id));
        if (file.isFile()) {
            file.delete();
        }
    }
}
