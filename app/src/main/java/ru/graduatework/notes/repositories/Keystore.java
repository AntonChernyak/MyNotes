package ru.graduatework.notes.repositories;

public interface Keystore {

    boolean checkPin(String pin) throws Exception;

    void saveNew(String pin);
}
