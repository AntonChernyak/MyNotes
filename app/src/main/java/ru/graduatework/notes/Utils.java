package ru.graduatework.notes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import static ru.graduatework.notes.ListOfNotesActivity.NEW_NOTE_LABEL;

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

    // удаляем строку из файла
    public static void deleteStringFromFile(String deleteStr, File dataFile) {
        File temp = null;
        PrintWriter writer = null;
        Scanner scanner;
        String charset = "UTF-8";

        try {
            temp = File.createTempFile("tempDataFile", ".txt", dataFile.getParentFile());
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));

            boolean flag = true;
            scanner = new Scanner(dataFile);
            scanner.useDelimiter(NEW_NOTE_LABEL);
            while (scanner.hasNext()) {
                String line = scanner.next();
                if (line.equals(deleteStr) && flag) {
                    flag = false;
                    continue;
                }
                writer.print(line + NEW_NOTE_LABEL);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert writer != null;
            writer.close();
        }

        dataFile.delete();
        temp.renameTo(dataFile);

    }

}

