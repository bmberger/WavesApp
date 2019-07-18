package com.example.waves_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> categories;
    private Context context;

    // Data is passed into the constructor
    public RecyclerViewAdapter(Context context, List<String> data) {
        this.categories = data;
        this.context = context;
    }

    // Inflates the row layout from xml when needed and returns the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.recylerview_row, parent, false);
        return new ViewHolder(view);
    }

    // Binds data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = categories.get(position);
        holder.etNewItem.setText(item);
    }

    // Returns total count of items in the list
    @Override
    public int getItemCount() {
        return categories.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Member variable for view that will be set as row renders
        public EditText etNewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etNewItem = (EditText) itemView.findViewById(R.id.etNewItmm);
        }
    }
}


