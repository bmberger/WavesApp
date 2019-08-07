/*
 * Project: Waves
 *
 * Purpose: Displays the search page and allows user to search for where a task is located/due date
 *
 * Reference(s): Angela Liu
 */

package com.example.waves_app.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;
import com.example.waves_app.adapters.SearchAdapter;
import com.example.waves_app.model.Task;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private MaterialSearchView svSearch;
    private EditText etSearch;
    private TextView tvResultCount;
    private ImageButton ibClear;
    private RecyclerView rvSearchTasks;
    private SearchAdapter searchAdapter;
    private List<String> searchCategories;
    private List<Task> searchTasks;
    private List<String> categoryData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        getActivity().setTitle(""); // Required for setting action bar title
        view.bringToFront();

        // Notifies host activity that fragment has menu items
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.svSearch = (MaterialSearchView) view.findViewById(R.id.svSearch);
        this.etSearch = (EditText) view.findViewById(R.id.etSearch);
        this.tvResultCount = (TextView) view.findViewById(R.id.tvResultCount);
        this.ibClear = (ImageButton) view.findViewById(R.id.ibClear);
        this.rvSearchTasks = (RecyclerView) view.findViewById(R.id.rvSearchTasks);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Search");
        toolbar.setTitleTextAppearance(getContext(), R.style.MyTitleTextApperance);

        // Keeps track of all the inputs in the editText and populates recyclerView with relevant tasks
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                loadTasks(editable.toString());
            }
        });

        // Clear the search editText if ibClear is pressed
        ibClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
            }
        });
    }

    private void loadTasks(String search) {
        readCategoryItems();

        searchTasks = new ArrayList<>();
        searchCategories = new ArrayList<>();

        // Go through all categories and search for their tasks
        for (String categoryName : categoryData) {
            String categoryFile = categoryName + ".txt";

            try {
                // Read all the tasks in a category
                List<String> tasksRead = new ArrayList<>(FileUtils.readLines(getTasksFile(categoryFile), Charset.defaultCharset()));

                // Load any tasks into the searchTasks list to populate recyclerView
                for (String task : tasksRead) {
                    int delimiter = task.indexOf(",");
                    String taskDetail = task.substring(0, delimiter);
                    String dueDate = task.substring(delimiter + 1);

                    if (taskDetail.toLowerCase().contains(search.toLowerCase())) {
                        Task t = new Task();
                        t.setTaskDetail(taskDetail);
                        t.setDueDate(dueDate);

                        searchCategories.add(categoryName);
                        searchTasks.add(t);
                    }

                    // Update result view with necessary output
                    if (searchTasks.size() == 0) {
                        tvResultCount.setText("There are no tasks for this search.");
                    } else {
                        tvResultCount.setText("Results: " + searchTasks.size());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        searchAdapter = new SearchAdapter(getContext(), searchTasks, searchCategories);
        rvSearchTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchTasks.addItemDecoration(new DividerItemDecoration(rvSearchTasks.getContext(), DividerItemDecoration.VERTICAL));
        rvSearchTasks.setAdapter(searchAdapter);
    }

    private void readCategoryItems() {
        try {
            // Create the array of categories
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.miSearch);
        svSearch.setMenuItem(item);
    }
}