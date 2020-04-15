package ru.graduatework.notes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.graduatework.notes.R;
import ru.graduatework.notes.model.Note;

public class CustomAdapter extends ArrayAdapter<Note> {

    private LayoutInflater inflater;

    public CustomAdapter(Context context, int resource, List<Note> items) {
        super(context, resource, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_view, parent, false);
        }

        Note note = getItem(position);

        TextView noteTitle = convertView.findViewById(R.id.item_note_title);
        TextView noteText = convertView.findViewById(R.id.item_note_text);
        TextView nodeDate = convertView.findViewById(R.id.item_date);

        if (note != null) {
            noteTitle.setText(note.getTitle());
            noteText.setText(note.getText());
            nodeDate.setText(note.getDate());
        }

        return convertView;
    }

}