/*
 * Project: Waves
 *
 * Purpose: Populates the task fragment with all the data based on the category that the user is looking into
 *
 * Reference(s): Angela Liu, Briana Berger
 */

package com.example.waves_app.adapters;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.interfaces.ItemTouchHelperAdapter;
import com.example.waves_app.MyAlarm;
import com.example.waves_app.R;
import com.example.waves_app.model.Category;
import com.example.waves_app.model.Task;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private List<Task> mTasksList;
    private List<String> parsedData;
    private String catTasks; // Sets the category file name that contains all of the tasks
    private AlarmManager alarmManager;
    int completedTasks;
    int pos;

    // Variables to be used if user wants to undo delete/completion of task
    private Task recentlyConfiguredTask;
    private Task testDeletedTask;
    private int configuredTaskPosition;

    public TaskAdapter (Context context, List<Task> tasks, List<String> twoStrings, String catTasks) {
        this.context = context;
        this.mTasksList = tasks;
        this.parsedData = twoStrings;
        this.catTasks = catTasks;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); // Sets up alarm manager
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
            // Save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), parsedData);
        } catch (IOException e) {
            // Print the error to the console
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

    // Dismisses task from current position when it is moved from its original position
    public void onItemDismiss(int position) {
        mTasksList.remove(position);
        parsedData.remove(position);
        writeTaskItems();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, parsedData.size() - position);
    }

    // Handles the view when a task is being moved to a new position
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < mTasksList.size() && toPosition < mTasksList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mTasksList, i, i + 1);
                    Collections.swap(parsedData, i, i + 1);
                }
                notifyItemRangeChanged(fromPosition, parsedData.size() - fromPosition);
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mTasksList, i, i - 1);
                    Collections.swap(parsedData, i, i - 1);
                }
                notifyItemRangeChanged(toPosition, parsedData.size() - toPosition);
            }

            notifyItemMoved(fromPosition, toPosition);
            writeTaskItems();
        }

        return true;
    }

    // Creates one individual row in the recycler view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    // Following four methods are used in part with swipe functionality of recyclerView
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteTask(int pos, RecyclerView.ViewHolder holder) {
        EditText etTaskDetail = holder.itemView.findViewById(R.id.etTaskDescription);
        TextView tvDueDate = holder.itemView.findViewById(R.id.tvDueDate);

        recentlyConfiguredTask = mTasksList.get(pos);
        recentlyConfiguredTask.setTaskDetail(etTaskDetail.getText().toString());
        recentlyConfiguredTask.setDueDate(tvDueDate.getText().toString());
        configuredTaskPosition = pos;

        testDeletedTask = mTasksList.get(pos);
        testDeletedTask.setTaskDetail(etTaskDetail.getText().toString());
        testDeletedTask.setDueDate(tvDueDate.getText().toString());

        mTasksList.remove(pos);
        parsedData.remove(pos);

        if (needToDeleteAlarm(recentlyConfiguredTask)) {
            cancelAlarm(recentlyConfiguredTask.getTaskDetail());
        }

        writeTaskItems();
        notifyDataSetChanged();

        Snackbar.make(holder.itemView, "Undo task deletion", Snackbar.LENGTH_LONG)
                .setAction("UNDO", myOnClickListenerUndo)
                .setActionTextColor(ContextCompat.getColor(context, R.color.blue_5))
                .show();
    }

    View.OnClickListener myOnClickListenerUndo = new View.OnClickListener(){
        public void onClick(View v){
            mTasksList.add(configuredTaskPosition, recentlyConfiguredTask);
            parsedData.add(configuredTaskPosition, recentlyConfiguredTask.getTaskDetail() + "," + recentlyConfiguredTask.getDueDate());
            writeTaskItems();
            notifyItemInserted(configuredTaskPosition);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void markComplete(int pos, RecyclerView.ViewHolder holder) {
        recentlyConfiguredTask = mTasksList.get(pos);
        configuredTaskPosition = pos;
        testDeletedTask = mTasksList.get(pos);
        EditText etTaskDetail = holder.itemView.findViewById(R.id.etTaskDescription);

        if (isValidTask(recentlyConfiguredTask, etTaskDetail)) {
            mTasksList.remove(pos);
            parsedData.remove(pos);

            // Set the count for completedTasks
            readCompletedCount();
            completedTasks += 1;

            writeTaskItems();
            writeCompletedCount();

            if (needToDeleteAlarm(recentlyConfiguredTask)) {
                cancelAlarm(recentlyConfiguredTask.getTaskDetail());
            }
            notifyDataSetChanged();

            // Create popup when task is marked complete
            Dialog ad_dialog = new Dialog(context);
            ad_dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            ad_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ad_dialog.setCancelable(true);
            ad_dialog.setContentView(R.layout.ic_popup);

            // Set congrats words for message on pop-up
            TextView congratsWords = (TextView) ad_dialog.findViewById(R.id.tvSelectStatement);
            congratsWords.setText("You have now saved " + completedTasks + " fish. Make sure to see all the fish you saved by going to your fish tank!");

            // Set the fish image for pop-up
            ImageView fishImage = (ImageView) ad_dialog.findViewById(R.id.ivFishView);
            int fishID = getRandomFishId();
            fishImage.setImageResource(fishID);

            // Displays the popup to the screen
            ad_dialog.show();
        } else {
            // If you had an empty task with deadline, we don't want it to count towards fish tank
            deleteTask(pos, holder);
        }
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
        private AlarmManager alarmManager;

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

                    if (isEditingDate(task, etTask)) {
                        pos = getAdapterPosition();
                        setTaskDate(task, dueDate);
                    } else {
                        // Due date is being set first
                        pos = getAdapterPosition();
                        task.setTaskDetail(etTask.getText().toString());
                        setTaskDate(task, dueDate);
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

                    // Fixes the add on add issue that Android Studio doesn't account for
                    for (int i = 0; i < parsedData.size(); i++) {
                        String temp = parsedData.get(i);
                        int delimiter = temp.lastIndexOf(",");

                        if (newDetail.equals(temp.substring(0, delimiter))) {
                            pos = i;
                        }
                    }

                    // When focus is lost check that the text field has valid values.
                    if (!hasFocus && mTasksList.contains(task)) {
                        if (isSettingTextWithDateSet(ogDetail, task)) {
                            setTaskText(pos, task, newDetail, ogDetail);
                        } else if (isEditingTaskDetail(newDetail, ogDetail)) {
                            if (deletedOtherWhileEditing(testDeletedTask, pos)) {
                                testDeletedTask = null;
                                pos--;
                            }
                            setTaskText(pos, task, newDetail, ogDetail);
                        }
                    }
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setTaskText(int pos, Task task, String newDetail, String ogDetail) {
            // Sets the text of a task
            changeAlarmText(task, newDetail, ogDetail);
            task.setTaskDetail(newDetail);
            parsedData.set(pos, task.getTaskDetail() + "," + task.getDueDate());
            writeTaskItems(); // Update the persistence
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setTaskDate(Task task, String dueDate) {
            // Sets the date of a task
            task.setDueDate(dueDate);
            changeAlarmDate(task, dueDate);
            parsedData.set(pos, task.getTaskDetail() + "," + task.getDueDate());
            writeTaskItems();
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
                id = R.color.blue_12;
                break;
            case 2:
                id = R.color.blue_10;
                break;
            case 3:
                id = R.color.blue_8;
                break;
            case 4:
                id = R.color.blue_6;
                break;
            case 5:
                id = R.color.blue_4;
                break;
            case 6:
                id = R.color.blue_2;
                break;
            default:
                id = R.color.blue_0; // white
                break;
        }
        return id;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate getLocalDate(String dueDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;

        try {
            date = sdf.parse(dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate;
    }

    public long getDiffDays(Calendar calendarDeadline, Calendar calendarEarly) {
        // Gets the difference between current date and deadline
        Date deadlineDate = calendarDeadline.getTime();
        Date currentDate = calendarEarly.getTime();
        long deadlineTime = deadlineDate.getTime();
        long currentTime = currentDate.getTime();
        long diffTime = currentTime - deadlineTime;
        long diffDays = abs(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEarlyAlarm(Calendar calendarEarly, String taskDetail, int month, int dayOfMonth, int year) {
        // Early point alarm - has alarm go off at 7pm five days before its due
        calendarEarly.setTimeInMillis(System.currentTimeMillis());
        calendarEarly.clear();

        int newDay = dayOfMonth - 2;
        if (month == 1 && newDay < 0) {
            // Adjusts for case if January (underflow)
            calendarEarly.set(year - 1,12, 31 + newDay - 1,19,00);
        } else if (newDay < 0) {
            // Adjusts for case if beginning of month (underflow)
            YearMonth yearMonthObject = YearMonth.of(year, month - 1); // gets previous month - aka 1 is January here
            int daysInMonth = yearMonthObject.lengthOfMonth();
            calendarEarly.set(year,month - 2, daysInMonth + newDay - 1,19,00);
        } else {
            // For regular case
            calendarEarly.set(year,month - 1, newDay,19,00); //19:00 is for 7pm
        }

        Intent earlyIntent = new Intent(this.context, MyAlarm.class);
        earlyIntent.putExtra("taskDetail", taskDetail);
        earlyIntent.putExtra("earlyPoint", "true"); // purposed to signal in MyAlarm if this item is due today or it is halfway

        int halfwayId = taskDetail.hashCode() + 1;
        PendingIntent pendingIntentHalfway = PendingIntent.getBroadcast(this.context, halfwayId, earlyIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarEarly.getTimeInMillis(), pendingIntentHalfway);
    }

    // To be called in adding a task (for both due date AND task detail/desc)(JUST ADDING THOUGH)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setAlarm(String dueDate, String taskDetail) {
        // For adding/setting a new alarm
        Calendar calendarDeadline = Calendar.getInstance();
        Calendar calendarEarly = Calendar.getInstance();

        // Getting the date in terms of MM, dd, yyyy for calendar
        LocalDate localDate = getLocalDate(dueDate);
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int dayOfMonth = localDate.getDayOfMonth();

        // Deadline alarm - has the alarm go off at 7pm on user's set date
        calendarDeadline.setTimeInMillis(System.currentTimeMillis());
        calendarDeadline.clear();
        calendarDeadline.set(year,month - 1, dayOfMonth,19,00); //19:00 is for 7pm
        long diffDays = getDiffDays(calendarDeadline, calendarEarly);

        // Allows us to utilize broadcasting and alarms
        Intent deadlineIntent = new Intent(this.context, MyAlarm.class);
        deadlineIntent.putExtra("taskDetail", taskDetail);
        deadlineIntent.putExtra("earlyPoint", "false");

        // Takes a task and returns a unique id - utilized to keep track of different alarms (adding, deleting, editing)
        int deadlineId = taskDetail.hashCode();

        // The early point alarm only occurs if the deadline is more than two days away
        if (diffDays > 2) {
            setEarlyAlarm(calendarEarly, taskDetail, month, dayOfMonth, year);
        }

        // For others to understand a bit better: https://medium.com/@architgupta690/creating-pending-intent-in-android-a-step-by-step-guide-74784ec60c9e
        PendingIntent pendingIntentDeadline = PendingIntent.getBroadcast(this.context, deadlineId, deadlineIntent, 0);

        // Sets up the actual alarm - for deadline and halfway point
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarDeadline.getTimeInMillis(), pendingIntentDeadline);
        Log.d("TaskAdapter", "The deadline alarm set");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // Utilized to know when to set, edit, cancel alarm
    public int dueDateComparedToCurrent(String dueDate) {
        Calendar currentCal = Calendar.getInstance();
        Calendar dueDateCal = Calendar.getInstance();

        LocalDate localDate = getLocalDate(dueDate)
        int yearDeadline = localDate.getYear();
        int monthDeadline = localDate.getMonthValue();
        int dayOfMonthDeadline = localDate.getDayOfMonth();

        int yearCurrent = Calendar.getInstance().get(Calendar.YEAR);
        int monthCurrent = Calendar.getInstance().get(Calendar.MONTH);
        int dayOfMonthCurrent = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        dueDateCal.setTimeInMillis(System.currentTimeMillis());
        currentCal.setTimeInMillis(System.currentTimeMillis());

        dueDateCal.clear();
        currentCal.clear();

        dueDateCal.set(yearDeadline,monthDeadline - 1, dayOfMonthDeadline,19,00); //19:00 is for 7pm
        currentCal.set(yearCurrent, monthCurrent, dayOfMonthCurrent, 19, 00);

        // Early point alarm math - has alarm go off at 7pm two days before its due
        Date deadlineDate = dueDateCal.getTime();
        Date currentDate = currentCal.getTime();

        // Only set, cancel, or edit alarm is this function returns > 0
        return deadlineDate.compareTo(currentDate);
    }

    // To be called in removing a task and in checking off a task
    public void cancelAlarm(String taskDetail) {
        // For canceling an alarm

        // Allows us to utilize broadcasting and alarms
        Intent myIntent = new Intent(this.context, MyAlarm.class);
        myIntent.putExtra("taskDetail", taskDetail);

        int id = taskDetail.hashCode();

        // For others to understand a bit better: https://medium.com/@architgupta690/creating-pending-intent-in-android-a-step-by-step-guide-74784ec60c9e
        PendingIntent pendingIntentDeadline = PendingIntent.getBroadcast(this.context, id, myIntent, 0);
        PendingIntent pendingIntentHalfway = PendingIntent.getBroadcast(this.context, id + 1, myIntent, 0);

        alarmManager.cancel(pendingIntentDeadline);
        alarmManager.cancel(pendingIntentHalfway);
    }

    // To be called in editing a task (for both due date AND task detail/desc)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void editAlarm(String newDueDate, String newTaskDetail, String ogTaskDetail) {
        // For editing an alarm
        cancelAlarm(ogTaskDetail);
        setAlarm(newDueDate, newTaskDetail);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeAlarmDate(Task task, String dueDate) {
        // Utilized when a user edits the date (needs to edit the alarm)
        if (isChangingDateToFuture(task)) {
            editAlarm(dueDate, task.getTaskDetail(), task.getTaskDetail());
        } else if (isChangingDateToPast(task)) {
            cancelAlarm(task.getTaskDetail());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeAlarmText(Task task, String newDetail, String ogDetail) {
        // Utilized when a user edits the date (needs to edit the alarm)
        if (isChangingDateToFuture(task)) {
            editAlarm(task.getDueDate(), newDetail, ogDetail);
        } else if (isChangingDateToPast(task)) {
            cancelAlarm(ogDetail);
            cancelAlarm(newDetail);
        }
    }

    // Conditionals
    public boolean isValidTask(Task recentlyConfiguredTask, EditText etTaskDetail) {
        // Tests if the task is an actual task
        return (recentlyConfiguredTask.getTaskDetail().length() > 0 || etTaskDetail.getText().toString().length() > 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean needToDeleteAlarm(Task recentlyConfiguredTask) {
        // Tests if this task has an alarm set that needs deleting
        return (!recentlyConfiguredTask.getDueDate().equals("set due date") && dueDateComparedToCurrent(recentlyConfiguredTask.getDueDate()) > 0);
    }

    public boolean isEditingDate(Task task, EditText etTask) {
        // Tests if user is editing a date
        return (etTask.getText().toString().length() > 0 && task.getDueDate() != null);
    }

    public boolean isEditingTaskDetail(String newDetail, String ogDetail){
        // Tests if editing task detail
        return (newDetail.length() > 0 && !ogDetail.equals(newDetail));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isChangingDateToFuture(Task task) {
        // Tests if user is editing a date to the future
        return (!task.getDueDate().equals("set due date") && dueDateComparedToCurrent(task.getDueDate()) > 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isChangingDateToPast(Task task) {
        // Tests if user is editing a date to the past
        return (!task.getDueDate().equals("set due date") && dueDateComparedToCurrent(task.getDueDate()) <= 0);
    }

    public boolean deletedOtherWhileEditing(Task testRecentlyDeleted, int pos) {
        // Tests if a task was deleted/completed while editing this task (which would mess up pos)
        return (testDeletedTask != null && pos > 0);
    }

    public boolean isSettingTextWithDateSet(String ogDetail, Task task) {
        // Tests if date is set first while setting text
        return (ogDetail.equals("") && !task.getDueDate().equals("set due date"));
    }
}