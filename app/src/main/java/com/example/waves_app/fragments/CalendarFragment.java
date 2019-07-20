package com.example.waves_app.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;
import com.example.waves_app.model.Task;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private TextView tvMonthYear;

    private List<String> categoryData;
    private List<Task> taskEvents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMonthYear = (TextView) getActivity().findViewById(R.id.tvMonthYear);
        compactCalendar = (CompactCalendarView) getActivity().findViewById(R.id.calendarView);
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY);

        // Initially sets the monthYear textView with information
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        onMonthScroll(c.getTime());

        loadEvents();

        // Add the tasks as events into calendar
        for (Task task : taskEvents) {
            String dueDate = task.getDueDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date date = null;

            try {
                date = sdf.parse(dueDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = date.getTime();

            Event ev1 = new Event(Color.BLUE, millis, task.getTaskDetail());
            compactCalendar.addEvent(ev1);
        }

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getActivity().getApplicationContext();

                List<Event> events = compactCalendar.getEvents(dateClicked);
                String remindersOfDay = "";
                if (events != null) {
                    for (int i = 0; i < events.size(); i++) {
                        String temp;
                        if (i == (events.size() - 1)) {
                            temp = events.get(i).getData().toString();
                        } else {
                            temp = events.get(i).getData().toString() + "%n";
                        }
                        remindersOfDay += temp;
                    }
                    if (remindersOfDay.length() != 0) {
                        Toast.makeText(context, String.format(remindersOfDay), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // CODE ADDED FOR OUR APP
                // https://gist.github.com/skooltch84/b7cb5361a09b687b4b9f434ddc33d2c6
                // THIS IS FOR THE MONTH AND YEAR AT TOP OF CALENDAR
                tvMonthYear.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    public void onMonthScroll(Date firstDayOfNewMonth) {
        tvMonthYear.setText(dateFormatMonth.format(firstDayOfNewMonth));
    }

    private void loadEvents() {
        readCategoryItems();
        taskEvents = new ArrayList<>();

        // Go through all categories and search for their tasks
        for (String categoryName : categoryData) {
            String categoryFile = categoryName + ".txt";

            try {
                List<String> tasks = new ArrayList<>(FileUtils.readLines(getTasksFile(categoryFile), Charset.defaultCharset()));

                // Load any tasks into the taskEvent list so that we can make events later
                for (String task : tasks) {
                    Task tEvent = new Task();

                    int delimiter = task.indexOf(",");
                    String taskDetail = task.substring(0, delimiter);
                    String dueDate = task.substring(delimiter + 1);

                    tEvent.setTaskDetail(taskDetail);
                    tEvent.setDueDate(dueDate);

                    taskEvents.add(tEvent);
                }
            } catch (IOException e) {
                taskEvents = new ArrayList<>();
                e.printStackTrace();
            }
        }
    }

    public void readCategoryItems() {
        try {
            // create the array of categories
            categoryData = new ArrayList<String>(FileUtils.readLines(getCategoriesFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            categoryData = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private File getCategoriesFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
    }

    private File getTasksFile(String category) {
        return new File(getContext().getFilesDir(), category);
    }
}