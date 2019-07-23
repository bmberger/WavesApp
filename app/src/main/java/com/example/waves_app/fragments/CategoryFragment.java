package com.example.waves_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.CategoryAdapter;
import com.example.waves_app.R;
import com.example.waves_app.model.Category;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryFragment extends Fragment {

    private List<Category> categories;
    private CategoryAdapter categoryAdapter;
    private RecyclerView rvCategories;
    private Button button;
    private List<String> parsedData;
    private List<String> taskData;
    private List<Integer> num;

    // returns the file in which the data is stored
    private File getDataFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
    }

    // read the items from the file system
    public void readCategoryItems() {
        categories = new ArrayList<>();
        try {
            // create the array using the content in the file
            parsedData = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));

            for(String obj : parsedData) {
                Category tempCat = new Category();
                String name = obj;

                tempCat.setCategoryName(name);
                categories.add(tempCat);
            }

        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
            // just load an empty list
            categories = new ArrayList<>();
            parsedData = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvCategories = view.findViewById(R.id.categoriesList);

        readCategoryItems();

        num = taskCount();

        // Create the categoryAdapter
        categoryAdapter = new CategoryAdapter(getContext(), categories, parsedData, num);

        // Set the layout manager on the recycler view
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set the categoryAdapter on the recycler view
        rvCategories.setAdapter(categoryAdapter);

        // set on click listener on button
        button = view.findViewById(R.id.btnAdd);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Category category = new Category();
                categories.add(category);
                categoryAdapter.notifyDataSetChanged();
            }
        });
    }

    public List<Integer> taskCount() {
        num = new ArrayList<>();
        for(String obj : parsedData) {
            String cat = obj + ".txt";
            readTaskItems(cat);
            num.add(taskData.size());
        }
        return num;
    }

    private File getTaskFile(String cat) {
        return new File(getContext().getFilesDir(), cat);
    }

    public void readTaskItems(String cat) {
        try {
            // create the array of tasks
            taskData = new ArrayList<String>(FileUtils.readLines(getTaskFile(cat), Charset.defaultCharset()));
        } catch (IOException e) {
            taskData = new ArrayList<>();
            e.printStackTrace();
        }
    }


}