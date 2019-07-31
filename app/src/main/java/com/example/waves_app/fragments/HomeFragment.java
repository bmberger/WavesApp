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

public class HomeFragment extends Fragment {

    // Declarations
    ArrayList<String> items; // items data in strings (model)
    ArrayAdapter<String> itemsAdapter; // items that moves the model to the view (controller)
    ListView itemsList;
    List<String> categoryData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up architecture of list and list adapter
        itemsList = (ListView) view.findViewById(R.id.listItems);
        items = new ArrayList<String>();

        int categoryCount = categoryCount();

        String[] homePageOptions = new String[] { "My Categories (" + categoryCount + ")", "FAQ", "Tutorial", "Fun Facts", "Search", "Share" };
        items.addAll(Arrays.asList(homePageOptions));

        itemsAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row_layout, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView and sets colors for gradient
                View view = super.getView(position, convertView, parent);

                if (position == 0) {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_14));
                } else if (position == 1) {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_12));
                } else if (position == 2) {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_10));
                } else if (position == 3) {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_8));
                } else if (position == 4) {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_6));
                } else if (position == 5) {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_4));
                } else if (position == 6) {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_2));
                }
                return view;
            }
        };

        itemsList.setAdapter(itemsAdapter);
        listViewListener();
    }

    // Listens for when someone clicks on an item in list
    private void listViewListener() {
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the item at position in the items ArrayList<String>
                String clickedOption = items.get(position).toString();

                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment;

                // Switches to a different category dependent on user choice
                if (clickedOption.equals("My Categories (" + categoryCount() + ")")) {
                    fragment = new CategoryFragment();
                } else if (clickedOption.equals("FAQ")) {
                    fragment = new FAQFragment();
                } else if (clickedOption.equals("Tutorial")) {
                    fragment = new TutorialFragment();
                } else if (clickedOption.equals("Search")){
                    fragment = new SearchFragment();
                } else {
                    fragment = new HomeFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });
    }

    public int categoryCount() {
        readCategoryItems();
        return categoryData.size();
    }

    private File getCategoriesFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
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
}