/*
 * Project: Waves
 *
 * Purpose: Handles deleting a task or marking it as complete when user swipes on one
 *
 * Reference(s): Angela Liu
 */

package com.example.waves_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.adapters.TaskAdapter;

public class SwipeToDeleteTaskCallback extends ItemTouchHelper.SimpleCallback {

    private TaskAdapter taskAdapter;
    private Drawable completedIcon;
    private Drawable deleteIcon;
    private ColorDrawable deleteBackground;
    private ColorDrawable completeBackground;

    public SwipeToDeleteTaskCallback(TaskAdapter adapter, Context context) {
        // First parameter in super adds support for draggin the RecyclerView item up or down.
        // Second parameter tells the holder to pass information about left/right swipes.
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        taskAdapter = adapter;
        completedIcon = ContextCompat.getDrawable(context, R.drawable.ic_completed);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_item);
        deleteBackground = new ColorDrawable(context.getResources().getColor(R.color.red_transparent));
        completeBackground = new ColorDrawable(context.getResources().getColor(R.color.green_transparent));
    }

    // This method is called when an item is swiped off the screen
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int direction) {
        int position = holder.getAdapterPosition();

        // Check which direction user swiped
        if (direction == 4) { // Delete task
            taskAdapter.deleteTask(position, holder);
        } else { // Complete task
            taskAdapter.markComplete(position, holder);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                    actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - completedIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - completedIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + completedIcon.getIntrinsicHeight();

        // Cover left, right, and no swipe cases
        // Sets bounds for background in each case and draws onto canvas
        if (dX > 0) { // Swiping to the right aka checking off
            int iconLeft = itemView.getLeft() + iconMargin + completedIcon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;

            completedIcon.setBounds(iconRight, iconTop, iconLeft, iconBottom);
            completeBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());

            completeBackground.draw(c);
            completedIcon.draw(c);
        } else if (dX < 0) { // Swiping to the left aka deleting
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            deleteBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());

            deleteBackground.draw(c);
            deleteIcon.draw(c);
        } else { // View is unSwiped
            completeBackground.setBounds(0, 0, 0, 0);
            deleteBackground.setBounds(0, 0, 0, 0);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Used for up and down movements
        return false;
    }
}