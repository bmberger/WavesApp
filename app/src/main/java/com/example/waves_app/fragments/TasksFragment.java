package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.ItemMoveCallbackCategory;
import com.example.waves_app.ItemMoveCallbackTask;
import com.example.waves_app.OnStartDragListener;
import com.example.waves_app.R;
import com.example.waves_app.SwipeToDeleteCategoryCallback;
import com.example.waves_app.SwipeToDeleteTaskCallback;
import com.example.waves_app.TaskAdapter;
import com.example.waves_app.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements OnStartDragListener {

    public static final String TAG = "TasksFragment";
    private FloatingActionButton fabAddTask;
    private RecyclerView rvTasks;
    private List<Task> mTasksList;
    private List<String> parsedData;
    private TaskAdapter taskAdapter;
    private String catTasks;

    // returns the file in which the data is stored
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
        rvTasks = (RecyclerView) view.findViewById(R.id.rvTasks);
        fabAddTask = (FloatingActionButton) view.findViewById(R.id.fabAddTask);

        // getting the category file name that contains these tasks
        Bundle information = getArguments();
        catTasks = information.getString("catName") + ".txt";

        readTaskItems();

        taskAdapter = new TaskAdapter(getContext(), mTasksList, parsedData, catTasks);

        ItemTouchHelper.Callback callback =
                new ItemMoveCallbackTask(taskAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvTasks);

        rvTasks.setAdapter(taskAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        // Attaching swipe capabilities to the recyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteTaskCallback(taskAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(rvTasks);

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prevent user with adding multiple blank categories
                RecyclerView.ViewHolder lastTask = rvTasks.findViewHolderForAdapterPosition(taskAdapter.getItemCount() - 1);
                EditText etTaskDescription = (EditText) lastTask.itemView.findViewById(R.id.etTaskDescription);
                TextView tvDueDate = (TextView) lastTask.itemView.findViewById(R.id.tvDueDate);

                if (etTaskDescription.getText().length() > 0 && !tvDueDate.getText().toString().equals("set due date")){
                    Task task = new Task();
                    mTasksList.add(task);
                    taskAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Fill out the current blank task!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteTaskCallback(taskAdapter, getContext()));
        itemTouchHelper.startDrag(viewHolder);
    }
}