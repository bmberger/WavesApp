/*
 * Project: Waves
 *
 * Purpose: Interface that helps with moving an item up or down
 *
 * Reference(s): Aweys Abdul
 */

package com.example.waves_app;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}