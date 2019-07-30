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
import androidx.cardview.widget.CardView;
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
    private List<Integer> taskCount;
    private int viewColor = 14; // used for coloring different items
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
                // if you are moving up list
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(categories, i, i + 1);
                    Collections.swap(parsedData, i, i + 1);
                }
                notifyItemRangeChanged(fromPosition, parsedData.size() - fromPosition);
            } else {
                // if you are moving down list
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(categories, i, i - 1);
                    Collections.swap(parsedData, i, i - 1);
                }
                notifyItemRangeChanged(toPosition, parsedData.size() - toPosition);
            }
            notifyItemMoved(fromPosition, toPosition);
            writeCatItems();
        }
        return true;
    }


    public void updateList(List<Category> Categories, List<String> ParsedData) {
        categories = Categories;
        parsedData = ParsedData;
        notifyDataSetChanged();
        writeCatItems();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
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

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        public void bind(final Category category) {
            int id = getColorId(getAdapterPosition());
            itemView.setBackgroundColor(context.getResources().getColor(id));

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
                    if (!hasFocus && categories.contains(category)) {
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

    // Gets the color id of that category item
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
}