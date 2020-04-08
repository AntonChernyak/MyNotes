package ru.graduatework.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    CustomAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_view, parent, false);
        }

        String noteData = getItem(position);

        TextView noteTitle = convertView.findViewById(R.id.item_note_title);
        TextView noteText = convertView.findViewById(R.id.item_note_text);
        TextView nodeDate = convertView.findViewById(R.id.item_date);

        if (noteData != null) {
            String[] noteDataArray = noteData.split("\n");

            nodeDate.setText(noteDataArray[0]);
            if (noteDataArray.length > 1) {
                noteTitle.setText(noteDataArray[1]);
                noteText.setText(arrayToString(copyPartArray(noteDataArray, 2)));
            }
        }

        return convertView;
    }

    private String[] copyPartArray(String[] a, int start) {
        if (a == null)
            return null;
        if (start > a.length)
            return null;
        String[] r = new String[a.length - start];
        System.arraycopy(a, start, r, 0, a.length - start);
        return r;
    }

    private String arrayToString(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

}