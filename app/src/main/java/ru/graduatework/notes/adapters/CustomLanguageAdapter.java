package ru.graduatework.notes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.graduatework.notes.R;

public class CustomLanguageAdapter extends BaseAdapter {

    private int[] images;
    private String[] languages;
    private LayoutInflater inflater;

    public CustomLanguageAdapter(Context applicationContext, int[] flags, String[] fruit) {
        this.images = flags;
        this.languages = fruit;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.spinner_custom_layout, null);
        ImageView icon = view.findViewById(R.id.imageView);
        TextView names = view.findViewById(R.id.textView);
        icon.setImageResource(images[i]);
        names.setText(languages[i]);
        return view;
    }
}