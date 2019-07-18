package com.example.waves_app.fragments;

import android.os.Bundle;
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
import com.example.waves_app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    public static final String TAG = "TasksFragment";
    private Button btnAddTask;
    private RecyclerView rvTasks;
    private List<Task> mTasksList;
    private TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnAddTask = (Button) view.findViewById(R.id.btnAddTask);
        rvTasks = (RecyclerView) view.findViewById(R.id.rvTasks);
        mTasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), mTasksList);
        rvTasks.setAdapter(taskAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        rvTasks.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayout.VERTICAL));

        populateView();

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task();
                mTasksList.add(task);
                taskAdapter.notifyDataSetChanged();
            }
        });
    }

    public void populateView() {
        // TODO - use user persistence to write the current tasks
        Task t = new Task();
        t.setDueDate("07/20/2019");
        t.setTaskDetail("Get this working pls");
        mTasksList.add(t);

        Task t2 = new Task();
        t2.setDueDate("07/21/2019");
        t2.setTaskDetail("Get this working");
        mTasksList.add(t2);
        taskAdapter.notifyDataSetChanged();
    }
}