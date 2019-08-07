/*
 * Project: Waves
 *
 * Purpose: Populates the search fragment with all the data based on a search that user entered
 *
 * Reference(s): Angela Liu
 */

package com.example.waves_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;
import com.example.waves_app.model.Task;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<Task> searchTasks;
    private List<String> searchCategories;

    public SearchAdapter(Context context, List<Task> searchTasks, List<String> searchCategories) {
        this.context = context;
        this.searchTasks = searchTasks;
        this.searchCategories = searchCategories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = searchTasks.get(position);
        String category = searchCategories.get(position);
        holder.bind(task, category);
    }

    @Override
    public int getItemCount() {
        return searchTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTaskDescription;
        private TextView tvDateHolder;
        private TextView tvDueDate;
        private TextView tvLocationHolder;
        private TextView tvLocation;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvTaskDescription = (TextView) view.findViewById(R.id.tvTaskDescription);
            tvDateHolder = (TextView) view.findViewById(R.id.tvDateHolder);
            tvDueDate = (TextView) view.findViewById(R.id.tvDueDate);
            tvLocationHolder = (TextView) view.findViewById(R.id.tvLocationHolder);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        }

        public void bind(final Task task, String category) {
            tvTaskDescription.setText(task.getTaskDetail());
            tvDateHolder.setText("Due date: ");
            tvDueDate.setText(task.getDueDate());
            tvLocationHolder.setText("Located in: ");
            tvLocation.setText(category);
        }
    }
}