package com.example.waves_app.fragments;

import android.content.Context;
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
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private TextView tvMonthYear;

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
}