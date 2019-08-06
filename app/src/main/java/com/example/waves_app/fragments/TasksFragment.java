/*
 * Project: Waves
 *
 * Purpose: To display all of the user's tasks for specific category and
 * listens for when a user adds/edits a task
 *
 * Reference(s): Angela Liu, Briana Berger
 */

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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.ItemMoveCallbackTask;
import com.example.waves_app.interfaces.OnStartDragListener;
import com.example.waves_app.R;
import com.example.waves_app.SwipeToDeleteTaskCallback;
import com.example.waves_app.adapters.TaskAdapter;
import com.example.waves_app.model.Task;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements OnStartDragListener {

    private RecyclerView rvTasks;
    private List<Task> mTasksList;
    private List<String> parsedData;
    private TaskAdapter taskAdapter;
    private String catTasks;
    private TextView tvSpaceHolder;

    // Returns the file in which the data is stored
    private File getDataFile() {
        return new File(getContext().getFilesDir(), catTasks);
    }

    // Read the items from the file system
    public void readTaskItems() {
        mTasksList = new ArrayList<>();
        try {
            // Create the array using the content in the file
            parsedData = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));

            // Parses through and creates each task from string in parsedData
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
            // Print the error to the console
            e.printStackTrace();

            // Just load an empty list
            mTasksList = new ArrayList<>();
            parsedData = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        getActivity().setTitle(""); // Required for setting action bar title
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvSpaceHolder = (TextView) view.findViewById(R.id.tvSpaceHolder);
        rvTasks = (RecyclerView) view.findViewById(R.id.rvTasks);

        // Getting the category file name that contains these tasks
        Bundle information = getArguments();
        catTasks = information.getString("catName") + ".txt";

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(information.getString("catName"));

        readTaskItems();

        taskAdapter = new TaskAdapter(getContext(), mTasksList, parsedData, catTasks);

        ItemTouchHelper.Callback callback = new ItemMoveCallbackTask(taskAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvTasks);

        rvTasks.setAdapter(taskAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        // Attaching swipe capabilities to the recyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteTaskCallback(taskAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(rvTasks);

        tvSpaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prevent user with adding multiple blank categories
                RecyclerView.ViewHolder lastTask = rvTasks.findViewHolderForAdapterPosition(taskAdapter.getItemCount() - 1);

                if (lastTask != null) {
                    EditText etTaskDescription = (EditText) lastTask.itemView.findViewById(R.id.etTaskDescription);
                    TextView tvDueDate = (TextView) lastTask.itemView.findViewById(R.id.tvDueDate);

                    if (etTaskDescription.getText().length() > 0){
                        addNewTask();
                    } else {
                        Toast.makeText(getContext(), "Fill out the current blank task!", Toast.LENGTH_SHORT).show();
                    }
                } else { // No tasks yet in the category
                    addNewTask();
                }
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteTaskCallback(taskAdapter, getContext()));
        itemTouchHelper.startDrag(viewHolder);
    }

    public void addNewTask() {
        Task task = new Task();

        task.setDueDate("set due date");
        task.setTaskDetail("");

        mTasksList.add(task);
        parsedData.add(",set due date");

        taskAdapter.notifyDataSetChanged();
        rvTasks.scrollToPosition(taskAdapter.getItemCount() - 1);
    }
}