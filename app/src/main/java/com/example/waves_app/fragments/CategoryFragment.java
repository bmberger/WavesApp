/*
 * Project: Waves
 *
 * Purpose: To display all of the user's categories and
 * listens for when a user adds/edits a category
 *
 * Reference(s): Briana Berger, Angela Liu, Aweys Abdullatif
 */

package com.example.waves_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.adapters.CategoryAdapter;
import com.example.waves_app.ItemMoveCallbackCategory;
import com.example.waves_app.interfaces.OnStartDragListener;
import com.example.waves_app.R;
import com.example.waves_app.SwipeToDeleteCategoryCallback;
import com.example.waves_app.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements OnStartDragListener {

    private List<Category> categories;
    private CategoryAdapter categoryAdapter;
    private RecyclerView rvCategories;
    private List<String> parsedData;
    private TextView tvSpaceHolder;

    // Returns the file in which the data is stored
    private File getDataFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
    }

    // Read the items from the file system
    public void readCategoryItems() {
        categories = new ArrayList<>();
        try {
            // Create the array using the content in the file
            parsedData = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));

            // Parses through and creates each category from string in parsedData
            for(String obj : parsedData) {
                Category tempCat = new Category();
                String name = obj;

                tempCat.setCategoryName(name);
                categories.add(tempCat);
            }

        } catch (IOException e) {
            // Print the error to the console
            e.printStackTrace();

            // Just load an empty list
            categories = new ArrayList<>();
            parsedData = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        getActivity().setTitle(""); // Required for setting action bar title

        // Notifies host activity that fragment has menu items
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvCategories = view.findViewById(R.id.categoriesList);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(getContext(), R.style.MyTitleTextApperance);
        toolbar.setTitle("Categories");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        FloatingActionButton factionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);

        readCategoryItems();

        // Create the categoryAdapter
        categoryAdapter = new CategoryAdapter(getContext(), categories, parsedData);

        // Set the layout manager on the recycler view
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new ItemMoveCallbackCategory(categoryAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvCategories);

        // Set the categoryAdapter on the recycler view
        rvCategories.setAdapter(categoryAdapter);

        // Attaching swipe capabilities to the recyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCategoryCallback(categoryAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(rvCategories);

        // Set on click listener to the textView
        factionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prevent user with adding multiple blank categories
                RecyclerView.ViewHolder lastCategory = rvCategories.findViewHolderForAdapterPosition(categoryAdapter.getItemCount() - 1);

                if (lastCategory != null) {
                    EditText etCatName = (EditText) lastCategory.itemView.findViewById(R.id.etNewCategory);
                    if (etCatName.getText().toString().length() > 0) {
                        addNewCategory();
                    } else {
                        Toast.makeText(getContext(), "Fill out the current blank category!", Toast.LENGTH_SHORT).show();
                    }
                } else { // There are currently no categories in the list so it's okay to add one
                    addNewCategory();
                }
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCategoryCallback(categoryAdapter, getContext()));
        itemTouchHelper.startDrag(viewHolder);
    }

    public void addNewCategory() {
        Category category = new Category();
        category.setCategoryName("");

        categories.add(category);
        parsedData.add(category.getCategoryName());

        categoryAdapter.notifyDataSetChanged();
        rvCategories.scrollToPosition(categoryAdapter.getItemCount() - 1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_clock, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miClock) {
            final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = new ProductivityFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
        }

        return super.onOptionsItemSelected(item);
    }
}