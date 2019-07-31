package com.example.waves_app.fragments;

import android.os.Bundle;
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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {
    // Declarations
    ArrayList<String> settings; // items data in strings (model)
    ArrayAdapter<String> settingsAdapter; // items that moves the model to the view (controller)
    ListView settingsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up architecture of list and list adapter
        settingsList = (ListView) view.findViewById(R.id.listItems);
        settings = new ArrayList<String>();

        String[] settingsPageOptions = new String[] { "Change Font Style", "Change Font Size"};
        settings.addAll(Arrays.asList(settingsPageOptions));

        settingsAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row_layout, settings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView and sets colors for gradient
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(getResources().getColor(R.color.blue_6));
                return view;
            }
        };

        settingsList.setAdapter(settingsAdapter);
        listViewListener();
    }

    // Listens for when someone clicks on an item in list
    private void listViewListener() {
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the item at position in the items ArrayList<String>
                String clickedOption = settings.get(position).toString();

                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment;

                // Switches to a different category dependent on user choice
                if (clickedOption.equals("My Categories")) {
                    fragment = new CategoryFragment();
                } else if (clickedOption.equals("FAQ")) {
                    fragment = new FAQFragment();
                } else {
                    fragment = new SettingsFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });
    }
}
