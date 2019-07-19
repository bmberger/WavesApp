package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;
import com.example.waves_app.CategoryAdapter;
import com.example.waves_app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private List<Category> categories;
    private CategoryAdapter categoryAdapter;
    private RecyclerView rvCategories;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvCategories = view.findViewById(R.id.categoriesList);

        // Create the data source, lists of categories
        categories = new ArrayList<>();

        // Create the categoryAdapter
        categoryAdapter = new CategoryAdapter(getContext(), categories);

        // Set the layout manager on the recycler view
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set the categoryAdapter on the recycler view
        rvCategories.setAdapter(categoryAdapter);

        // set on click listener on button
        button = view.findViewById(R.id.btnAdd);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Category category = new Category();
                categories.add(category);
                categoryAdapter.notifyDataSetChanged();
            }
        });
    }
}