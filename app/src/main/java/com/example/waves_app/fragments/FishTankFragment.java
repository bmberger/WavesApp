package com.example.waves_app.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.waves_app.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FishTankFragment extends Fragment {

    private int removedCount;
    private int displayCount;
    private int maxWidth;
    private int maxHeight;
    private TextView tvTankCount;
    private TextView tvTotalCount;
    private ConstraintLayout layout;

    private int midWidth;
    private int midHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fish_tank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Calculate values for how many fish to display
        removedCount = readCompletedCount();
        displayCount = ((removedCount % 15) == 0) ? 15 : removedCount % 15;
        if (removedCount == 0) {
            displayCount = 0; // For the base case, when user first installs app
        }

        // Set layout width and height range
        Display display = view.getDisplay();
        maxWidth = display.getWidth() - 300;
        maxHeight = display.getHeight() - 400;
        midWidth = maxWidth / 2;
        midHeight = maxHeight / 2;

        // Get the objects by id
        tvTankCount = (TextView) view.findViewById(R.id.tvTankCount);
        tvTotalCount = (TextView) view.findViewById(R.id.tvTotalCount);
        layout = (ConstraintLayout) view.findViewById(R.id.cLayout);

        // Set information for tankCount and totalCount
        tvTankCount.setText(String.format("Tank Count: %d", displayCount));
        tvTotalCount.setText(String.format(" Total Count: %d ", removedCount));

        // Generate an image per count
        for (int i = 0; i < displayCount; i++) {
            // Prepare imageView for fish display
            ImageView fishImage = new ImageView(getContext());
            fishImage.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 200));

            // Get the fish image and add it into the view
            int fishID = getRandomFishId();
            fishImage.setImageResource(fishID);

            ObjectAnimator animator = ObjectAnimator.ofFloat(fishImage, View.X, View.Y, generatePath());
            animator.setDuration(generateTime());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();

            // Displays the image onto screen
            layout.addView(fishImage);
        }

        tvTotalCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move us into ocean view fragment
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = new OceanViewFragment();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });
    }

    private Path generatePath() {
        int left = new Random().nextInt(midWidth);
        int top = new Random().nextInt(midHeight);
        int right = new Random().nextInt(maxWidth - midWidth + 1) + midWidth;
        int bottom = new Random().nextInt(maxHeight - midHeight + 1) + midHeight;
        int startAngle = new Random().nextInt(360);

        Path path = new Path();
        path.arcTo(left, top, right, bottom, startAngle, 359 * generateDirection(), true);
        return path;
    }

    private int generateTime() {
        // Generate random number between 20 and 30 inclusive
        int time = new Random().nextInt(11) + 20;
        return time * 1000;
    }

    private int generateDirection() {
        int random = new Random().nextInt(2);
        if (random == 0) {
            return 1;
        }

        return -1;
    }

    private int getRandomFishId() {
        // Generates random integer between 0 and 14 inclusive
        int random = new Random().nextInt(15);
        return getResources().getIdentifier("fish_" + random, "drawable", getContext().getPackageName());
    }

    // Returns the file in which the completedTask count is stored
    private File getCompletedTaskCountFile() { return new File(getContext().getFilesDir(), "completedTaskCount"); }

    // Set the completedTasks count by reading what's currently in the file
    private int readCompletedCount() {
        try {
            String count = FileUtils.readFileToString(getCompletedTaskCountFile(), (String) null);
            return Integer.parseInt(count);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}