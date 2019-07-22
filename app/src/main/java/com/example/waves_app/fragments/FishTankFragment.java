package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FishTankFragment extends Fragment {

    private int removedCount;
    private int displayCount;
    private int maxHeight;
    private int maxWidth;
    private TextView tvFishCount;
    private ConstraintLayout layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fish_tank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // TODO - use user persistence to find out how many tasks have been completed;
        // Calculate values for how many fish to display
        removedCount = readCompletedCount();
        displayCount = ((removedCount % 15) == 0) ? 15 : removedCount % 15;

        // Set layout width and height range
        maxHeight = 1300;
        maxWidth = 700;

        // Get the objects by id
        tvFishCount = (TextView) view.findViewById(R.id.tvFishCount);
        layout = (ConstraintLayout) view.findViewById(R.id.cLayout);

        // Set information for fishCount
        tvFishCount.setText(String.format("Fish Count: %d", displayCount));
      
        // Generate an image per count
        for (int i = 0; i < displayCount; i++) {
            // Prepare imageView for fish display
            ImageView fishImage = new ImageView(getContext());
            fishImage.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 200));

            int fishID = getRandomFishId();
            fishImage.setImageResource(fishID);
            fishImage.setX(new Random().nextInt(maxWidth));
            fishImage.setY(new Random().nextInt(maxHeight) + 100);
            layout.addView(fishImage);
        }
    }

    public int getRandomFishId() {
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