package com.example.waves_app.fragments;

import android.os.Bundle;
import org.apache.commons.io.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;
import com.example.waves_app.TaskAdapter;
import com.example.waves_app.model.Category;
import com.example.waves_app.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    public static final String TAG = "TasksFragment";
    private Button btnAddTask;
    private RecyclerView rvTasks;
    private List<Task> mTasksList;
    private List<String> parsedData;
    private TaskAdapter taskAdapter;
    private String catTasks;

    // returns the file in which the data is stored
    // TODO: Make to-do dependent on the actual category
    private File getDataFile() {
        return new File(getContext().getFilesDir(), catTasks);
    }

    // read the items from the file system
    public void readTaskItems() {
        mTasksList = new ArrayList<>();
        try {
            // create the array using the content in the file
            parsedData = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));

            for(String obj : parsedData) {
                Task tempTask = new Task();

                int delimiter = obj.indexOf(",");
                String note = obj.substring(0, delimiter);
                String date = obj.substring(delimiter + 1);

                tempTask.setTaskDetail(note);
                tempTask.setDueDate(date);

                mTasksList.add(tempTask);
            }

        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
            // just load an empty list
            mTasksList = new ArrayList<>();
            parsedData = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnAddTask = (Button) view.findViewById(R.id.btnAddTask);
        rvTasks = (RecyclerView) view.findViewById(R.id.rvTasks);

        // getting the category file name that contains these tasks
        Bundle information = getArguments();
        catTasks = information.getString("catName") + ".txt";

        readTaskItems();
        taskAdapter = new TaskAdapter(getContext(), mTasksList, parsedData, catTasks);
        rvTasks.setAdapter(taskAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        rvTasks.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayout.VERTICAL));

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task();
                mTasksList.add(task);
                taskAdapter.notifyDataSetChanged();
            }
        });
    }

}