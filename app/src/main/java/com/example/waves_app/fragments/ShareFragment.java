package com.example.waves_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;
import com.example.waves_app.model.Task;
import com.webianks.library.scroll_choice.ScrollChoice;
import com.webianks.library.scroll_choice.ScrollChoice;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ShareFragment extends Fragment {

    private ScrollChoice scrollChoice;
    private List<String> categoryData;
    private final String LOG_TAG = "test";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.scrollChoice = (ScrollChoice) view.findViewById(R.id.scroll_choice);

       readCategoryItems();

       List<String> data = new ArrayList<>();
       for (String categoryName : categoryData) {
           data.add(categoryName);
       }

        scrollChoice.addItems(data,0);

        scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                Log.d(LOG_TAG,name);
            }
        });
    }

    private void readCategoryItems() {
        try {
            // create the array of categories
            categoryData = new ArrayList<String>(FileUtils.readLines(getCategoriesFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            categoryData = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private File getCategoriesFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
    }

    private File getTasksFile(String category) {
        return new File(getContext().getFilesDir(), category);
    }

}
