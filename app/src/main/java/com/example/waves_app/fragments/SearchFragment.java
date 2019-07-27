package com.example.waves_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;

import java.util.Map;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private ImageButton ibSearch;
    private RecyclerView rvSearchTasks;
    private Map searchInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.etSearch = (EditText) view.findViewById(R.id.etSearch);
        this.ibSearch = (ImageButton) view.findViewById(R.id.ibSearch);
        this.rvSearchTasks = (RecyclerView) view.findViewById(R.id.rvSearchTasks);

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etSearch.getText().toString().length() > 0) {
                    populateView(view);
                } else {
                    // Nothing was searched
                    Toast.makeText(getContext(), "Enter something into search bar!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void populateView(View view) {
        Log.d("hi", "here!");
    }
}