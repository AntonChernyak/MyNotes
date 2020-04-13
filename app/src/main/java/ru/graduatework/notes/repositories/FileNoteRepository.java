package ru.graduatework.notes.repositories;

import java.util.List;

import ru.graduatework.notes.model.Note;

public class FileNoteRepository implements NoteRepository {
    @Override
    public Note getNoteById(int id) {
        return null;
    }

    @Override
    public List<Note> getNotes() {
        return null;
    }

    @Override
    public void saveNote(Note note) {

    }

    @Override
    public void deleteById(String id) {

    }
}
