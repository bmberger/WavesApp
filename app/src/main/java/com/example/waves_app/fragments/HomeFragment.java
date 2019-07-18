package com.example.waves_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.waves_app.R;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    // Declarations
    ArrayList<String> items; // items data in strings (model)
    ArrayAdapter<String> itemsAdapter; // items that moves the model to the view (controller)
    ListView itemsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up architecture of list and list adapter
        itemsList = (ListView) view.findViewById(R.id.listItems);
        items = new ArrayList<String>();

        String[] homePageOptions = new String[] { "My Categories", "FAQ", "Tutorial", "Settings" };
        items.addAll(Arrays.asList(homePageOptions));

        itemsAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row_layout, items);
        itemsList.setAdapter(itemsAdapter);

        listViewListener();
    }

    // Listens for when someone clicks on an item in list
    private void listViewListener() {
        Log.i("MainActivity", "Setting up listener on list view");
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the item at position in the items ArrayList<String>
                String clickedOption = items.get(position).toString();

                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment;

                // Switches to a different category dependent on user choice
                if (clickedOption.equals("My Categories")) {
                    fragment = new CategoryFragment();
                } else if (clickedOption.equals("FAQ")) {
                    fragment = new FAQFragment();
                } else if (clickedOption.equals("Tutorial")) {
                    fragment = new TutorialFragment();
                } else {
                    fragment = new SettingsFragment();
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                Log.i("MainActivity", "Fragment switched to " + clickedOption);
            }
        });
    }
}