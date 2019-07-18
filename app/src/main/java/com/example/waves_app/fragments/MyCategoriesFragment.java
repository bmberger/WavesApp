package com.example.waves_app.fragments;

import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;
import com.example.waves_app.RecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MyCategoriesFragment extends Fragment {

    List<String> categories;
    RecyclerViewAdapter adapter;
    RecyclerView categoriesList;
    Button button;
    EditText etNewItem;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoriesList = view.findViewById(R.id.categoriesList);


        // Create the data source, lists of categories
        categories = new ArrayList<>();
        // Create the adapter
        adapter = new RecyclerViewAdapter(getContext(), categories);

        // Set the layout manager on the recycler view
        categoriesList.setLayoutManager(new LinearLayoutManager(getContext()));
        // Set the adapter on the recycler view
        categoriesList.setAdapter(adapter);


        etNewItem = view.findViewById(R.id.etNewItem);

        // set on click listener on button
        button = view.findViewById(R.id.btnAdd);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String category = etNewItem.getText().toString();
                categories.add(category);
                adapter.notifyItemInserted(categories.size() - 1);
                etNewItem.setText("");
            }
        });

    }

}


