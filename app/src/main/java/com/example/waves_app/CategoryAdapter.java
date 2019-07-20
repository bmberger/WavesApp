package com.example.waves_app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.fragments.TasksFragment;
import com.example.waves_app.model.Category;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;
    private List<String> parsedData;
    int pos;

    // Data is passed into the constructor
    public CategoryAdapter(Context context, List<Category> data, List<String> parsedData) {
        this.categories = data;
        this.context = context;
        this.parsedData = parsedData;
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

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        // Member variable for view that will be set as row renders
        public EditText etCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etCategory = (EditText) itemView.findViewById(R.id.etNewCategory);

            // Attach a click listener to the entire row view
            itemView.setOnClickListener((View.OnClickListener)this);
            itemView.setOnLongClickListener((View.OnLongClickListener)this);
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

                            if (newName.length() > 0 && ogName != null) {
                                // case if the user needs to edit the category
                                category.setCategoryName(newName);
                                parsedData.set(pos, newName);
                                writeCatItems(); // update the persistence
                            } else {
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

        @Override
        public boolean onLongClick(View view) {
            // Create dialog popup to confirm deletion of task
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Delete this category?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    pos = getAdapterPosition();

                                    File toDelete = new File(context.getFilesDir(), categories.get(pos) + ".txt");
                                    toDelete.delete(); // removes the category file from allCategories.txt

                                    categories.remove(pos);
                                    parsedData.remove(pos);
                                    writeCatItems();
                                    notifyDataSetChanged();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            final AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }
    }
}