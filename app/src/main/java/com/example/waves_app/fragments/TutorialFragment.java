/*
 * Project: Waves
 *
 * Purpose: Displays the tutorial with a viewPager
 *
 * Reference(s): Angela Liu
 */

package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.waves_app.R;
import com.example.waves_app.adapters.TutorialAdapter;

import java.util.ArrayList;
import java.util.List;

public class TutorialFragment extends Fragment {

    private ViewPager viewPager;
    private TutorialAdapter tutorialAdapter;
    private List<String> tutorials;
    private Button btnFinish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        getActivity().setTitle(""); // Required for setting action bar title
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Create the list of all the steps that will be displayed in the tutorial
        tutorials = new ArrayList<>();
        tutorials.add("Welcome to Waves, an app that promotes productivity and environmental cleanliness! Swipe to the right to learn more about how to use it.");
        tutorials.add("Swipe up and down on the screen to navigate between the home page, calendar, and fish tank.");
        tutorials.add("Press the back button to return to the home page.");
        tutorials.add("To add a new category or task, tap below the current list and we'll generate one for you!");
        tutorials.add("Swipe left on a category or task to delete the selected item.");
        tutorials.add("Swipe right on a task to mark it as complete");
        tutorials.add("Check your fish tank to see how much fish you've saved!");
        tutorials.add("If you forget where a task is located or when it's due, go to the search option and type in what you remember of the task.");
        tutorials.add("Open the share option to send a particular list of tasks to others by email!");
        tutorials.add("Tap into the facts option to learn fun facts about oceans.");
        tutorials.add("Look into our FAQ to find out how you can make an impact and our story.");

        // Create new adapter for tutorial steps
        tutorialAdapter = new TutorialAdapter(tutorials, getContext());

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Tutorial");

        // Locate the view and set the adapter
        viewPager = view.findViewById(R.id.vpTutorialCards);
        viewPager.setAdapter(tutorialAdapter);

        // Adds padding to the cardViews (distance from the borders)
        viewPager.setPadding(130, 380, 100, 0);

        // Sets background color of the viewPager
        viewPager.setBackgroundColor(getResources().getColor(R.color.blue_5_10_transparent));

        // Set onClickListener for the button
        btnFinish = (Button) view.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = new HomeFragment();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });
    }
}