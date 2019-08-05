/*
 * Project: Waves
 *
 * Purpose: To update the data behind all the categories (adding, deleting, editing), moving items up and down,
 * swiping, and dynamically changing colors as the item is moved.
 *
 * Reference(s): Briana Berger, Angela Liu, Aweys Abdullatif
 */

package com.example.waves_app;

import android.content.Context;
import android.graphics.Color;
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
import java.util.Collections;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Category> categories;
    private Context context;
    private List<String> parsedData;
    int pos;

    // Variables to be used if user wants to undo deletion of category
    private Category recentlyDeletedCategory;
    private Category testRecentlyDeleted;
    private int deletedCategoryPosition;
    private List<String> associatedTasks;

    // Data is passed into the constructor
    public CategoryAdapter(Context context, List<Category> data, List<String> parsedData) {
        this.categories = data;
        this.context = context;
        this.parsedData = parsedData;
    }

    // Returns the file in which the data is stored
    private File getDataFile() {
        return new File(context.getFilesDir(), "allCategories.txt");
    }

    // Write the items to the filesystem
    private void writeCatItems() {
        try {
            // Save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), parsedData);
        } catch (IOException e) {
            // Print the error to the console
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
        EditText etCategoryName = holder.itemView.findViewById(R.id.etNewCategory);

        recentlyDeletedCategory = categories.get(pos);
        recentlyDeletedCategory.setCategoryName(etCategoryName.getText().toString());
        deletedCategoryPosition = pos;

        testRecentlyDeleted = categories.get(pos);
        testRecentlyDeleted.setCategoryName(etCategoryName.getText().toString());

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

    public void onItemDismiss(int position) {
        categories.remove(position);
        parsedData.remove(position);

        writeCatItems();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, parsedData.size() - position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < categories.size() && toPosition < categories.size()) {
            if (fromPosition < toPosition) {
                // If you are moving up list
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(categories, i, i + 1);
                    Collections.swap(parsedData, i, i + 1);
                }
                notifyItemRangeChanged(fromPosition, parsedData.size() - fromPosition); // Enables colors
            } else {
                // If you are moving down list
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(categories, i, i - 1);
                    Collections.swap(parsedData, i, i - 1);
                }
                notifyItemRangeChanged(toPosition, parsedData.size() - toPosition);  // Enables colors
            }
            notifyItemMoved(fromPosition, toPosition);  // Enables colors
            writeCatItems();
        }
        return true;
    }

    // Provides a direct reference to each of the views within a data item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {

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

        // Goes into the actual list of to-dos under that category
        @Override
        public void onClick(View view) {
            String catName = etCategory.getText().toString();

            if (wasAnythingTyped(catName)) {
                // Ensures that the category can't be empty when clicking into it
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                Fragment fragment = new TasksFragment();
                Bundle information = new Bundle();

                information.putString("catName", catName);
                fragment.setArguments(information);
                manager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            } else {
                Toast.makeText(context, "Enter a category name.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        public void setCount(Category category) {
            // Sets the count of how many tasks in each category
            if (ifFileExists(category) ) {
                count.setText(Integer.toString(getSizeOfCatList(category.getCategoryName())));
            } else {
                count.setText(Integer.toString(0));
            }
        }

        public void getPos(String input) {
            // Fixes the add on add issue that Android Studio doesn't account for
            for (int i = 0; i < parsedData.size(); i++) {
                String temp = parsedData.get(i);

                if (input.equals(temp)) {
                    pos = i;
                }
            }
        }

        public void bind(final Category category) {
            // Binds values to the itemView/specific category's view
            int id = getColorId(getAdapterPosition());
            itemView.setBackgroundColor(context.getResources().getColor(id));

            etCategory.setText(category.getCategoryName());
            setCount(category);

            // Get data from editText and set name for new category
            etCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String ogName = category.getCategoryName();
                    String newName = etCategory.getText().toString();

                    getPos(newName);

                    if (isValidInputAndFocusLost(category, hasFocus)) {
                        if (wasAnythingTyped(newName)) {
                            renameFile(ogName, newName);

                            if (toEdit(ogName, newName)) {
                                category.setCategoryName(newName);
                                if (deletedOtherWhileEditing(testRecentlyDeleted, pos)) {
                                    pos--;
                                    testRecentlyDeleted = null;
                                }
                                parsedData.set(pos, newName);
                                writeCatItems(); // Updates persistence
                            }
                        } else {
                            Toast.makeText(v.getContext(), "No category name has been entered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    // Gets the color id of that category item for the intensity gradient
    public int getColorId(int viewColor) {
        int id;
        switch (viewColor) {
            case 0:
                id = R.color.blue_14;
                break;
            case 1:
                id = R.color.blue_13;
                break;
            case 2:
                id = R.color.blue_12;
                break;
            case 3:
                id = R.color.blue_11;
                break;
            case 4:
                id = R.color.blue_10;
                break;
            case 5:
                id = R.color.blue_9;
                break;
            case 6:
                id = R.color.blue_8;
                break;
            case 7:
                id = R.color.blue_7;
                break;
            case 8:
                id = R.color.blue_6;
                break;
            case 9:
                id = R.color.blue_5;
                break;
            case 10:
                id = R.color.blue_4;
                break;
            case 11:
                id = R.color.blue_3;
                break;
            case 12:
                id = R.color.blue_2;
                break;
            case 13:
                id = R.color.blue_1;
                break;
            default:
                id = R.color.blue_0; // white
                break;
        }
        return id;
    }

    private File getTaskFile(String cat) {
        return new File(context.getFilesDir(), cat + ".txt");
    }

    public int getSizeOfCatList(String cat) {
        ArrayList<String> taskData;
        try {
            // Create the array of tasks
            taskData = new ArrayList<String>(FileUtils.readLines(getTaskFile(cat), Charset.defaultCharset()));
        } catch (IOException e) {
            taskData = new ArrayList<>();
            e.printStackTrace();
        }
        return taskData.size();
    }

    public void renameFile(String ogName, String newName) {
        File ogFile = new File(context.getFilesDir(), ogName + ".txt");
        File renameFile = new File(context.getFilesDir(), newName + ".txt");
        try {
            FileUtils.moveFile(ogFile, renameFile);
            ogFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Conditionals
    public boolean toEdit(String ogName, String newName) {
        // Tests if user is attempting to edit category
        return (!ogName.equals(newName) && !parsedData.contains(newName));
    }

    public boolean deletedOtherWhileEditing(Category testRecentlyDeleted, int pos) {
        // Tests if a task was deleted/completed while editing this category (which would mess up pos)
        return (testRecentlyDeleted != null && pos > 0);
    }

    public boolean isValidInputAndFocusLost(Category category, boolean hasFocus) {
        // Tests when focus is lost and checks that the text field has valid values.
        return (!hasFocus && categories.contains(category));
    }

    public boolean wasAnythingTyped(String input) {
        // Tests if anything was typed
        return (input.length() > 0);
    }

    public boolean ifFileExists(Category category) {
        return (new File(context.getFilesDir(), category.getCategoryName() + ".txt").exists());
    }
}