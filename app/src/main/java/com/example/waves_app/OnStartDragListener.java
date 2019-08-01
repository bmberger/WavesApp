/*
 * Project: Waves
 *
 * Purpose: Interface for recognizing when an item is dragged
 *
 * Reference(s): Aweys Abdullatif
 */

package com.example.waves_app;

import androidx.recyclerview.widget.RecyclerView;

public interface OnStartDragListener {

    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
