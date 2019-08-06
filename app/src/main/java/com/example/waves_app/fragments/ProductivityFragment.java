/*
 * Project: Waves
 *
 * Purpose: Displays timer that runs in background to keep user on app or working on tasks for set time.
 *
 * Reference(s): Aweys Abdullatif
 */

package com.example.waves_app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class ProductivityFragment extends Fragment {

    private static final int Two_Seconds = 120000;

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button fiveMins;
    private Button twentyFiveMins;
    private Button viewCategories;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long START_TIME_IN_MILLIS;
    private long mTimeLeftInMillis;
    private long mEndTime;

    Handler handler;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productivity, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        view.setBackgroundColor(getResources().getColor(R.color.blue_0));
        getActivity().setTitle(""); // Required for setting action bar title
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTextViewCountDown = (TextView)view.findViewById(R.id.text_view_countdown);
        mButtonStartPause = (Button)view.findViewById(R.id.button_start_pause);
        mButtonReset = (Button)view.findViewById(R.id.button3);
        fiveMins = (Button)view.findViewById(R.id.fiveMins);
        twentyFiveMins = (Button)view.findViewById(R.id.twentyFiveMins);

        handler = new Handler();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Productivity");
        toolbar.setTitleTextAppearance(getContext(), R.style.MyTitleTextApperance);

        twentyFiveMins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // START_TIME_IN_MILLIS = 1500000;
                setTime(1500000);
                twentyFiveMins.setVisibility(View.INVISIBLE);
                fiveMins.setVisibility(View.INVISIBLE);
            }
        });

        fiveMins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // START_TIME_IN_MILLIS = 300000;
                setTime(300000);
                twentyFiveMins.setVisibility(View.INVISIBLE);
                fiveMins.setVisibility(View.INVISIBLE);
            }
        });


        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();

        // Set onClickListener for the button
        viewCategories = (Button) view.findViewById(R.id.btnCategories);
        viewCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = new CategoryFragment();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

//        // Sets background color of the RecyclerView
//        RecyclerView viewPager = view.findViewById(R.id.productivity);
//        viewPager.setBackgroundColor(getResources().getColor(R.color.blue_5_10_transparent));
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished == Two_Seconds){
                    Toast.makeText(getContext(), "Pomodora has two minutes left", Toast.LENGTH_SHORT).show();
                }
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Pomodora finished", Toast.LENGTH_SHORT).show();
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void setTime(long milliseconds) {
        START_TIME_IN_MILLIS = milliseconds;
        resetTimer();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
        twentyFiveMins.setVisibility(View.VISIBLE);
        fiveMins.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        twentyFiveMins.setVisibility(View.INVISIBLE);
        fiveMins.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void updateButtons() {
        if (mTimerRunning) {
            mButtonReset.setVisibility(View.INVISIBLE);
            twentyFiveMins.setVisibility(View.INVISIBLE);
            fiveMins.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
        } else {
            mButtonStartPause.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }

            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                mButtonReset.setVisibility(View.VISIBLE);
                twentyFiveMins.setVisibility(View.VISIBLE);
                fiveMins.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
                twentyFiveMins.setVisibility(View.INVISIBLE);
                fiveMins.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", START_TIME_IN_MILLIS);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);

        START_TIME_IN_MILLIS = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateButtons();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }
    }
}