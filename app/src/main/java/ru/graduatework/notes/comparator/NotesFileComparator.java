package ru.graduatework.notes.comparator;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import ru.graduatework.notes.model.Note;

public class NotesFileComparator implements Comparator<Note> {
    private Context mContext;

    public NotesFileComparator(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int compare(Note note1, Note note2) {
        File file1 = new File(mContext.getFilesDir(), String.valueOf(note1.getId()));
        File file2 = new File(mContext.getFilesDir(), String.valueOf(note2.getId()));

        // если дедлайн есть у заметок, то
        if ((!note1.getDate().isEmpty() && !note2.getDate().isEmpty())) {
            if (note1.getDate().equals(note2.getDate())) {
                // если даты совпали, то сравниваем по дате последнего изменения файла
                return (int) ((int) file2.lastModified() - file1.lastModified());
            } else {
                // если даты не совпали, то сравниваем по датам
                return parseString(note1.getDate()).compareTo(parseString(note2.getDate()));
            }
        } else if ((!note1.getDate().isEmpty() && note2.getDate().isEmpty()) ||
                (!note2.getDate().isEmpty() && note1.getDate().isEmpty())) {
            // если дата есть только у одной заметки
            return note2.getDate().compareTo(note1.getDate());
        } else {
            // если дедлайна нет, то сразу сравниваем по последнему изменению файла
            return (int) ((int) file2.lastModified() - file1.lastModified());
        }
    }

    private Date parseString(String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-y, HH:mm");
        Date parsingDate;
        try {
            parsingDate = dateFormat.parse(date);
            return parsingDate;
        } catch (ParseException e) {
            return new Date();
        }
    }

}
