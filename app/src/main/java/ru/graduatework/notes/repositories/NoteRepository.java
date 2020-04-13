package ru.graduatework.notes.repositories;

import java.util.List;

import ru.graduatework.notes.model.Note;

public interface NoteRepository {
    Note getNoteById(int id);

    List<Note> getNotes();

    void saveNote(Note note);

    void deleteById(String id);
}
