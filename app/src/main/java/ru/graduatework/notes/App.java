package ru.graduatework.notes;

import android.app.Application;

import ru.graduatework.notes.repositories.FileNoteRepository;
import ru.graduatework.notes.repositories.Keystore;
import ru.graduatework.notes.repositories.NoteRepository;
import ru.graduatework.notes.repositories.SaltHashKeystore;
import ru.graduatework.notes.repositories.SimpleKeystore;

public class App extends Application {
    private static NoteRepository noteRepository;
    private static Keystore keystore;

    @Override
    public void onCreate() {
        super.onCreate();

        // Конкретная реализация выбирается только здесь.
        noteRepository = new FileNoteRepository(this);

        //keystore = new SimpleKeystore(this);
        keystore = new SaltHashKeystore(this);
    }


    // Возвращаем интерфейс, а не конкретную реализацию
    public static NoteRepository getNoteRepository() {
        return noteRepository;
    }

    // Возвращаем интерфейс, а не конкретную реализацию
    public static Keystore getKeystore() {
        return keystore;
    }
}
