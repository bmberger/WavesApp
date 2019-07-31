package com.example.waves_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class TutorialAdapter extends PagerAdapter {

    private List<String> tutorials;
    private LayoutInflater layoutInflater;
    private Context context;

    public TutorialAdapter(List<String> tutorials, Context context) {
        this.tutorials = tutorials;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tutorials.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    // Creates the view of each cardView and adds it to container to display
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = layoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_tutorial, container, false);

        // Find the textView and sets the tutorial steps
        TextView tvTutorial = (TextView) view.findViewById(R.id.tvTutorial);
        tvTutorial.setText(tutorials.get(position));

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}