package ru.graduatework.notes.repositories;

public interface Keystore {
    boolean hashPin();

    boolean checkPin(String pin);

    void saveNew(String pin);
}
