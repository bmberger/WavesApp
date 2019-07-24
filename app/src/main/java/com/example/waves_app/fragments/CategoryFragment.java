package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.CategoryAdapter;
import com.example.waves_app.ItemMoveCallbackCategory;
import com.example.waves_app.OnStartDragListener;
import com.example.waves_app.R;
import com.example.waves_app.SwipeToDeleteCategoryCallback;
import com.example.waves_app.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements OnStartDragListener {

    private List<Category> categories;
    private CategoryAdapter categoryAdapter;
    private RecyclerView rvCategories;
    private FloatingActionButton fabAddCategory;
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
        fabAddCategory = (FloatingActionButton) view.findViewById(R.id.fabAddCategory);

        readCategoryItems();

        num = taskCount();

        // Create the categoryAdapter
        categoryAdapter = new CategoryAdapter(getContext(), categories, parsedData, num);

        // Set the layout manager on the recycler view
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback =
                new ItemMoveCallbackCategory(categoryAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvCategories);

        // Set the categoryAdapter on the recycler view
        rvCategories.setAdapter(categoryAdapter);

        // Attaching swipe capabilities to the recyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCategoryCallback(categoryAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(rvCategories);

        // set on click listener on fab
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prevent user with adding multiple blank categories
                // First need to grab the last added category to check if it has been named
                RecyclerView.ViewHolder lastCategory = rvCategories.findViewHolderForAdapterPosition(categoryAdapter.getItemCount() - 1);
                EditText etCatName = (EditText) lastCategory.itemView.findViewById(R.id.etNewCategory);
                if (etCatName.getText().toString().length() > 0) {
                    Category category = new Category();
                    categories.add(category);
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Fill out the current blank category!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCategoryCallback(categoryAdapter, getContext()));
        itemTouchHelper.startDrag(viewHolder);
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