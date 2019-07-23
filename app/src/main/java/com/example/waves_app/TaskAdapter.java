package com.example.waves_app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.model.Task;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<Task> mTasksList;
    private List<String> parsedData;
    private String catTasks; // sets the category file name that contains all of the tasks
    int pos;
    int completedTasks;
    boolean addingAction = false;

    // Variables to be used if user wants to undo delete/completion of task
    private Task recentlyConfiguredTask;
    private int configuredTaskPosition;

    public TaskAdapter (Context context, List<Task> tasks, List<String> twoStrings, String catTasks) {
        this.context = context;
        this.mTasksList = tasks;
        this.parsedData = twoStrings;
        this.catTasks = catTasks;
    }

    // Returns the file in which the completedTask count is stored
    private File getCompletedTaskCountFile() { return new File(context.getFilesDir(), "completedTaskCount.txt"); }

    // Write the count into the filesystem
    private void writeCompletedCount() {
        try {
            FileUtils.writeStringToFile(getCompletedTaskCountFile(), Integer.toString(completedTasks), (String) null, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set the completedTasks count by reading what's currently in the file
    private void readCompletedCount() {
        try {
            String count = FileUtils.readFileToString(getCompletedTaskCountFile(), (String) null);
            completedTasks = Integer.parseInt(count);
        } catch (IOException e) {
            completedTasks = 0;
            e.printStackTrace();
        }
    }

    // Returns the file in which the data is stored
    private File getDataFile() {
        return new File(context.getFilesDir(), catTasks);
    }

    // Write the items to the filesystem
    private void writeTaskItems() {
        try {
            // save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), parsedData);
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
        }
    }

    // With the data at the given position, bind it to the holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = mTasksList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return mTasksList.size();
    }

    // Creates one individual row in the recycler view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    // Following four methods are used in part with swipe functionality of recyclerView
    public void deleteTask(int pos, RecyclerView.ViewHolder holder) {
        recentlyConfiguredTask = mTasksList.get(pos);
        configuredTaskPosition = pos;

        mTasksList.remove(pos);
        parsedData.remove(pos);
        writeTaskItems();
        notifyDataSetChanged();

        Snackbar.make(holder.itemView, "Undo task deletion", Snackbar.LENGTH_LONG)
                .setAction("UNDO", myOnClickListenerDelete)
                .setActionTextColor(ContextCompat.getColor(context, R.color.blue_5))
                .show();
    }

    View.OnClickListener myOnClickListenerDelete = new View.OnClickListener(){
        public void onClick(View v){
            mTasksList.add(configuredTaskPosition, recentlyConfiguredTask);
            parsedData.add(configuredTaskPosition, recentlyConfiguredTask.getTaskDetail() + "," + recentlyConfiguredTask.getDueDate());
            writeTaskItems();
            notifyItemInserted(configuredTaskPosition);
        }
    };

    public void markComplete(int pos, RecyclerView.ViewHolder holder) {
        recentlyConfiguredTask = mTasksList.get(pos);
        configuredTaskPosition = pos;

        mTasksList.remove(pos);
        parsedData.remove(pos);

        // Set the count for completedTasks
        readCompletedCount();
        completedTasks += 1;

        writeTaskItems();
        writeCompletedCount();
        notifyDataSetChanged();

        Snackbar.make(holder.itemView, "Undo task completion", Snackbar.LENGTH_LONG)
                .setAction("UNDO", myOnClickListenerComplete)
                .setActionTextColor(ContextCompat.getColor(context, R.color.blue_5))
                .show();
    }

    View.OnClickListener myOnClickListenerComplete = new View.OnClickListener(){
        public void onClick(View v){
            mTasksList.add(configuredTaskPosition, recentlyConfiguredTask);
            parsedData.add(configuredTaskPosition, recentlyConfiguredTask.getTaskDetail() + "," + recentlyConfiguredTask.getDueDate());
            completedTasks -= 1;

            writeTaskItems();
            writeCompletedCount();
            notifyItemInserted(configuredTaskPosition);
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        private EditText etTask;
        private TextView tvDueDate;
        private TextView tvDueDateHolder;
        private DatePickerDialog.OnDateSetListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Perform findViewById lookups
            etTask = (EditText) itemView.findViewById(R.id.etTaskDescription);
            tvDueDate = (TextView) itemView.findViewById(R.id.tvDueDate);
            tvDueDateHolder = (TextView) itemView.findViewById(R.id.tvDateHolder);
        }

        public void bind(final Task task) {
            etTask.setText(task.getTaskDetail());
            tvDueDateHolder.setText("Due Date:");

            if (task.getDueDate() == null) {
                tvDueDate.setText("set due date");
            } else {
                tvDueDate.setText(task.getDueDate());
            }

            // Configuration for date picker
            tvDueDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            view.getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            listener,
                            year, month, day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });

            listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month += 1;
                    String dueDate = reformatDate(month, day, year);
                    tvDueDate.setText(dueDate);

                    // forces the user to have a task note before setting the due date
                    if (etTask.getText().toString().length() > 0 && task.getDueDate() != null) {
                        // the case if the user needs to edit the date
                        pos = getAdapterPosition();
                        task.setDueDate(dueDate);
                        parsedData.set(pos, task.getTaskDetail() + "," + task.getDueDate());
                        writeTaskItems(); // update the persistence
                    } else if (etTask.getText().toString().length() > 0) {
                        // the case if the user is setting date
                        task.setDueDate(dueDate);
                        task.setTaskDetail(etTask.getText().toString());
                        addingAction = true; // this gives us the power to avoid problems with add vs editing
                        parsedData.add(task.getTaskDetail() + "," + task.getDueDate());
                        writeTaskItems(); // update the persistence
                    } else {
                        //Toast.makeText(this.getContext(), "No task description has been entered!", Toast.LENGTH_LONG).show();
                    }
                }
            };

            // Get data from editText and set description for new task
            etTask.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String ogDetail = task.getTaskDetail();
                    String newDetail = etTask.getText().toString();

                    // When focus is lost check that the text field has valid values.
                    if (!hasFocus) {
                        if (ogDetail == null) {
                            // When you have no due date for a task
                            Toast.makeText(context, "Due due date needed! Re-enter task.", Toast.LENGTH_LONG).show();
                        } else if (etTask.getText().toString().length() > 0 && !ogDetail.equals(newDetail) && !addingAction) {
                            // the case if the user needs to edit the name
                            task.setTaskDetail(newDetail);
                            parsedData.set(pos, task.getTaskDetail() + "," + task.getDueDate());
                            writeTaskItems(); // update the persistence
                        } else if (etTask.getText().toString().length() > 0 && !addingAction) {
                            // the case if the user is setting name
                            task.setTaskDetail(newDetail);
                            parsedData.add(task.getTaskDetail() + "," + task.getDueDate());
                            writeTaskItems(); // update the persistence
                        }
                    } else {
                        // fixes the add on add issue that Android Studio doesn't account for
                        for (int i = 0; i < parsedData.size(); i++) {
                            String temp = parsedData.get(i);
                            int delimiter = temp.indexOf(",");

                            if (etTask.getText().toString().equals(temp.substring(0, delimiter))) {
                                pos = i;
                            }
                        }
                    }
                }
            });
        }

        public String reformatDate(int month, int day, int year) {
            // If the month and day are both double digits, return the original format
            if (month >= 10 && day >= 10) {
                return month + "/" + day + "/" + year;
            }

            String date = "";
            if (month < 10) {
                date = "0" + month;
            } else {
                date += month;
            }

            if (day < 10) {
                date += "/0" + day;
            } else {
                date += "/" + day;
            }

            return date + "/" + year;
        }
    }
}