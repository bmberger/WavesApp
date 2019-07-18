package com.example.waves_app;

import android.app.DatePickerDialog;
import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.model.Task;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<Task> mTasksList;

    public TaskAdapter (Context context, List<Task> tasks) {
        this.context = context;
        this.mTasksList = tasks;
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
                    task.setDueDate(dueDate);
                }
            };

            etTask.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // When focus is lost check that the text field has valid values.
                    if (!hasFocus) {
                        // If anything was typed
                        if (etTask.getText().toString().length() > 0) {
                            Log.d("test", etTask.getText().toString());
                            task.setTaskDetail(etTask.getText().toString());
                        } else {
                            Toast.makeText(v.getContext(), "No task description has been entered!", Toast.LENGTH_SHORT).show();
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