package ru.netology.lists;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.netology.lists.databinding.ActivityListViewBinding;

public class ListViewActivity extends AppCompatActivity {

    private final static String KEY1 = "key1";
    private final static String KEY2 = "key2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        ActivityListViewBinding binding = ActivityListViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);

        List<Map<String, String>> values = prepareContent();

        BaseAdapter listContentAdapter = createAdapter(values);
        binding.list.setAdapter(listContentAdapter);
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> dataList) {
        return new SimpleAdapter(this, dataList, R.layout.simple_adapter_view,
                new String[]{KEY1, KEY2}, new int[]{R.id.title, R.id.subtitle});
    }

    @NonNull
    private List<Map<String, String>> prepareContent() {
        Map<String, String> dataMap;
        String subtitle;
        List<Map<String, String>> dataList = new ArrayList<>();
        String[] titleArray = getString(R.string.large_text).split("\n\n");

        for (String title : titleArray) {
            dataMap = new HashMap<>();
            dataMap.put(KEY1, title.trim());
            subtitle = Integer.toString(title.length());
            dataMap.put(KEY2, subtitle);
            dataList.add(dataMap);
        }

        return dataList;
    }

}
                                                                                                                                                                                                                                                                                  