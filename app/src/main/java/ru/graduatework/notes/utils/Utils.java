package ru.graduatework.notes.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

    public static String[] copyPartArray(String[] a, int start) {
        if (a == null)
            return null;
        if (start > a.length)
            return null;
        String[] r = new String[a.length - start];
        System.arraycopy(a, start, r, 0, a.length - start);
        return r;
    }

    public static String arrayToString(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    // Чтение из файла
    public static String readFileToString(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            // ищем файл и проверяем, файл ли это. Если да, то считываем
            if (file.isFile()) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}