/*
 * Project: Waves
 *
 * Purpose: To update the data for fun facts and turns a card when one is clicked to display answer.
 *
 * Reference(s): Aweys Abdullatif
 */

package com.example.waves_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.R;
import com.example.waves_app.model.FunFacts;

import java.util.List;

public class FunFactsAdapter extends RecyclerView.Adapter<FunFactsAdapter.ViewHolder> {

    private List<FunFacts> questionsList;

    public FunFactsAdapter(List<FunFacts> questionsList)
    {
        this.questionsList = questionsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facts, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FunFacts fact = questionsList.get(position);

        holder.tvQuestion.setText(fact.getTvshow());
        holder.tvAnswer.setText(fact.getTvshowAnswer());

        holder.cvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = holder.cvQuestion;

                // First quarter turn
                v.animate().withLayer()
                        .rotationY(90)
                        .setDuration(300)
                        .withEndAction(
                                new Runnable() {
                                    @Override public void run() {
                                        // Second quarter turn
                                        v.setRotationY(-90);
                                        v.animate().withLayer()
                                                .rotationY(0)
                                                .setDuration(300)
                                                .start();
                                        holder.cvQuestion.setVisibility(View.GONE);
                                        holder.cvAnswer.setVisibility(View.VISIBLE);
                                    }
                                }
                        ).start();
            }
        });

        holder.cvAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = holder.cvAnswer;

                // First quarter turn
                v.animate().withLayer()
                        .rotationY(90)
                        .setDuration(300)
                        .withEndAction(
                                new Runnable() {
                                    @Override public void run() {
                                        // Second quarter turn
                                        v.setRotationY(-90);
                                        v.animate().withLayer()
                                                .rotationY(0)
                                                .setDuration(300)
                                                .start();
                                        holder.cvAnswer.setVisibility(View.GONE);
                                        holder.cvQuestion.setVisibility(View.VISIBLE);
                                    }
                                }
                        ).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cvQuestion;
        public TextView tvQuestion;
        public CardView cvAnswer;
        public TextView tvAnswer;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cvQuestion = (CardView)itemView.findViewById(R.id.cvQuestion);
            this.tvQuestion = (TextView)itemView.findViewById(R.id.tvQuestion);
            this.cvAnswer = (CardView)itemView.findViewById(R.id.cvAnswer);
            this.tvAnswer = (TextView)itemView.findViewById(R.id.tvAnswer);
        }
    }
}