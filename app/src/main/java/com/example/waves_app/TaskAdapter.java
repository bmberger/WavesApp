package com.example.waves_app;

import android.app.DatePickerDialog;
import org.apache.commons.io.FileUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.fragments.TasksFragment;
import com.example.waves_app.model.Task;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<Task> mTasksList;
    private List<String> parsedData;
    private String catTasks; // sets the category file name that contains all of the tasks
    int pos;

    public TaskAdapter (Context context, List<Task> tasks, List<String> twoStrings, String catTasks) {
        this.context = context;
        this.mTasksList = tasks;
        this.parsedData = twoStrings;
        this.catTasks = catTasks;
    }

    // returns the file in which the data is stored
    private File getDataFile() {
        return new File(context.getFilesDir(), catTasks);
    }

    // write the items to the filesystem
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

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

            // Attach a long click listener to the entire row view
            itemView.setOnLongClickListener((View.OnLongClickListener)this);
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
                        task.setTaskDetail(newDetail); // due date has all the writing code
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

        @Override
        public boolean onLongClick(View view) {

            // Create dialog popup to confirm deletion of task
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Delete this task?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    pos = getAdapterPosition();
                                    mTasksList.remove(pos);
                                    parsedData.remove(pos);
                                    writeTaskItems();
                                    notifyDataSetChanged();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            final AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    }

    public void getPos(EditText etTask) {
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