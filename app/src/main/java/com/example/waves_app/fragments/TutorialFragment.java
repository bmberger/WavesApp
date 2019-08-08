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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.waves_app.R;
import com.example.waves_app.adapters.TutorialAdapter;

import java.util.ArrayList;
import java.util.List;

public class TutorialFragment extends Fragment {

    private ViewPager viewPager;
    private TutorialAdapter tutorialAdapter;
    private List<String> tutorials;

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
        tutorials.add("Welcome to Waves, an app that promotes productivity and environmental cleanliness!");
        tutorials.add("Swipe up and down on the screen to navigate between the home page, calendar, and fish tank.");
        tutorials.add("Tap the add button to create a new category or task.");
        tutorials.add("Swipe to the left on a category or task to delete the selected item.");
        tutorials.add("Swipe to the right on a task to mark it as complete and save a fish.");
        tutorials.add("Check your calendar to see when your tasks are due.");
        tutorials.add("Check your fish tank to see how much fish you've saved!");

        // Create new adapter for tutorial steps
        tutorialAdapter = new TutorialAdapter(tutorials, getContext());

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Tutorial");
        toolbar.setTitleTextAppearance(getContext(), R.style.MyTitleTextApperance);

        // Locate the view and set the adapter
        viewPager = view.findViewById(R.id.vpTutorialCards);
        viewPager.setAdapter(tutorialAdapter);

        // Adds padding to the cardViews (distance from the borders)
        viewPager.setPadding(132, 400, 132, 0);

        // Sets background color of the viewPager
        viewPager.setBackgroundColor(getResources().getColor(R.color.blue_5_10_transparent));
    }
}