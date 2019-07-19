package com.example.waves_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.fragments.TasksFragment;
import com.example.waves_app.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;

    // Data is passed into the constructor
    public CategoryAdapter(Context context, List<Category> data) {
        this.categories = data;
        this.context = context;
    }

    // Inflates the row layout from xml when needed and returns the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    // Binds data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    // Returns total count of items in the list
    @Override
    public int getItemCount() {
        return categories.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member variable for view that will be set as row renders
        public EditText etCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etCategory = (EditText) itemView.findViewById(R.id.etNewCategory);

            // Attach a click listener to the entire row view
            itemView.setOnClickListener((View.OnClickListener)this);
        }

        @Override
        public void onClick(View view) {
            FragmentManager manager = ((FragmentActivity)context).getSupportFragmentManager();
            Fragment fragment = new TasksFragment();
            manager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        }

        public void bind(final Category category) {
            etCategory.setText(category.getCategoryName());

            // Get data from editText and set name for new category
            etCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // When focus is lost check that the text field has valid values.
                    if (!hasFocus) {
                        // If anything was typed
                        if (etCategory.getText().toString().length() > 0) {
                            category.setCategoryName(etCategory.getText().toString());
                        } else {
                            Toast.makeText(v.getContext(), "No category name has been entered!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }
}