package com.example.waves_app;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import org.apache.commons.io.FileUtils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.model.Category;
import com.example.waves_app.model.Task;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private List<Task> mTasksList;
    private List<String> parsedData;
    private String catTasks; // sets the category file name that contains all of the tasks
    int pos;
    int completedTasks;
    boolean addingAction = false;
    AlarmManager alarmManager;

    // Variables to be used if user wants to undo delete/completion of task
    private Task recentlyConfiguredTask;
    private int configuredTaskPosition;

    public TaskAdapter (Context context, List<Task> tasks, List<String> twoStrings, String catTasks) {
        this.context = context;
        this.mTasksList = tasks;
        this.parsedData = twoStrings;
        this.catTasks = catTasks;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); // sets up alarm manager
    }

    // Returns the file in which the completedTask count is stored
    private File getCompletedTaskCountFile() { return new File(context.getFilesDir(), "completedTaskCount"); }

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

    public void onItemDismiss(int position) {
        mTasksList.remove(position);
        parsedData.remove(position);
        writeTaskItems();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, parsedData.size() - position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < mTasksList.size() && toPosition < mTasksList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mTasksList, i, i + 1);
                    Collections.swap(parsedData, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mTasksList, i, i - 1);
                    Collections.swap(parsedData, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            notifyItemRangeChanged(toPosition, parsedData.size() - toPosition);
            writeTaskItems();
        }
        //notifyDataSetChanged();
        return true;
    }

    public void updateList(List<Task> tasksList, List<String> ParsedData) {
        mTasksList = tasksList;
        parsedData = ParsedData;
        notifyDataSetChanged();
        writeTaskItems();
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
        cancelAlarm(recentlyConfiguredTask.getTaskDetail());
        notifyDataSetChanged();

        Dialog ad_dialog = new Dialog(context);
        ad_dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ad_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ad_dialog.setCancelable(true);
        ad_dialog.setContentView(R.layout.ic_popup);

        // Set congrats words for message on pop-up
        TextView congratsWords = (TextView) ad_dialog.findViewById(R.id.tvCongratsWords);
        congratsWords.setText("You have now saved " + completedTasks + " fish. Make sure to see all the fish you saved by going to your fish tank!");

        // Set the fish image for pop-up
        ImageView fishImage = (ImageView) ad_dialog.findViewById(R.id.ivFishView);
        int fishID = getRandomFishId();
        fishImage.setImageResource(fishID);

        ad_dialog.show();
    }

    public int getRandomFishId() {
        // Generates random integer between 0 and 14 inclusive
        int random = new Random().nextInt(15);
        return context.getResources().getIdentifier("fish_" + random, "drawable", context.getPackageName());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private EditText etTask;
        private TextView tvDueDate;
        private TextView tvDueDateHolder;
        private DatePickerDialog.OnDateSetListener listener;
        AlarmManager alarmManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Perform findViewById lookups
            etTask = (EditText) itemView.findViewById(R.id.etTaskDescription);
            tvDueDate = (TextView) itemView.findViewById(R.id.tvDueDate);
            tvDueDateHolder = (TextView) itemView.findViewById(R.id.tvDateHolder);
        }

        public void bind(final Task task) {
            int id = getColorId(getAdapterPosition());
            itemView.setBackgroundColor(context.getResources().getColor(id));

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
                    getDateInfo(view);
                }
            });

            tvDueDateHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDateInfo(view);
                }
            });

            listener = new DatePickerDialog.OnDateSetListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month += 1;
                    String dueDate = reformatDate(month, day, year);
                    tvDueDate.setText(dueDate);

                    // forces the user to have a task note before setting the due date
                    if (etTask.getText().toString().length() > 0 && task.getDueDate() != null) {
                        // the case if the user needs to edit the date
                        pos = getAdapterPosition();
                        editAlarm(dueDate, task.getTaskDetail(), task.getTaskDetail());
                        task.setDueDate(dueDate);
                        parsedData.set(pos, task.getTaskDetail() + "," + task.getDueDate());
                        writeTaskItems(); // update the persistence
                    } else if (etTask.getText().toString().length() > 0) {
                        // the case if the user is adding/setting date
                        task.setDueDate(dueDate);
                        task.setTaskDetail(etTask.getText().toString());
                        addingAction = true; // this gives us the power to avoid problems with add vs editing
                        parsedData.add(task.getTaskDetail() + "," + task.getDueDate());
                        setAlarm(task.getDueDate(), task.getTaskDetail());
                        writeTaskItems(); // update the persistence
                    }
                }
            };

            // Get data from editText and set name for new task
            etTask.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
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
                            // the case if the user needs to edit the name of task
                            editAlarm(task.getDueDate(), newDetail, ogDetail);
                            task.setTaskDetail(newDetail);
                            parsedData.set(pos, task.getTaskDetail() + "," + task.getDueDate());
                            writeTaskItems(); // update the persistence
                        } else if (etTask.getText().toString().length() > 0 && !addingAction && !parsedData.contains(newDetail + "," + task.getDueDate())) {
                            // the case if the user is adding name of task
                            task.setTaskDetail(newDetail);
                            parsedData.add(task.getTaskDetail() + "," + task.getDueDate());
                            setAlarm(task.getDueDate(), task.getTaskDetail());
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

        public void getDateInfo(View view) {
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

    // Gets the color id of that category item
    public int getColorId(int viewColor) {
        int id;
        switch (viewColor) {
            case 0:
                id = R.color.blue_14;
                break;
            case 1:
                id = R.color.blue_13;
                break;
            case 2:
                id = R.color.blue_12;
                break;
            case 3:
                id = R.color.blue_11;
                break;
            case 4:
                id = R.color.blue_10;
                break;
            case 5:
                id = R.color.blue_9;
                break;
            case 6:
                id = R.color.blue_8;
                break;
            case 7:
                id = R.color.blue_7;
                break;
            case 8:
                id = R.color.blue_6;
                break;
            case 9:
                id = R.color.blue_5;
                break;
            case 10:
                id = R.color.blue_4;
                break;
            case 11:
                id = R.color.blue_3;
                break;
            case 12:
                id = R.color.blue_2;
                break;
            case 13:
                id = R.color.blue_1;
                break;
            default:
                id = R.color.blue_0; // white
                break;
        }
        return id;
    }

    // to be called in adding a task (for both due date AND task detail/desc)(JUST ADDING THOUGH)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setAlarm(String dueDate, String taskDetail) {
        // For adding/setting a new alarm
        Calendar calendar = Calendar.getInstance();

        // getting the date in terms of MM, dd, yyyy for calendar
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;

        try {
            date = sdf.parse(dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int dayOfMonth = localDate.getDayOfMonth();

        // has the alarm go off at 7pm on user's set date
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.clear();
        calendar.set(year,month - 1,dayOfMonth,19,21); //19:00 is for 7pm

        // allows us to utilize broadcasting and alarms
        Intent myIntent = new Intent(this.context, MyAlarm.class);
        myIntent.putExtra("taskDetail", taskDetail);

        // takes a task and returns a unique id - utilized to keep track of different alarms (adding, deleting, editing)
        int id = taskDetail.hashCode();

        // for others to understand a bit better: https://medium.com/@architgupta690/creating-pending-intent-in-android-a-step-by-step-guide-74784ec60c9e
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, id, myIntent, 0);

        // sets up the actual alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this.context, "Alarm has been set", Toast.LENGTH_SHORT).show();
        Log.d("TaskAdapter", "Alarm set");
    }

    // to be called in removing a task and in checking off a task
    public void cancelAlarm(String taskDetail) {
        // For canceling an alarm

        // allows us to utilize broadcasting and alarms
        Intent myIntent = new Intent(this.context, MyAlarm.class);
        myIntent.putExtra("taskDetail", taskDetail);

        int id = taskDetail.hashCode();

        // for others to understand a bit better: https://medium.com/@architgupta690/creating-pending-intent-in-android-a-step-by-step-guide-74784ec60c9e
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, id, myIntent, 0);

        alarmManager.cancel(pendingIntent);
        Log.d("TaskAdapter", "Alarm canceled");
    }

    //to be called in editing a task (for both due date AND task detail/desc) (JUST EDITING THOUGH)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void editAlarm(String newDueDate, String newTaskDetail, String ogTaskDetail) {
        // For editing an alarm
        cancelAlarm(ogTaskDetail);
        setAlarm(newDueDate, newTaskDetail);
        Log.d("TaskAdapter", "Alarm edited");
    }
}