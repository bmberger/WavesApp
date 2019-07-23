package com.example.waves_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.fragments.TasksFragment;
import com.example.waves_app.model.Category;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;
    private List<String> parsedData;
    private List<Integer> taskCount;
    int pos;

    // Variables to be used if user wants to undo deletion of category
    private Category recentlyDeletedCategory;
    private int deletedCategoryPosition;
    private List<String> associatedTasks;

    // Data is passed into the constructor
    public CategoryAdapter(Context context, List<Category> data, List<String> parsedData, List<Integer> taskCount) {
        this.categories = data;
        this.context = context;
        this.parsedData = parsedData;
        this.taskCount = taskCount;
    }

    // returns the file in which the data is stored
    private File getDataFile() {
        return new File(context.getFilesDir(), "allCategories.txt");
    }

    // write the items to the filesystem
    private void writeCatItems() {
        try {
            // save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), parsedData);
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
        }
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

    // Following four methods are used in part with swipe functionality of recyclerView
    public void deleteCategory(int pos, RecyclerView.ViewHolder holder) {
        recentlyDeletedCategory = categories.get(pos);
        deletedCategoryPosition = pos;

        // Delete the file with all the tasks within the selected category
        File toDelete = new File(context.getFilesDir(), categories.get(pos).getCategoryName() + ".txt");

        // Store the tasks within the selected category in case user wants to undo
        try {
            associatedTasks = new ArrayList<>(FileUtils.readLines(toDelete, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
            associatedTasks = new ArrayList<>();
        }

        if (categories.get(pos).getCategoryName() != null) {
            context.deleteFile(toDelete.getName());
            categories.remove(pos);
            parsedData.remove(pos);
            writeCatItems();
            notifyDataSetChanged();
        }

        Snackbar.make(holder.itemView, "Undo category deletion", Snackbar.LENGTH_LONG)
                .setAction("UNDO", myOnClickListenerDelete)
                .setActionTextColor(ContextCompat.getColor(context, R.color.blue_5))
                .show();
    }

    View.OnClickListener myOnClickListenerDelete = new View.OnClickListener(){
        public void onClick(View v){
            categories.add(deletedCategoryPosition, recentlyDeletedCategory);
            parsedData.add(deletedCategoryPosition, recentlyDeletedCategory.getCategoryName());
            writeCatItems();

            // Rewrite the tasks into file so that information is restored
            try {
                File undoFile = new File(context.getFilesDir(), recentlyDeletedCategory.getCategoryName() + ".txt");
                FileUtils.writeLines(undoFile, associatedTasks);
            } catch (IOException e) {
                e.printStackTrace();
            }

            notifyItemInserted(deletedCategoryPosition);
        }
    };

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member variable for view that will be set as row renders
        public EditText etCategory;
        public TextView count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etCategory = (EditText) itemView.findViewById(R.id.etNewCategory);
            count = (TextView) itemView.findViewById(R.id.taskCount);

            // Attach a click listener to the entire row view
            itemView.setOnClickListener((View.OnClickListener)this);
        }

        // goes into the actual task list
        @Override
        public void onClick(View view) {
            String catName = etCategory.getText().toString();

            FragmentManager manager = ((FragmentActivity)context).getSupportFragmentManager();
            Fragment fragment = new TasksFragment();
            Bundle information = new Bundle();

            information.putString("catName", catName);
            fragment.setArguments(information);
            manager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        }

        public void bind(final Category category) {
            etCategory.setText(category.getCategoryName());

            if (new File(context.getFilesDir(), category.getCategoryName() + ".txt").exists()) {
                count.setText(taskCount.get(getAdapterPosition()).toString());
            } else {
                count.setText(Integer.toString(0));
            }

            // Get data from editText and set name for new category
            etCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String ogName = category.getCategoryName();
                    String newName = etCategory.getText().toString();

                    // When focus is lost check that the text field has valid values.
                    if (!hasFocus) {
                        // If anything was typed
                        if (newName.length() > 0) {

                            File ogFile = new File(context.getFilesDir(), ogName + ".txt");
                            File renameFile = new File(context.getFilesDir(), etCategory.getText().toString() + ".txt");
                            try {
                                FileUtils.moveFile(ogFile, renameFile);
                                ogFile.delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (newName.length() > 0 && ogName != null && !ogName.equals(newName) && !parsedData.contains(newName)) {
                                // case if the user needs to edit the category
                                category.setCategoryName(newName);
                                parsedData.set(pos, newName);
                                writeCatItems(); // update the persistence
                            } else if (newName.length() > 0 && !parsedData.contains(newName)) {
                                // the case if the user is setting category
                                category.setCategoryName(newName);
                                parsedData.add(newName);
                                writeCatItems(); // update the persistence
                            }
                        } else {
                            Toast.makeText(v.getContext(), "No category name has been entered!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // fixes the add on add issue that Android Studio doesn't account for
                        for (int i = 0; i < parsedData.size(); i++) {
                            String temp = parsedData.get(i);

                            if (etCategory.getText().toString().equals(temp)) {
                                pos = i;
                            }
                        }
                    }
                }
            });
        }
    }
}