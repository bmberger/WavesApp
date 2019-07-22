package com.example.waves_app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private TaskAdapter taskAdapter;
    private Drawable completedIcon;
    private Drawable deleteIcon;
    private final ColorDrawable background;

    public SwipeToDeleteCallback(TaskAdapter adapter) {
        // First parameter in super adds support for draggin the RecyclerView item up or down.
        // Second parameter tells the holder to pass information about left/right swipes.
        super(0, ItemTouchHelper.LEFT);
        taskAdapter = adapter;
        // TODO - come back to fix this part once the swipe is connected to another class or something
//        completedIcon = ContextCompat.getDrawable(, R.drawable.ic_completed);
//        deleteIcon = ContextCompat.getDrawable(, R.drawable.ic_delete_item);
        background = new ColorDrawable(Color.BLUE);
    }

    // This method is called when an item is swiped off the screen
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int direction) {
        int position = holder.getAdapterPosition();
        // TODO - the deleteItem method will be implemented later in adapter
        taskAdapter.deleteItem(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        int iconMargin = (itemView.getHeight() - completedIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - completedIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + completedIcon.getIntrinsicHeight();

        // Cover left, right, and no swipe cases
        // Sets bounds for background in each case and draws onto canvas
        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + completedIcon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            completedIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        completedIcon.draw(c);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Used for up and down movements
        return false;
    }
}